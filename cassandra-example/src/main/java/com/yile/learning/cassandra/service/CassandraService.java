package com.yile.learning.cassandra.service;

import com.yile.learning.cassandra.locking.LockManager;
import com.yile.learning.cassandra.locking.impl.HectorLockManagerImpl;
import com.yile.learning.cassandra.utils.Constants;
import me.prettyprint.cassandra.connection.DynamicLoadBalancingPolicy;
import me.prettyprint.cassandra.connection.HConnectionManager;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.ThriftCluster;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;

public class CassandraService {
    private CassandraHostConfigurator cassandraHostConfigurator;
    private String clusterName;
    private String cassandraUrl;
    private Cluster cluster;
    private ConfigurableConsistencyLevel configurableConsistencyLevel;
    private LockManager lockManager;
    private String applicatinKeySpace = Constants.KEYSPACENAME;

    public CassandraService(String cassandraUrl, String clusterName) {
        this.clusterName = clusterName;
        this.cassandraUrl = cassandraUrl;
        configurableConsistencyLevel = new ConfigurableConsistencyLevel();
        configurableConsistencyLevel.setDefaultReadConsistencyLevel(HConsistencyLevel.LOCAL_QUORUM);
        configurableConsistencyLevel.setDefaultWriteConsistencyLevel(HConsistencyLevel.LOCAL_QUORUM);
        initCassandraHostConfigurator();
        initCassandraCluster();
        initLock();
    }

    public void initCassandraHostConfigurator() {
        int maxActive = 16;
        int timeout = 5000;
        boolean keepAlive = false;
        DynamicLoadBalancingPolicy loadBalancingPolicy = new DynamicLoadBalancingPolicy();
        cassandraHostConfigurator = new CassandraHostConfigurator(this.cassandraUrl);
        cassandraHostConfigurator.setMaxActive(maxActive);
        cassandraHostConfigurator.setCassandraThriftSocketTimeout(timeout);
        cassandraHostConfigurator.setUseSocketKeepalive(keepAlive);
        cassandraHostConfigurator.setLoadBalancingPolicy(loadBalancingPolicy);
    }

    public void initCassandraCluster() {
        cluster = new ThriftCluster(clusterName, cassandraHostConfigurator);
    }

    public void initLock() {
        lockManager = new HectorLockManagerImpl(cluster, Constants.KEYLOCKSPACENAME);
        ((HectorLockManagerImpl) lockManager).setConsistencyLevelPolicy(configurableConsistencyLevel);
        ((HectorLockManagerImpl) lockManager).init();
    }

    public void setLockManager(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public long createTimestamp() {
        return CassandraHostConfigurator.getClockResolution().createClock();
    }

    public void destory() throws Exception {
        if (cluster != null) {
            HConnectionManager connectionManager = cluster.getConnectionManager();
            if (connectionManager != null) {
                connectionManager.shutdown();
            }
        }
        cluster = null;
    }
}
