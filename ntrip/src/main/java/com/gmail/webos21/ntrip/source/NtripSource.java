package com.gmail.webos21.ntrip.source;

public interface NtripSource {

    SourceType getType();

    String getRawLine();

    String getSourceJson();

}
