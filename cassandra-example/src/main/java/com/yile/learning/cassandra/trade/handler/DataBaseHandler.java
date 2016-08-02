package com.yile.learning.cassandra.trade.handler;

import com.yile.learning.cassandra.service.CassandraService;
import com.yile.learning.cassandra.trade.model.Buyer;
import com.yile.learning.cassandra.utils.Constants;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.ArrayList;
import java.util.List;

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

    public void setupColumnFamily() {
        //创建买家列族
        cassandraService.createColumnFamily(Constants.KEYSPACENAME, createBuyerColumnFamily());
    }

    private ColumnFamilyDefinition createBuyerColumnFamily() {
        List<ColumnDefinition> buyerCDefs = new ArrayList<ColumnDefinition>();

        ColumnFamilyDefinition columnFamilyDefinition = HFactory.createColumnFamilyDefinition(Constants.KEYSPACENAME, Buyer.COLUMN_FAMILY, ComparatorType.UTF8TYPE, buyerCDefs);
        return columnFamilyDefinition;

    }

}
