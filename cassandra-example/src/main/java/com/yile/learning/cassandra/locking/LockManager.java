package com.yile.learning.cassandra.locking;

import java.util.UUID;

/**
 * 锁管理接口
 *
 * @author justin.liang
 */
public interface LockManager {
    public Lock createLock(UUID applicationId, final String... path);
}
