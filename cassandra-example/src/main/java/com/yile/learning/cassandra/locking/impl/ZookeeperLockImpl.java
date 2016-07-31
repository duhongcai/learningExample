package com.yile.learning.cassandra.locking.impl;

import com.yile.learning.cassandra.locking.Lock;
import com.yile.learning.cassandra.locking.LockException;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @author justin.liang
 */
public class ZookeeperLockImpl implements Lock {
    private static final Logger logger = LogManager.getLogger(ZookeeperLockImpl.class);
    private InterProcessMutex zkMutex;

    public ZookeeperLockImpl(InterProcessMutex zkMutex) {
        this.zkMutex = zkMutex;
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit time) throws LockException {
        try {
            return zkMutex.acquire(timeout, time);
        } catch (Exception e) {
            logger.error("Unable to obtain lock:" + e.getMessage(), e);
            throw new LockException("Unable to obtain lock", e);
        }
    }

    @Override
    public void lock() throws LockException {
        try {
            zkMutex.acquire();
        } catch (Exception e) {
            logger.error("Unable to obtain lock:" + e.getMessage(), e);
            throw new LockException("Unable to obtain lock", e);
        }
    }

    @Override
    public void unlock() throws LockException {
        try {
            zkMutex.release();
        } catch (Exception e) {
            logger.error("Unable to obtain lock:" + e.getMessage(), e);
            throw new LockException("Unable to obtain lock", e);
        }
    }
}
