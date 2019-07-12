package com.gmail.webos21.ntrip.model;

public interface NtripSource {

    SourceType getType();

    String getRawLine();

    String getSourceJson();

}
