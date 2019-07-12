package kr.syszone.t20.bmts.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.gmail.webos21.http.HttpHelper;
import com.gmail.webos21.http.HttpMethod;
import com.gmail.webos21.http.HttpResult;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import kr.syszone.t20.bmts.B2CSApp;
import kr.syszone.t20.bmts.model.MsgData;
import kr.syszone.t20.bmts.model.MsgPostRequest;
import kr.syszone.t20.bmts.settings.SettingsConst;

public class MsgPostTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "MsgPostTask";

    private Activity act;
    private MsgData msgData;

    private String svrAddress;
    private Integer svrPort;
    private String reqUrl;
    private String tid;

    public MsgPostTask(Activity act, MsgData md) {
        this.act = act;
        this.msgData = md;

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
            this.reqUrl += "://" + svrAddress + ":" + svrPort + "/" + NetConst.URI_VER + "/" + NetConst.URI_MESSAGE;
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (reqUrl == null || tid == null || msgData == null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, "[MsgPostTask] 필수 정보 부족!!!", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        B2CSApp app = (B2CSApp) act.getApplication();

        byte[] aesKey = app.getAesKey();
        String accessToken = app.getAccessToken();

        if (aesKey == null || accessToken == null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, "[MsgPostTask] 로그인 정보가 없습니다!!!", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        // XXX : For debugging
        if (msgData.getType() == 1) {
            msgData.setType(0);
            msgData.setTimeStamp(new Date(System.currentTimeMillis() + 1000));
        }

        MsgPostRequest req = new MsgPostRequest();
        req.setTid(tid);
        req.setMessage(msgData);

        String plainBody = req.toJSON(false);
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
            return null;
        }

        if (NetConst.DEBUG) {
            Log.d(TAG, "result.getStatus = " + httpRet.getStatus());
            Log.d(TAG, "result.getHeaders = " + httpRet.getHeaders());
        }

        return null;
    }
}
