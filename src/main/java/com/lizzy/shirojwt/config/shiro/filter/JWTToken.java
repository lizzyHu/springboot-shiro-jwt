package com.lizzy.shirojwt.config.shiro.filter;

import org.apache.shiro.authc.AuthenticationToken;

public class JWTToken implements AuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2404719767161247563L;
	
	private String token;
	
	public JWTToken(String token) {
		this.token = token;
	}
	
	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
