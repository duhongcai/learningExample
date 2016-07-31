package com.yile.learning.cassandra.trade.handler;

import com.yile.learning.cassandra.service.CassandraService;

/**
 * @author justin.liang
 */
public class ProductHandler {
    private CassandraService cassandraService;

    public ProductHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

}
