package kr.syszone.t20.bmts.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.gmail.webos21.http.HttpHelper;
import com.gmail.webos21.http.HttpMethod;
import com.gmail.webos21.http.HttpResult;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import kr.syszone.t20.bmts.B2CSApp;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.model.MovData;
import kr.syszone.t20.bmts.model.MovRequest;
import kr.syszone.t20.bmts.model.PicData;
import kr.syszone.t20.bmts.model.PicRequest;
import kr.syszone.t20.bmts.settings.SettingsConst;

public class FtpUploadTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "FtpUploadTask";
    private static final SimpleDateFormat SDF_FNAME = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    private Activity act;
    private File xferFile;
    private GpsData gpsData;
    private boolean bMovie;

    private String svrAddress;
    private Integer svrPort;
    private String reqUrl;
    private String tid;

    private String suffix;
    private String storeName;
    private String storePath;

    public FtpUploadTask(Activity act, File f, GpsData gps) {
        this.act = act;
        this.xferFile = f;
        this.gpsData = gps;

        if (f == null || !f.exists()) {
            throw new IllegalArgumentException("File not exists : " + f);
        }

        String fname = f.getName();
        this.suffix = fname.substring(fname.lastIndexOf('.'));
        this.bMovie = (!".jpg".equals(suffix.toLowerCase()));

        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(act);
        this.tid = shPref.getString(SettingsConst.KEY_PHONE_NUMBER, null);
        svrAddress = shPref.getString(SettingsConst.KEY_SERVER_ADDRESS, null);
        String sp = shPref.getString(SettingsConst.KEY_SERVER_PORT, null);
        svrPort = (sp == null) ? -1 : Integer.parseInt(sp);
        if (svrAddress == null || svrPort < 0) {
            this.reqUrl = null;
        } else {
            if (svrPort == 443) {
                this.reqUrl = NetConst.URI_HTTPS;
            } else {
                this.reqUrl = NetConst.URI_HTTP;
            }
            this.reqUrl += "://" + svrAddress + ":" + svrPort + "/" + NetConst.URI_VER + "/";
            if (bMovie) {
                this.reqUrl += NetConst.URI_MOVIE;
            } else {
                this.reqUrl += NetConst.URI_PICTURE;
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!ftpTransfer()) {
            String msg = (bMovie) ? "[FtpUploadTask] 영상 전송 실패!" : "[FtpUploadTask] 사진 전송 실패!";
            showToast(msg);
            return null;
        }
        if (postRequest()) {
            String msg = (bMovie) ? "[FtpUploadTask] 영상 등록 성공!" : "[FtpUploadTask] 사진 등록 성공!";
            showToast(msg);
        }

        return null;
    }

    private boolean ftpTransfer() {
        if (reqUrl == null || tid == null || xferFile == null || gpsData == null) {
            showToast("[FtpUploadTask] 필수 정보 부족!!!");
            return false;
        }

        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect("webcom.hazzero.co.kr", 21);  // FIXME : add settings
            ftpClient.login("hazzero", "ftphazzero");
            ftpClient.changeWorkingDirectory("BMTS");
            boolean ret = ftpClient.changeWorkingDirectory(tid);
            if (!ret) {
                ftpClient.mkd(tid);
                ret = ftpClient.changeWorkingDirectory(tid);
                if (!ret) {
                    showToast("[FtpUploadTask] FTP 폴더 생성 실패 : " + tid);
                    return false;
                }
            }
            storePath = "http://webcom.hazzero.co.kr/ftp/BMTS/" + tid;
            if (bMovie) {
                ret = ftpClient.changeWorkingDirectory("mov");
                if (!ret) {
                    ftpClient.mkd("mov");
                    ret = ftpClient.changeWorkingDirectory("mov");
                    if (!ret) {
                        showToast("[FtpUploadTask] FTP 폴더 생성 실패 : mov");
                        return false;
                    }
                }
                storeName = "M_" + SDF_FNAME.format(new Date()) + suffix;
                storePath += "/mov/" + storeName;
            } else {
                ret = ftpClient.changeWorkingDirectory("pic");
                if (!ret) {
                    ftpClient.mkd("pic");
                    ret = ftpClient.changeWorkingDirectory("pic");
                    if (!ret) {
                        showToast("[FtpUploadTask] FTP 폴더 생성 실패 : pic");
                        return false;
                    }
                }
                storeName = "P_" + SDF_FNAME.format(new Date()) + suffix;
                storePath += "/pic/" + storeName;
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream(xferFile));
            ftpClient.enterLocalPassiveMode();
            ftpClient.storeFile(storeName, buffIn);
            buffIn.close();
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            storeName = null;
            storePath = null;
            return false;
        }
        return true;
    }

    private boolean postRequest() {
        if (reqUrl == null || tid == null || xferFile == null || gpsData == null) {
            showToast("[FtpUploadTask] 필수 정보 부족!!!");
            return false;
        }

        B2CSApp app = (B2CSApp) act.getApplication();
        byte[] aesKey = app.getAesKey();
        String accessToken = app.getAccessToken();

        if (aesKey == null || accessToken == null) {
            showToast("[FtpUploadTask] 로그인 정보가 없습니다!!!");
            return false;
        }

        String plainBody = null;
        if (bMovie) {
            MovData md = new MovData(null, System.currentTimeMillis(), storePath, gpsData);
            MovRequest mr = new MovRequest();
            mr.setTid(tid);
            mr.setMovie(md);
            plainBody = mr.toJSON(false);
        } else {
            PicData pd = new PicData(null, System.currentTimeMillis(), storePath, gpsData);
            PicRequest pr = new PicRequest();
            pr.setTid(tid);
            pr.setPicture(pd);
            plainBody = pr.toJSON(false);
        }

        String reqBody = ClientHelper.encryptRequest(aesKey, plainBody);

        Map<String, String> reqHeaders = new HashMap<String, String>();
        reqHeaders.put("Content-Length", Integer.toString(reqBody.length()));
        reqHeaders.put("Content-Type", "application/json;charset=utf-8");
        reqHeaders.put("Access-Token", accessToken);

        if (NetConst.DEBUG) {
            Log.d(TAG, "request.URL = " + reqUrl);
            Log.d(TAG, "request.Headers = " + reqHeaders);
            Log.d(TAG, "request.Body = " + plainBody);
            Log.d(TAG, "request.BodyEnc = " + reqBody);
        }

        SSLContext sslCtx = (svrPort == 443) ? ClientHelper.getSSLContext(act) : null;
        HostnameVerifier hv = (svrPort == 443) ? ClientHelper.getHostnameVerifier(svrAddress) : null;

        HttpResult httpRet = HttpHelper.httpRequest(HttpMethod.POST, reqUrl, reqHeaders, null, reqBody, sslCtx, hv);
        if (httpRet == null || httpRet.getStatus() != HttpURLConnection.HTTP_OK) {
            Log.w(TAG, "request failed !! result = " + httpRet);
            return false;
        }

        if (NetConst.DEBUG) {
            Log.d(TAG, "result.getStatus = " + httpRet.getStatus());
            Log.d(TAG, "result.getHeaders = " + httpRet.getHeaders());
        }

        return true;
    }

    private void showToast(final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
