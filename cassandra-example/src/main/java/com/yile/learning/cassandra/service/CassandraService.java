package com.yile.learning.cassandra.service;

import me.prettyprint.cassandra.service.FailoverPolicy;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Keyspace;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cassandra服务,使用hector包对cassandra操作进行封装
 */
public class CassandraService {
    private static final Logger logger = LogManager.getLogger(CassandraService.class);
    private CassandraHostConfigurator cassandraHostConfigurator;
    private String clusterName;
    private String cassandraUrl;
    private Cluster cluster;
    private ConfigurableConsistencyLevel configurableConsistencyLevel;
    private LockManager lockManager;
    private Map<String, String> accessMap = new HashMap<String, String>(2);
    private String keyspace;

    public CassandraService(String clusterName, String cassandraUrl, String username, String password) {
        this.clusterName = clusterName;
        this.cassandraUrl = cassandraUrl;
        username = username == null ? "" : username;
        password = password == null ? "" : password;
        accessMap.put("username", username);
        accessMap.put("password", password);
        this.keyspace = keyspace;
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
        // TODO: 需要创建所的keyspace,在此注释掉锁管理的创建
//        lockManager = new HectorLockManagerImpl(cluster, Constants.KEYLOCKSPACENAME);
//        ((HectorLockManagerImpl) lockManager).setConsistencyLevelPolicy(configurableConsistencyLevel);
//        ((HectorLockManagerImpl) lockManager).init();
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
    public void destroy() throws Exception {
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


    public Keyspace getKeyspace(String keyspace) {
        Keyspace keyspace1 = HFactory.createKeyspace(keyspace, cluster, configurableConsistencyLevel,
                FailoverPolicy.ON_FAIL_TRY_ALL_AVAILABLE, accessMap);
        return keyspace1;
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
            // cluster.dropKeyspace(keyspace);
            return;
        }
        ThriftKsDef keyspaceDefinition = (ThriftKsDef) HFactory.createKeyspaceDefinition(keyspace,
                strategy, replication, cfDefs);
        if (strategyOption != null && strategyOption.size() > 0) {
            keyspaceDefinition.setStrategyOptions(strategyOption);
        }
        cluster.addKeyspace(keyspaceDefinition);
    }

    /**
     * 创建列家族
     *
     * @param keyspace
     * @param cfDef
     */
    public void createColumnFamily(String keyspace, ColumnFamilyDefinition cfDef) {
        if (!keyspaceExist(keyspace)) {
            createKeyspace(keyspace);
        }

        if (!cfExist(keyspace, cfDef.getName())) {
            cfDef.setReadRepairChance(0.1d);
            cfDef.setCompactionStrategy("LeveledCompactionStrategy");
            Map<String, String> options = new HashMap<String, String>();
            options.put("sstable_size_in_mb", "512");
            cfDef.setCompactionStrategyOptions(options);
            cluster.addColumnFamily(cfDef, true);
            logger.info("Create column family {} in keyspace {}", cfDef.getName(), keyspace);
        } else {
            logger.info("{} already created", cfDef.getName());
        }
    }

    /**
     * 创建多个列家族
     *
     * @param keyspace
     * @param cfDefs
     */
    public void createColumnFamilies(String keyspace, List<ColumnFamilyDefinition> cfDefs) {
        for (ColumnFamilyDefinition cfDef : cfDefs) {
            createColumnFamily(keyspace, cfDef);
        }
    }

    /**
     * 根据keyspace名称获取{@link KeyspaceDefinition}
     *
     * @param keyspace
     * @return
     */
    public KeyspaceDefinition getKeyspaceDef(String keyspace) {
        KeyspaceDefinition ksDef = cluster.describeKeyspace(keyspace);
        return ksDef;
    }

    /**
     * 判断keyspace是否创建
     *
     * @param keyspace
     * @return
     */
    public boolean keyspaceExist(String keyspace) {
        KeyspaceDefinition ksDef = cluster.describeKeyspace(keyspace);
        if (ksDef == null) {
            return false;
        }
        return true;
    }

    /**
     * 判断column Family是否创建
     *
     * @param keyspace
     * @param cfName
     * @return
     */
    public boolean cfExist(String keyspace, String cfName) {
        KeyspaceDefinition ksDef = getKeyspaceDef(keyspace);
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
