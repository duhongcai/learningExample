package com.yile.learning.cassandra.locking.impl;

import com.google.common.cache.*;
import com.yile.learning.cassandra.locking.Lock;
import com.yile.learning.cassandra.locking.LockManager;
import com.yile.learning.cassandra.locking.LockPathBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * cassandra 单节点锁
 *
 * @author justin.liang
 */
public class SingleNodeLockManager implements LockManager {
    private static final Logger logger = LogManager.getLogger(SingleNodeLockManager.class);
    public static final long MILLI_EXPIRATION = 5000;
    private LoadingCache<String, ReentrantLock> locks = CacheBuilder.newBuilder()
            .expireAfterWrite(MILLI_EXPIRATION, TimeUnit.MILLISECONDS)
            .weakValues().removalListener(new RemovalListener<String, ReentrantLock>() {
                @Override
                public void onRemoval(RemovalNotification<String, ReentrantLock> notification) {
                    logger.debug("evicting reentrant lock for {}", notification.getKey());
                }
            }).build(new CacheLoader<String, ReentrantLock>() {
                @Override
                public ReentrantLock load(String key) throws Exception {
                    return new ReentrantLock(true);
                }
            });

    @Override
    public Lock createLock(UUID applicationId, String... path) {
        String lockPath = LockPathBuilder.buildPath(applicationId, path);
        try {
            return new SingleNodeLockImpl(locks.get(lockPath));
        } catch (ExecutionException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Unable to create lock in cache", e);
        }
    }
}
