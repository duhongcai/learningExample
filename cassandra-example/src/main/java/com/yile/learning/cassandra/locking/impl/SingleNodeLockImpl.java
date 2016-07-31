package com.yile.learning.cassandra.locking.impl;

import com.yile.learning.cassandra.locking.Lock;
import com.yile.learning.cassandra.locking.LockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SingleNodeLockImpl implements Lock {
    private Logger logger = LogManager.getLogger(SingleNodeLockImpl.class);
    private final ReentrantLock lock;

    public SingleNodeLockImpl(ReentrantLock lock) {
        this.lock = lock;
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit time) throws LockException {
        try {
            return this.lock.tryLock(timeout, time);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw new LockException("Couldn't get the lock", e);
        }
    }

    @Override
    public void lock() throws LockException {
        this.lock.lock();
    }

    @Override
    public void unlock() throws LockException {
        this.lock.unlock();
    }
}
