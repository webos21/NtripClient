package kr.syszone.t20.bmts.task;

import android.content.Context;
import android.content.res.AssetManager;

import com.gmail.webos21.crypto.Base64WebSafe;
import com.gmail.webos21.crypto.CryptoHelper;
import com.gmail.webos21.crypto.SelfSignedSSL;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public class ClientHelper {

    private static SSLContext sslContext;
    private static HostnameVerifier hostnameVerifier;

    public static ClientAuthRes getAuthResponse(ClientAuthReq authReq, String pubKey, String aesKey, String accessToken) {
        ClientAuthRes result = new ClientAuthRes();

        byte[] pubKeyBytes = Base64WebSafe.decode(pubKey);
        byte[] aesKeyBytes = Base64WebSafe.decode(aesKey);
        byte[] accessTokenBytes = Base64WebSafe.decode(accessToken);

        byte[] decAesKey = CryptoHelper.decryptWithPrivateKey(NetConst.ALG_RSA, authReq.getPrivKeyBytes(), aesKeyBytes);
        byte[] iv = Arrays.copyOf(decAesKey, 16);
        byte[] decToken1 = CryptoHelper.decryptWithAESKey(decAesKey, iv, accessTokenBytes);
        byte[] decToken2 = CryptoHelper.decryptWithPublicKey(NetConst.ALG_RSA, pubKeyBytes, decToken1);

        result.setPubKey(pubKey);
        result.setAesKeyBytes(decAesKey);
        result.setAesKey(Base64WebSafe.encode(decAesKey));
        result.setAccessToken(Base64WebSafe.encode(decToken2));

        return result;
    }

    public static String encryptRequest(byte[] aesKey, String json) {
        byte[] iv = Arrays.copyOf(aesKey, 16);
        byte[] dataToEnc = null;
        try {
            dataToEnc = json.getBytes(NetConst.CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] encBytes = CryptoHelper.encryptWithAESKey(aesKey, iv, dataToEnc);

        return Base64WebSafe.encode(encBytes);
    }


    public static SSLContext getSSLContext(Context context) {
        if (!NetConst.USE_SELF_SIGNED_SSL) {
            return null;
        }
        if (sslContext == null) {
            AssetManager assetManager = context.getResources().getAssets();
            InputStream is = null;
            try {
                is = assetManager.open("hazzero-mid06.cer");
                sslContext = SelfSignedSSL.createSSLContext(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // Nothing to do
                    }
                }
            }
        }
        return sslContext;
    }

    public static HostnameVerifier getHostnameVerifier(String serverDomain) {
        if (!NetConst.USE_SELF_SIGNED_SSL) {
            return null;
        }
        if (hostnameVerifier == null) {
            hostnameVerifier = SelfSignedSSL.createHostnameVerifier(new String[]{serverDomain});
        }
        return hostnameVerifier;
    }

}
