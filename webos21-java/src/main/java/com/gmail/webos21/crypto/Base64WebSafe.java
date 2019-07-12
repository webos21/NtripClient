package com.gmail.webos21.crypto;

public class Base64WebSafe {

    private static final int DEF_FLAG = Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP;

    public static String encode(byte[] input) {
        return Base64.encodeToString(input, DEF_FLAG);
    }

    public static byte[] decode(String b64str) {
        return Base64.decode(b64str, DEF_FLAG);
    }

}
