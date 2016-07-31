package com.yile.learning.cassandra.locking.impl;

import com.yile.learning.cassandra.locking.Lock;
import com.yile.learning.cassandra.locking.LockException;
import me.prettyprint.hector.api.locking.HLock;
import me.prettyprint.hector.api.locking.HLockManager;
import me.prettyprint.hector.api.locking.HLockTimeoutException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HectorLockImpl implements Lock {
    private HLock lock;
    private HLockManager lockManager;
    private AtomicInteger count = new AtomicInteger();

    public HectorLockImpl(HLock lock, HLockManager lockManager) {
        this.lock = lock;
        this.lockManager = lockManager;
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit time) throws LockException {
        try {
            lockManager.acquire(lock, time.toMillis(timeout));
            count.incrementAndGet();
        } catch (HLockTimeoutException e) {
            return false;
        }
        return true;
    }

    @Override
    public void lock() throws LockException {
        lockManager.acquire(lock);
        count.incrementAndGet();
    }

    @Override
    public void unlock() throws LockException {
        int current = count.decrementAndGet();
        if (current == 0) {
            lockManager.release(lock);
        }
    }
}
