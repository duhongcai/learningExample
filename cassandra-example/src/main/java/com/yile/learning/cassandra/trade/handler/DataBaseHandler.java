package com.yile.learning.cassandra.trade.handler;

import com.yile.learning.cassandra.service.CassandraService;

/**
 * @author justin.liang
 */
public class DataBaseHandler {
    private CassandraService cassandraService;

    public DataBaseHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

    public void createKeyspace(String keyspace) {
        cassandraService.createKeyspace(keyspace);
    }
}
