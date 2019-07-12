package com.gmail.webos21.http;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpResult {

	private int status;
	private Map<String, List<String>> headers;
	private byte[] responseBody;

	public HttpResult(int status) {
		this(status, null, null);
	}

	public HttpResult(Map<String, List<String>> headers, byte[] responseBody) {
		this(HttpURLConnection.HTTP_OK, headers, responseBody);
	}

	public HttpResult(int status, Map<String, List<String>> headers, byte[] responseBody) {
		this.status = status;
		this.headers = headers;
		this.responseBody = responseBody;
	}

	public int getStatus() {
		return status;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public byte[] getResponseBody() {
		return responseBody;
	}
}
