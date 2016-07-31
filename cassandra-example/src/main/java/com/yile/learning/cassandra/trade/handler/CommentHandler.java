package com.yile.learning.cassandra.trade.handler;


import com.yile.learning.cassandra.service.CassandraService;

/**
 * @author justin.liang
 */
public class CommentHandler {
    private CassandraService cassandraService;

    public CommentHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }
}
