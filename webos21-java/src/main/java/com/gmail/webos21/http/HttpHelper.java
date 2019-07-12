package com.gmail.webos21.http;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class HttpHelper {

    public static final int TIMEOUT_CONNECT = 15000;
    public static final int TIMEOUT_READ = 15000;

    private static final int BUF_LEN_1K = 1024;
    private static final int BUF_LEN_8K = 8192;

    public static HttpResult httpRequest(HttpMethod reqMethod, String uri, Map<String, String> reqHeaders,
                                         Map<String, String> queryParams, String reqBody, SSLContext sslContext, HostnameVerifier hv) {
        HttpResult result = null;

        URL url = null;
        try {
            String reqUri = uri;
            if (queryParams != null) {
                reqUri += "?" + makeQueryString(queryParams);
            }
            url = new URL(reqUri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return result;
        }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT_READ);
            conn.setConnectTimeout(TIMEOUT_CONNECT);
            conn.setRequestMethod(reqMethod.getValue());
            conn.setDoInput(true);

            if ("https".equals(url.getProtocol())) {
                HttpsURLConnection sconn = (HttpsURLConnection) conn;
                if (sslContext != null) {
                    sconn.setSSLSocketFactory(sslContext.getSocketFactory());
                }
                if (hv != null) {
                    sconn.setHostnameVerifier(hv);
                }
            }

            if (reqHeaders != null) {
                Set<String> keySet = reqHeaders.keySet();
                for (String key : keySet) {
                    conn.addRequestProperty(key, reqHeaders.get(key));
                }
            }

            if (HttpMethod.POST == reqMethod || HttpMethod.PUT == reqMethod) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(reqBody);
                writer.flush();
                writer.close();
            }

            int responseCode = conn.getResponseCode();
            Map<String, List<String>> headers = conn.getHeaderFields();
            byte[] responseBody = null;
            if (HttpMethod.HEAD != reqMethod) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(BUF_LEN_8K);
                InputStream is = conn.getInputStream();
                byte[] buf = new byte[BUF_LEN_1K];
                int rbytes;
                while ((rbytes = is.read(buf)) > 0) {
                    baos.write(buf, 0, rbytes);
                }
                responseBody = baos.toByteArray();
                baos.close();
                is.close();
            }
            result = new HttpResult(responseCode, headers, responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

    public static String makeQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

}
