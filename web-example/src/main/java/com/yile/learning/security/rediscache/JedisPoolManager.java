package com.yile.learning.security.rediscache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

public class JedisPoolManager implements RedisManager {
    private String host = "127.0.0.1";
    private int port = 6379;
    // 0 - never expire
    private int expire = 0;
    //timeout for jedis try to connect to redis server, not expire time! In milliseconds
    private int timeout = 0;
    private String password = "";
    private static JedisPool jedisPool = null;

    public JedisPoolManager() {

    }

    /**
     * 初始化方法
     */
    @Override
    public void init() {
        if (jedisPool == null) {
            if (password != null && !"".equals(password)) {
                jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
            } else if (timeout != 0) {
                jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout);
            } else {
                jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
            }

        }
    }

    /**
     * get value from redis
     *
     * @param key
     * @return
     */
    @Override
    public byte[] get(byte[] key) {
        byte[] value = null;
        Jedis jedis = jedisPool.getResource();
        try {
            value = jedis.get(key);
        } catch (Throwable e) {
            jedisPool.returnBrokenResource(jedis);
            throw new RuntimeException(e);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public byte[] set(byte[] key, byte[] value) {
        set(key, value, expire);
        return value;
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    @Override
    public byte[] set(byte[] key, byte[] value, int expire) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
            if (expire != 0) {
                jedis.expire(key, expire);
            }
        } catch (Throwable e) {
            jedisPool.returnBrokenResource(jedis);
            throw new RuntimeException(e);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * del
     *
     * @param key
     */
    @Override
    public void del(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } catch (Throwable e) {
            jedisPool.returnBrokenResource(jedis);
            throw new RuntimeException(e);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * flush
     */
    @Override
    public void flushDB() {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.flushDB();
        } catch (Throwable e) {
            jedisPool.returnBrokenResource(jedis);
            throw new RuntimeException(e);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * size
     */
    @Override
    public Long dbSize() {
        Long dbSize = 0L;
        Jedis jedis = jedisPool.getResource();
        try {
            dbSize = jedis.dbSize();
        } catch (Throwable e) {
            jedisPool.returnBrokenResource(jedis);
            throw new RuntimeException(e);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return dbSize;
    }

    /**
     * keys
     *
     * @param pattern
     * @return
     */
    @Override
    public Set<byte[]> keys(String pattern) {
        Set<byte[]> keys = null;
        Jedis jedis = jedisPool.getResource();
        try {
            keys = jedis.keys(pattern.getBytes());
        } catch (Throwable e) {
            jedisPool.returnBrokenResource(jedis);
            throw new RuntimeException(e);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return keys;
    }

    @Override
    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getExpire() {
        return expire;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}