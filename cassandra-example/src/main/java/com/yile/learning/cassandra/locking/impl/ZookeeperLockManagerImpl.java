package com.yile.learning.cassandra.locking.impl;

import com.yile.learning.cassandra.locking.Lock;
import com.yile.learning.cassandra.locking.LockManager;
import com.yile.learning.cassandra.locking.LockPathBuilder;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * @author justin.liang
 */
public class ZookeeperLockManagerImpl implements LockManager {
    private static final Logger logger = LogManager.getLogger(ZookeeperLockManagerImpl.class);
    private String hostPort;
    private int sessionTimeout = 2000;
    private int maxAttempts = 5;
    private CuratorFramework client;

    public ZookeeperLockManagerImpl(String hostPort, int sessionTimeout, int maxAttempts) {
        this.hostPort = hostPort;
        this.sessionTimeout = sessionTimeout;
        this.maxAttempts = maxAttempts;
        init();
    }

    public void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(sessionTimeout, maxAttempts);
        client = CuratorFrameworkFactory.newClient(hostPort, retryPolicy);
        client.start();
    }

    public void close() {
        client.close();
    }

    @Override
    public Lock createLock(UUID applicationId, String... path) {
        String lockPath = LockPathBuilder.buildPath(applicationId, path);
        return new ZookeeperLockImpl(new InterProcessMutex(client, lockPath));
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
