package kr.syszone.t20.bmts.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.preference.PreferenceManager;
import android.util.Log;

import com.gmail.webos21.http.HttpHelper;
import com.gmail.webos21.http.HttpMethod;
import com.gmail.webos21.http.HttpResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import kr.syszone.t20.bmts.B2CSApp;
import kr.syszone.t20.bmts.settings.SettingsConst;

public class LoginTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "LoginTask";

    private Activity act;
    private String svrAddress;
    private Integer svrPort;
    private String reqUrl;
    private String tid;
    private ClientAuthRes authResult;

    public LoginTask(Activity act) {
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
            this.reqUrl += "://" + svrAddress + ":" + svrPort + "/" + NetConst.URI_VER + "/" + NetConst.URI_AUTH;
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (reqUrl == null || tid == null) {
            return null;
        }

        ClientAuthReq req = new ClientAuthReq(tid);
        String reqBody = req.toJSON();

        Map<String, String> reqHeaders = new HashMap<String, String>();
        reqHeaders.put("Content-Length", Integer.toString(reqBody.length()));
        reqHeaders.put("Content-Type", "application/json;charset=utf-8");

        if (NetConst.DEBUG) {
            Log.d(TAG, "request.URL = " + reqUrl);
            Log.d(TAG, "request.Headers = " + reqHeaders);
            Log.d(TAG, "request.Body = " + reqBody);
        }

        SSLContext sslCtx = (svrPort == 443) ? ClientHelper.getSSLContext(act) : null;
        HostnameVerifier hv = (svrPort == 443) ? ClientHelper.getHostnameVerifier(svrAddress) : null;

        HttpResult httpRet = HttpHelper.httpRequest(HttpMethod.PUT, reqUrl, reqHeaders, null, reqBody, sslCtx, hv);
        if (httpRet == null || httpRet.getStatus() != HttpURLConnection.HTTP_OK) {
            Log.w(TAG, "request failed !! result = " + httpRet);
            return null;
        }

        if (NetConst.DEBUG) {
            Log.d(TAG, "result.getStatus = " + httpRet.getStatus());
            Log.d(TAG, "result.getHeaders = " + httpRet.getHeaders());
        }

        try {
            String bodyStr = new String(httpRet.getResponseBody(), "UTF-8");
            JSONObject json = new JSONObject(bodyStr);
            if (NetConst.DEBUG) {
                Log.d(TAG, "result.getResponseBody = " + bodyStr);
            }

            String pubKey = json.getString("pub_key");
            String aesKey = json.getString("aes_key");
            String accessToken = json.getString("access_token");

            if (NetConst.DEBUG) {
                Log.d(TAG, "pub_key = " + pubKey);
                Log.d(TAG, "aes_key = " + aesKey);
                Log.d(TAG, "access_token  = " + accessToken);
            }

            authResult = ClientHelper.getAuthResponse(req, pubKey, aesKey, accessToken);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (authResult != null) {
            B2CSApp app = (B2CSApp) act.getApplication();
            app.setAuthInfo(authResult.getAesKeyBytes(), authResult.getAccessToken());
        }
    }
}
