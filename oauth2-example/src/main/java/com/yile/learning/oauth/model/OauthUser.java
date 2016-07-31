package com.yile.learning.oauth.model;

import com.rabbitframework.dbase.annontations.Column;
import com.rabbitframework.dbase.annontations.ID;
import com.rabbitframework.dbase.annontations.Table;

/**
 * This class corresponds to the database table oauth_user
 */
@Table
public class OauthUser {
	/**
	 * This field corresponds to the database column oauth_user.user_id
	 */
	@ID
	private long userId;

	/**
	 * This field corresponds to the database column oauth_user.user_name
	 */
	@Column
	private String userName;

	/**
	 * This field corresponds to the database column oauth_user.password
	 */
	@Column
	private String password;

	/**
	 * This field corresponds to the database column oauth_user.salt
	 */
	@Column
	private String salt;

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSalt() {
		return salt;
	}

}
