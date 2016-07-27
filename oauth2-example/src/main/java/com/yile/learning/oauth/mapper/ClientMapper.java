package com.yile.learning.oauth.mapper;

import com.rabbitframework.jadb.annontations.Mapper;
import com.rabbitframework.jadb.annontations.Select;
import com.yile.learning.oauth.model.OauthClient;

@Mapper
public interface ClientMapper {
	@Select("select * from oauth_client where client_id=#{clientId}")
	public OauthClient findClientByClientId(String clientId);

	@Select("select * from oauth_client where client_secret=#{clientSecret}")
	public OauthClient findClientByClientSecret(String clientSecret);
}
