package com.yile.learning.cassandra.trade;

import com.yile.learning.cassandra.service.CassandraService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author justin.liang
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    private CassandraService cassandraService;

    private void before() {
        cassandraService = new CassandraService("Test Cluster", "192.168.2.113:9160");
    }

    private void after() {
        try {
            cassandraService.destory();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.before();

        app.after();
    }


}
