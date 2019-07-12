package com.gmail.webos21.ntrip.model;

public enum SourceType {
    UNKNOWN, STR, CAS, NET;

    public static SourceType getSourceType(String id) {
        SourceType rc = UNKNOWN;

        SourceType[] vals = SourceType.values();
        for (SourceType v : vals) {
            if (v.name().equals(id)) {
                rc = v;
                break;
            }
        }

        return rc;
    }

}
