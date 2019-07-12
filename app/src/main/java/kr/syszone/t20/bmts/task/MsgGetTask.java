package kr.syszone.t20.bmts.task;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.gmail.webos21.http.HttpHelper;
import com.gmail.webos21.http.HttpMethod;
import com.gmail.webos21.http.HttpResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import kr.syszone.t20.bmts.B2CSApp;
import kr.syszone.t20.bmts.MessagesActivity;
import kr.syszone.t20.bmts.db.DbApi;
import kr.syszone.t20.bmts.model.MsgData;
import kr.syszone.t20.bmts.model.MsgGetRequest;
import kr.syszone.t20.bmts.settings.SettingsConst;

public class MsgGetTask extends TimerTask {

    private static final String TAG = "MsgGetTask";

    private Activity act;
    private String tid;
    private String svrAddress;
    private Integer svrPort;
    private String reqUrl;

    public MsgGetTask(Activity act) {
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
            this.reqUrl += "://" + svrAddress + ":" + svrPort + "/" + NetConst.URI_VER + "/" + NetConst.URI_MESSAGE;
        }
    }

    @Override
    public void run() {
        if (reqUrl == null || tid == null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, "[MsgGetTask] 필수 정보 부족!!!", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        B2CSApp app = (B2CSApp) act.getApplication();
        byte[] aesKey = app.getAesKey();
        String accessToken = app.getAccessToken();

        if (aesKey == null || accessToken == null) {
            // TODO : policy of login retry
            // new LoginTask(act).execute();
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, "[MsgGetTask] 로그인 정보가 없습니다!!!", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        MsgGetRequest mgr = new MsgGetRequest();
        mgr.setTid(tid);

        String plainBody = mgr.toJSON(false);
        String reqBody = ClientHelper.encryptRequest(aesKey, plainBody);

        Map<String, String> reqHeaders = new HashMap<String, String>();
        //reqHeaders.put("Content-Length", Integer.toString(reqBody.length()));
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

        HttpResult httpRet = HttpHelper.httpRequest(HttpMethod.GET, reqUrl, reqHeaders, null, null, sslCtx, hv);
        if (httpRet == null || httpRet.getStatus() != HttpURLConnection.HTTP_OK) {
            Log.w(TAG, "failed !! result = " + httpRet);
            return;
        }

        if (NetConst.DEBUG) {
            Log.d(TAG, "result.getStatus = " + httpRet.getStatus());
            Log.d(TAG, "result.getHeaders = " + httpRet.getHeaders());
        }

        try {
            String bodyStr = new String(httpRet.getResponseBody(), NetConst.CHARSET);
            JSONObject json = new JSONObject(bodyStr);
            if (NetConst.DEBUG) {
                Log.d(TAG, "result.getResponseBody = " + bodyStr);
            }

            Integer statusCode = json.getInt("status_code");
            JSONArray msgList = json.getJSONArray("messages");
            int listLen = msgList.length();
            if (listLen > 0) {
                DbApi dbh = app.getDbHandle();
                int i;
                for (i = 0; i < listLen; i++) {
                    final MsgData md = new MsgData();
                    JSONObject jo = (JSONObject) msgList.get(i);
                    md.setTimeStamp(new Date(jo.getLong("time_stamp")));
                    md.setType(jo.getInt("type"));
                    md.setSender(jo.getString("sender"));
                    md.setReceiver(jo.getString("receiver"));
                    md.setContent(jo.getString("content"));
                    dbh.updateMsgData(md);
                }

                Intent intent = new Intent(act, MessagesActivity.class);
                act.startActivity(intent);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
