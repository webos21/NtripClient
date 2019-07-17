package com.gmail.webos21.ntrip;

import com.gmail.webos21.ntrip.source.NtripSource;
import com.gmail.webos21.ntrip.source.ServerInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class CasterClientTest {

    /**
     * Get Source Table
     */
    @Test
    public void TestCasterClient() {
        ServerInfo sinfo = new ServerInfo("gnssdata.or.kr", 2101, "gnss", "gnss");
        CasterClient cc = new CasterClient(sinfo);
        List<NtripSource> sourceList = cc.getSourceTable();
        for (NtripSource ns : sourceList) {
            System.out.println(ns.getSourceJson());
        }
    }
}
