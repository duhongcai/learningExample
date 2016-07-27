package com.yile.learning.oauth.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yile.learning.oauth.mapper.ClientMapper;
import com.yile.learning.oauth.model.OauthClient;
import com.yile.learning.oauth.service.ClientService;

@Service("clientService")
public class ClientServiceImpl implements ClientService {
	@Resource
	private ClientMapper clientMapper;

	@Override
	public OauthClient findClientByClientId(String clientId) {
		return clientMapper.findClientByClientId(clientId);
	}

	public OauthClient findClientByClientSecret(String clientSecret) {
		return clientMapper.findClientByClientSecret(clientSecret);
	}
}
