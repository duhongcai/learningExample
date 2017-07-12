package com.yile.learning.redis.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;

public class ShardedJedisPoolTest {
	private static final Logger logger = LogManager.getLogger(ShardedJedisPoolTest.class);
	private ShardedJedisPool shardedJedisPool;

	@Before
	public void before() {
		List<JedisShardInfo> shards = new ArrayList<>();
		JedisShardInfo jedisShardInfo = new JedisShardInfo("127.0.0.1", 6379);
		jedisShardInfo.setPassword("test");
		shards.add(jedisShardInfo);
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		// config.setMaxTotal(1);
		config.setBlockWhenExhausted(false);
		config.setTestOnBorrow(true);
		config.setTestWhileIdle(true);
		shardedJedisPool = new ShardedJedisPool(config, shards);
	}

	@After
	public void after() {
		shardedJedisPool.close();
	}

	@Test
	public void setValue() {
		// ShardedJedis shardedJedis = getShardedJedis();
		// for (int i = 0; i < 100; i++) {
		// shardedJedis.del("1_" + i);
		// }

		// String result = shardedJedis.set("redisexample", "shardedjedispool");
		// logger.debug("result:" + result);
		// shardedJedis.close();
		// shardedJedis = getShardedJedis();
		// shardedJedis.set("redisexample2", "shardedjedispool2");
		Set<Tuple> tuples = null;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = getShardedJedis();
			// String s =
			// shardedJedis.get("security_redis_cache:session:c48bf818-b5d2-4189-8801-ffc721a7338e");
			// System.out.println("s:" + s);
			// Double incrby = shardedJedis.zincrby("testd", 1, "testzincrby");
			// System.out.println("aa:"+incrby);
			// tuples = shardedJedis.zrangeByScoreWithScores("testd", 0, -1);
			tuples = shardedJedis.zrangeByScoreWithScores("dining:tables:message:key:77", "-inf", "+inf");
			// shardedJedis.zrevrangeByScore(key, max, min)
			shardedJedis.close();
			int score = (int) tuples.iterator().next().getScore();
			System.out.println(score);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * 获取分片{@link ShardedJedis}
	 *
	 * @return
	 */
	public ShardedJedis getShardedJedis() {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		return shardedJedis;
	}

	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("11", 111);
		String s = 11+"";
		System.out.println(map.get(s));
	}
}
