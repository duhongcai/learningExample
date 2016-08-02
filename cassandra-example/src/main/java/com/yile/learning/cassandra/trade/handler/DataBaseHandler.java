package com.yile.learning.cassandra.trade.handler;

import com.yile.learning.cassandra.service.CassandraService;
import com.yile.learning.cassandra.trade.model.*;
import com.yile.learning.cassandra.utils.Constants;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.factory.HFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author justin.liang
 */
public class DataBaseHandler {
    private static final Logger logger = LogManager.getLogger(DataBaseHandler.class);
    private CassandraService cassandraService;

    public DataBaseHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

    public void createKeyspace(String keyspace) {
        cassandraService.createKeyspace(keyspace);
    }

    public void setupColumnFamily() {
        cassandraService.createColumnFamily(Constants.KEYSPACENAME, createBuyerColumnFamily());
        cassandraService.createColumnFamily(Constants.KEYSPACENAME, createCommentColumnFamily());
        cassandraService.createColumnFamily(Constants.KEYSPACENAME, createProductColumnFamily());
        cassandraService.createColumnFamily(Constants.KEYSPACENAME, createProductCategoryColumnFamily());
        cassandraService.createColumnFamily(Constants.KEYSPACENAME, createSellerColumnFamily());
    }

    private ColumnFamilyDefinition createBuyerColumnFamily() {
        // List<ColumnDefinition> buyerCDefs = new ArrayList<ColumnDefinition>();
        ColumnFamilyDefinition columnFamilyDefinition = HFactory
                .createColumnFamilyDefinition(Constants.KEYSPACENAME, Buyer.COLUMN_FAMILY,
                        ComparatorType.UTF8TYPE);
        return columnFamilyDefinition;
    }

    private ColumnFamilyDefinition createCommentColumnFamily() {
        ColumnFamilyDefinition columnFamilyDefinition = HFactory
                .createColumnFamilyDefinition(Constants.KEYSPACENAME, Comment.COLUMN_FAMILY,
                        ComparatorType.TIMEUUIDTYPE);
        columnFamilyDefinition.setColumnType(ColumnType.SUPER);
        return columnFamilyDefinition;
    }

    private ColumnFamilyDefinition createProductColumnFamily() {
        ColumnFamilyDefinition columnFamilyDefinition = HFactory
                .createColumnFamilyDefinition(Constants.KEYSPACENAME, Product.COLUMN_FAMILY,
                        ComparatorType.UTF8TYPE);

        return columnFamilyDefinition;
    }

    private ColumnFamilyDefinition createProductCategoryColumnFamily() {
        ColumnFamilyDefinition columnFamilyDefinition = HFactory
                .createColumnFamilyDefinition(Constants.KEYSPACENAME, ProductCategory.COLUMN_FAMILY,
                        ComparatorType.TIMEUUIDTYPE);
        return columnFamilyDefinition;
    }

    private ColumnFamilyDefinition createSellerColumnFamily() {
        ColumnFamilyDefinition columnFamilyDefinition = HFactory
                .createColumnFamilyDefinition(Constants.KEYSPACENAME, Seller.COLUMN_FAMILY,
                        ComparatorType.UTF8TYPE);
        return columnFamilyDefinition;
    }

    public void destroy() {
        try {
            cassandraService.destroy();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new RuntimeException(e);
        }
    }

}
