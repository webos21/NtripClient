package kr.syszone.t20.bmts.task;

import com.gmail.webos21.crypto.Base64WebSafe;
import com.gmail.webos21.crypto.CryptoHelper;

import java.security.KeyPair;

import kr.syszone.t20.bmts.task.NetConst;

public class ClientAuthReq {

    private String pubKey;
    private String tid;

    private byte[] privKeyBytes;
    private byte[] pubKeyBytes;

    public ClientAuthReq(String phoneNo) {
        KeyPair rsaPair = CryptoHelper.genKeyPair(NetConst.ALG_RSA, NetConst.ALG_HASH, NetConst.PKI_KEY_LEN);
        this.privKeyBytes = CryptoHelper.getPrivateKey(rsaPair).getEncoded();
        this.pubKeyBytes = CryptoHelper.getPublicKey(rsaPair).getEncoded();

        byte[] tidEnc = CryptoHelper.encryptWithPrivateKey(NetConst.ALG_RSA, privKeyBytes, phoneNo, NetConst.CHARSET);

        this.pubKey = Base64WebSafe.encode(pubKeyBytes);
        this.tid = Base64WebSafe.encode(tidEnc);
    }

    public byte[] getPrivKeyBytes() {
        return privKeyBytes;
    }

    public void setPrivKeyBytes(byte[] privKeyBytes) {
        this.privKeyBytes = privKeyBytes;
    }

    public byte[] getPubKeyBytes() {
        return pubKeyBytes;
    }

    public void setPubKeyBytes(byte[] pubKeyBytes) {
        this.pubKeyBytes = pubKeyBytes;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String toJSON() {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        sb.append("  \"tid\" : \"").append(tid).append('\"').append(',').append('\n');
        sb.append("  \"pub_key\" : \"").append(pubKey).append('\"').append('\n');
        sb.append('}').append('\n');

        return sb.toString();

    }
}
