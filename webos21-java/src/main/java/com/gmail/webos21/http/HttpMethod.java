package com.gmail.webos21.http;

public enum HttpMethod {
	HEAD("HEAD"), GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

	private String value;

	HttpMethod(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

}
