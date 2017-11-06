package com.acme.extorderapp.services;

public class SecurityToken {

	private final String csrfToken;
	private final String sessionCookie;
	
	public SecurityToken(String csrfToken, String sessionCookie) {
		this.csrfToken = csrfToken;
		this.sessionCookie = sessionCookie;
	}

	public String getCsrfToken() {
		return csrfToken;
	}

	public String getSessionCookie() {
		return sessionCookie;
	}
	
}