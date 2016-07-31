package com.yile.learning.cassandra.trade.handler;

import com.yile.learning.cassandra.service.CassandraService;

public class BuyerHandler {
    private CassandraService cassandraService;

    public BuyerHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

}
