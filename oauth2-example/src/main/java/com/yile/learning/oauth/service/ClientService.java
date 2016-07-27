package com.yile.learning.oauth.service;

import com.yile.learning.oauth.model.OauthClient;

public interface ClientService {
	public OauthClient findClientByClientId(String clientId);

	public OauthClient findClientByClientSecret(String clientSecret);
}
