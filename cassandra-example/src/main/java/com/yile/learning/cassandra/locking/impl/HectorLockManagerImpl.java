package com.yile.learning.cassandra.locking.impl;

import com.yile.learning.cassandra.locking.Lock;
import com.yile.learning.cassandra.locking.LockManager;
import com.yile.learning.cassandra.locking.LockPathBuilder;
import me.prettyprint.cassandra.locking.HLockManagerImpl;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ConsistencyLevelPolicy;
import me.prettyprint.hector.api.locking.HLockManager;
import me.prettyprint.hector.api.locking.HLockManagerConfigurator;

import java.util.UUID;

/**
 * @author justin.liang
 */
public class HectorLockManagerImpl implements LockManager {
    private int replicationFactor = 1;
    private int numberOfLockObserverThreads = 1;
    private long lockTtl = 2000;
    private Cluster cluster;
    private String keyspaceName;
    private HLockManager hLockManager;
    private ConsistencyLevelPolicy consistencyLevelPolicy;

    public HectorLockManagerImpl() {
    }

    public HectorLockManagerImpl(Cluster cluster, String keyLockspaceName) {
        this.cluster = cluster;
        this.keyspaceName = keyLockspaceName;
    }

    public void init() {
        HLockManagerConfigurator hlc = new HLockManagerConfigurator();
        hlc.setReplicationFactor(replicationFactor);
        hlc.setKeyspaceName(keyspaceName);
        hlc.setNumberOfLockObserverThreads(numberOfLockObserverThreads);
        hlc.setLocksTTLInMillis(lockTtl);
        hLockManager = new HLockManagerImpl(cluster, hlc);
        if (consistencyLevelPolicy != null) {
            hLockManager.getKeyspace().setConsistencyLevelPolicy(consistencyLevelPolicy);
        }
        hLockManager.init();
    }

    @Override
    public Lock createLock(UUID applicationId, String... path) {
        String lockPath = LockPathBuilder.buildPath(applicationId, path);
        return new HectorLockImpl(hLockManager.createLock(lockPath), hLockManager);
    }

    //必须是奇数
    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public void setConsistencyLevelPolicy(ConsistencyLevelPolicy consistencyLevelPolicy) {
        this.consistencyLevelPolicy = consistencyLevelPolicy;
    }

    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public void setNumberOfLockObserverThreads(int numberOfLockObserverThreads) {
        this.numberOfLockObserverThreads = numberOfLockObserverThreads;
    }

    public void sethLockManager(HLockManager hLockManager) {
        this.hLockManager = hLockManager;
    }
}
