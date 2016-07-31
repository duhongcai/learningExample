package com.yile.learning.cassandra.service;

import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CassandraService {
    private static final Logger logger = LogManager.getLogger(CassandraService.class);
    private CassandraHostConfigurator cassandraHostConfigurator;
    private String clusterName;
    private String cassandraUrl;
    private Cluster cluster;
    private ConfigurableConsistencyLevel configurableConsistencyLevel;
    private LockManager lockManager;


    public CassandraService(String clusterName, String cassandraUrl) {
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


    /**
     * 关闭cassandra连接
     *
     * @throws Exception
     */
    public void destory() throws Exception {
        if (cluster != null) {
            HConnectionManager connectionManager = cluster.getConnectionManager();
            if (connectionManager != null) {
                connectionManager.shutdown();
            }
        }
        logger.debug("cassandra service destory done!");
        cluster = null;
    }

    /**
     * 创建keyspace
     *
     * @param keyspace
     */
    public void createKeyspace(String keyspace) {
        createKeyspace(keyspace, "org.apache.cassandra.locator.SimpleStrategy",
                1, new ArrayList<ColumnFamilyDefinition>(), null);
    }

    /**
     * 创建keyspace,如果cassandra中包有keyspace将删除重新创建
     *
     * @param keyspace
     * @param strategy
     * @param replication
     * @param cfDefs
     * @param strategyOption
     */
    private void createKeyspace(String keyspace, String strategy, int replication,
                                List<ColumnFamilyDefinition> cfDefs, Map<String, String> strategyOption) {
        if (cluster.describeKeyspace(keyspace) != null) {
            cluster.dropKeyspace(keyspace);
        }
        ThriftKsDef keyspaceDefinition = (ThriftKsDef) HFactory.createKeyspaceDefinition(keyspace,
                strategy, replication, cfDefs);
        if (strategyOption != null && strategyOption.size() > 0) {
            keyspaceDefinition.setStrategyOptions(strategyOption);
        }
        cluster.addKeyspace(keyspaceDefinition);
    }

    /**
     * 判断column Family是否创建
     *
     * @param keyspace
     * @param cfName
     * @return
     */
    public boolean cfExists(String keyspace, String cfName) {
        KeyspaceDefinition ksDef = cluster.describeKeyspace(keyspace);
        if (ksDef == null) {
            return false;
        }
        for (ColumnFamilyDefinition cfDef : ksDef.getCfDefs()) {
            if (cfDef.getName().equals(cfName)) {
                return true;
            }
        }
        return false;
    }
}
