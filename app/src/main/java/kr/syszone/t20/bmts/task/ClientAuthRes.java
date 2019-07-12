package kr.syszone.t20.bmts.task;

public class ClientAuthRes {

    private String pubKey;
    private String aesKey;
    private String accessToken;

    private byte[] aesKeyBytes;

    public ClientAuthRes() {
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public byte[] getAesKeyBytes() {
        return aesKeyBytes;
    }

    public void setAesKeyBytes(byte[] aesKeyBytes) {
        this.aesKeyBytes = aesKeyBytes;
    }

}
