package com.yile.learning.oauth.utils;

public class Constants {
	/** 10分钟 **/
	public static final int AUTH_CODE_EXPIRE = 600;
	/** 1小时 **/
	public static final int ACCESS_TOKEN_EXPIRE = 3600;
	
	public static final String AUTH_CODE_KEY_PREFIX = "oauthCodeKey:";

	public static final String ACCESS_TOKEN_KEY_PREFIX = "access_token:";
}
