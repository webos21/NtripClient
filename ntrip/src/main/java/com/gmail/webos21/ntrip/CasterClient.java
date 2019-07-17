package com.gmail.webos21.ntrip;

import com.gmail.webos21.crypto.Base64;
import com.gmail.webos21.ntrip.source.CasterSource;
import com.gmail.webos21.ntrip.source.NetworkSource;
import com.gmail.webos21.ntrip.source.NtripSource;
import com.gmail.webos21.ntrip.source.ServerInfo;
import com.gmail.webos21.ntrip.source.SourceType;
import com.gmail.webos21.ntrip.source.StreamSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class CasterClient {

    private ServerInfo serverInfo;


    public CasterClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public List<NtripSource> getSourceTable() {
        List<NtripSource> resultList = new ArrayList<NtripSource>();

        Socket nsocket = null;
        InputStream nis = null;
        OutputStream nos = null;

        String hostName = serverInfo.getHost();
        int port = serverInfo.getPort();
        String username = serverInfo.getUsername();
        String password = serverInfo.getPassword();

        try {
            SocketAddress sockaddr = new InetSocketAddress(hostName, port);

            nsocket = new Socket();
            nsocket.connect(sockaddr, 10 * 1000); /* 10 second connection timeout */
            if (nsocket.isConnected()) {
                nsocket.setSoTimeout(20 * 1000); /* 20 second timeout once data is flowing */
                nis = nsocket.getInputStream();
                nos = nsocket.getOutputStream();

                StringBuilder sb = new StringBuilder();

                sb.append("GET / HTTP/1.0\r\n");
                sb.append("User-Agent: NTRIP JavaNTRIPClient/20190711\r\n");
                sb.append("Accept: */*\r\n");
                sb.append("Connection: close\r\n");
                if (username.length() > 0) {
                    sb.append("Authorization: Basic ").append(ToBase64(username + ":" + password));
                }
                sb.append("\r\n");
                nos.write(sb.toString().getBytes());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int readBytes = 0;

                while ((readBytes = nis.read(buffer, 0, 4096)) != -1) {
                    baos.write(buffer, 0, readBytes);
                }

                byte[] receivedData = baos.toByteArray();
                baos.close();
                baos = null;

                String receivedString = new String(receivedData);
                String[] stringLines = receivedString.split("\r\n");
                for (String line : stringLines) {
                    if (line.length() < 3) {
                        continue;
                    }
                    String typeStr = line.substring(0, 3);
                    SourceType st = SourceType.getSourceType(typeStr);
                    switch (st) {
                        case CAS:
                            resultList.add(new CasterSource(line));
                            break;
                        case NET:
                            resultList.add(new NetworkSource(line));
                            break;
                        case STR:
                            resultList.add(new StreamSource(line));
                            break;
                        default:
                            System.out.println("Invalid Line : " + line);
                            break;
                    }
                }
            }
        } catch (SocketTimeoutException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (nis != null) {
                    nis.close();
                }
                if (nos != null) {
                    nos.close();
                }
                if (nsocket != null) {
                    nsocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultList;
    }

    private String ToBase64(String in) {
        return Base64.encodeToString(in.getBytes(), Base64.CRLF);
    }

}
