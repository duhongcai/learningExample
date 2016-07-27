package com.yile.learning.oauth.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yile.learning.oauth.model.OauthClient;
import com.yile.learning.oauth.service.ClientService;
import com.yile.learning.oauth.utils.Constants;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Component
public class OauthBiz {
	@Autowired
	private ClientService clientService;
	@Autowired
	private ShardedJedisPool shardedJedisPool;

	public OauthClient findClientByClientId(String clientId) {
		return clientService.findClientByClientId(clientId);
	}

	public OauthClient findClientByClientSecret(String clientSecret) {
		return clientService.findClientByClientSecret(clientSecret);
	}

	public void addAuthCode(String key, String authCode) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			shardedJedis.set(key, authCode);
			shardedJedis.expire(key, Constants.AUTH_CODE_EXPIRE);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			shardedJedis.close();
		}
	}

	public void delAuthCode(String key) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			shardedJedis.del(key);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			shardedJedis.close();
		}
	}

	public String getAuthCodeByKey(String key) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		String authCode = "";
		try {
			authCode = shardedJedis.get(key);
			return authCode;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			shardedJedis.close();
		}
	}

	public void addAccessToken(String key, String accessToken) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			shardedJedis.set(key, accessToken);
			shardedJedis.expire(key, Constants.ACCESS_TOKEN_EXPIRE);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			shardedJedis.close();
		}
	}

	public String getAccessTokenByKey(String key) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		String accessToken = "";
		try {
			accessToken = shardedJedis.get(key);
			return accessToken;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			shardedJedis.close();
		}
	}
}
