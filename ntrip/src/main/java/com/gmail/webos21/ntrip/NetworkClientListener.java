package com.gmail.webos21.ntrip;

public interface NetworkClientListener {

    void onDataReceived(byte[] data);

    void onTimeoutOccurred();

    void onTransactionCompleted();

}
