package kr.syszone.t20.bmts.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.gmail.webos21.http.HttpHelper;
import com.gmail.webos21.http.HttpMethod;
import com.gmail.webos21.http.HttpResult;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import kr.syszone.t20.bmts.B2CSApp;
import kr.syszone.t20.bmts.model.AmbData;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.model.ObdData;
import kr.syszone.t20.bmts.model.SensorsRequest;
import kr.syszone.t20.bmts.settings.SettingsConst;

public class TransferTask extends TimerTask {

    private static final String TAG = "TransferTask";

    private Activity act;
    private String tid;
    private String svrAddress;
    private Integer svrPort;
    private String reqUrl;

    public TransferTask(Activity act) {
        this.act = act;
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
            this.reqUrl += "://" + svrAddress + ":" + svrPort + "/" + NetConst.URI_VER + "/" + NetConst.URI_SENSORS;
        }
    }

    @Override
    public void run() {
        B2CSApp app = (B2CSApp) act.getApplication();
        byte[] aesKey = app.getAesKey();
        String accessToken = app.getAccessToken();

        if (aesKey == null || accessToken == null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, "[TransferTask] 로그인 정보가 없습니다!!!", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        synchronized (app) {
            List<GpsData> gpsList = app.getGpsList();
            List<ObdData> obdList = app.getObdList();
            List<AmbData> ambList = app.getAmbList();

            SensorsRequest sr = new SensorsRequest();
            sr.setTid(tid);
            sr.setGps(gpsList.toArray(new GpsData[0]));
            sr.setObd(obdList.toArray(new ObdData[0]));
            sr.setAmb(ambList.toArray(new AmbData[0]));

            String plainBody = sr.toJSON(false);
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
                Log.w(TAG, "failed !! result = " + httpRet);
                return;
            }

            if (NetConst.DEBUG) {
                Log.d(TAG, "result.getStatus = " + httpRet.getStatus());
                Log.d(TAG, "result.getHeaders = " + httpRet.getHeaders());
                try {
                    Log.d(TAG, "result.getResponseBody = " + new String(httpRet.getResponseBody(), NetConst.CHARSET));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            gpsList.clear();
            obdList.clear();
            ambList.clear();
        }

    }


}
