package com.yile.learning.cassandra.trade.handler;

import com.yile.learning.cassandra.service.CassandraService;

/**
 * @author justin.liang
 */
public class SellerHandler {
    private CassandraService cassandraService;

    public SellerHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }
}
