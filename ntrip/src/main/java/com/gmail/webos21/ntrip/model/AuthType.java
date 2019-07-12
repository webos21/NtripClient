package com.gmail.webos21.ntrip.model;

public enum AuthType {
    UNKNOWN, N, B, D;

    public static AuthType getAuthType(String id) {
        AuthType rc = UNKNOWN;

        AuthType[] vals = AuthType.values();
        for (AuthType v : vals) {
            if (v.name().equals(id)) {
                rc = v;
                break;
            }
        }

        return rc;
    }

}
