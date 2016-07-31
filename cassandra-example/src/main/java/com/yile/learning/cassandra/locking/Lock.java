package com.yile.learning.cassandra.locking;

import java.util.concurrent.TimeUnit;

public interface Lock {
    public boolean tryLock(long timeout, TimeUnit time) throws LockException;

    public void lock() throws LockException;

    public void unlock() throws LockException;
}
