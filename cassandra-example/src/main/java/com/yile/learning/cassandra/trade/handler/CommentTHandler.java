package com.yile.learning.cassandra.trade.handler;


import com.yile.learning.cassandra.service.CassandraService;

/**
 * @author justin.liang
 */
public class CommentTHandler {
    private CassandraService cassandraService;

    public CommentTHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }
}
