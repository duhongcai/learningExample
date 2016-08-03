package com.yile.learning.cassandra.trade.handler;

import com.yile.learning.cassandra.hector.Serializers;
import com.yile.learning.cassandra.service.CassandraService;
import com.yile.learning.cassandra.trade.model.Buyer;
import com.yile.learning.cassandra.utils.Constants;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.*;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import static com.rabbitframework.commons.utils.CodecUtils.byteBuffer;
import static com.yile.learning.cassandra.hector.Serializers.be;
import static com.yile.learning.cassandra.hector.Serializers.se;

public class BuyerHandler {
    private static final Logger logger = LogManager.getLogger(BuyerHandler.class);
    private CassandraService cassandraService;

    public BuyerHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

    public void insertBuyer(Buyer buyer) {
        Keyspace keyspace = cassandraService.getKeyspace(Constants.KEYSPACENAME);
        Mutator<ByteBuffer> batch = HFactory.createMutator(keyspace, Serializers.be);
        ByteBuffer username = byteBuffer(buyer.getUserName());
        String cf = Buyer.COLUMN_FAMILY;
        batch.addInsertion(username, cf, HFactory.createColumn("name", byteBuffer(buyer.getName()), se, be));
        batch.addInsertion(username, cf, HFactory.createColumn("address", byteBuffer(buyer.getAddress()), se, be));
        batch.addInsertion(username, cf, HFactory.createColumn("age", byteBuffer(String.valueOf(buyer.getAge())), se, be));
        batch.addInsertion(username, cf, HFactory.createColumn("sex", byteBuffer(buyer.getSex()), se, be));
        batch.execute();
    }

    /**
     * 根据key查询数据
     *
     * @param key
     */
    public void queryByKey(String key) {
        Keyspace keyspace = cassandraService.getKeyspace(Constants.KEYSPACENAME);
        SliceQuery<String, String, String> sliceQuery = HFactory.createSliceQuery(keyspace, Serializers.se,
                Serializers.se, Serializers.se);
        sliceQuery.setKey(key); //必须设置,且不允许为空
        sliceQuery.setRange(null, null, false, 1000); //必须设置
        sliceQuery.setColumnFamily(Buyer.COLUMN_FAMILY);
        QueryResult<ColumnSlice<String, String>> queryResult = sliceQuery.execute();
        ColumnSlice<String, String> columnSlice = queryResult.get();
        List<HColumn<String, String>> results = columnSlice.getColumns();
        for (HColumn<String, String> hColumn : results) {
            String name = hColumn.getName();
            String value = hColumn.getValue();
            logger.debug("name:{},value:{}", name, value);
        }
    }

    public void queryAll() {
        Keyspace keyspace = cassandraService.getKeyspace(Constants.KEYSPACENAME);
        RangeSlicesQuery<String, String, String> sliceQuery = HFactory.createRangeSlicesQuery(keyspace, se, se, se);
//        sliceQuery.setKeys("","");
        sliceQuery.setRange(null, null, false, 100);
        sliceQuery.setColumnFamily(Buyer.COLUMN_FAMILY);
        QueryResult<OrderedRows<String, String, String>> result = sliceQuery.execute();
        Rows<String, String, String> rows = result.get();
        Iterator<Row<String, String, String>> iterator = rows.iterator();
        while (iterator.hasNext()) {
            Row<String, String, String> row = iterator.next();
            String key = row.getKey();
            logger.info("key:" + key);
            ColumnSlice<String, String> columnSlice = row.getColumnSlice();
            List<HColumn<String, String>> lists = columnSlice.getColumns();
            for (HColumn<String, String> hColumn : lists) {
                logger.info("columnName:" + hColumn.getName() + ",columnValue:" + hColumn.getValue());
            }
        }
    }

    public void delByKey(String key) {
        Keyspace keyspace = cassandraService.getKeyspace(Constants.KEYSPACENAME);
        Mutator<String> batch = HFactory.createMutator(keyspace, Serializers.se);
        String cf = Buyer.COLUMN_FAMILY;
        batch.addDeletion(key, cf);
        batch.execute();
    }
}
