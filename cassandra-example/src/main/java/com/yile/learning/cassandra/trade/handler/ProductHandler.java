package com.yile.learning.cassandra.trade.handler;

import com.rabbitframework.commons.utils.UUIDUtils;
import com.yile.learning.cassandra.hector.Serializers;
import com.yile.learning.cassandra.service.CassandraService;
import com.yile.learning.cassandra.trade.model.Product;
import com.yile.learning.cassandra.utils.Constants;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.*;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.rabbitframework.commons.utils.CodecUtils.byteBuffer;
import static com.yile.learning.cassandra.hector.Serializers.be;
import static com.yile.learning.cassandra.hector.Serializers.se;

/**
 * @author justin.liang
 */
public class ProductHandler {
    private static final Logger logger = LogManager.getLogger(ProductHandler.class);
    private CassandraService cassandraService;

    public ProductHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

    public void insert(Product product) {
        Keyspace keyspace = cassandraService.getKeyspace(Constants.KEYSPACENAME);
        Mutator<ByteBuffer> batch = HFactory.createMutator(keyspace, Serializers.be);
        ByteBuffer uuid = byteBuffer(product.getUuid());
        String cf = Product.COLUMN_FAMILY;
        batch.addInsertion(uuid, cf, HFactory.createColumn("name", byteBuffer(product.getName()), se, be));
        batch.addInsertion(uuid, cf, HFactory.createColumn("sellerUserName", byteBuffer(product.getSellerUserName()), se, be));
        batch.addInsertion(uuid, cf, HFactory.createColumn("desc", byteBuffer(product.getDesc()), se, be));
        batch.addInsertion(uuid, cf, HFactory.createColumn("price", byteBuffer(String.valueOf(product.getPrice())), se, be));
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
        sliceQuery.setColumnFamily(Product.COLUMN_FAMILY);
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
        RangeSlicesQuery<ByteBuffer, String, String> sliceQuery = HFactory.createRangeSlicesQuery(keyspace, be, se, se);
//        sliceQuery.setKeys("","");
        sliceQuery.setRange(null, null, false, 100);
        sliceQuery.setColumnFamily(Product.COLUMN_FAMILY);
        QueryResult<OrderedRows<ByteBuffer, String, String>> result = sliceQuery.execute();
        Rows<ByteBuffer, String, String> rows = result.get();
        Iterator<Row<ByteBuffer, String, String>> iterator = rows.iterator();
        while (iterator.hasNext()) {
            Row<ByteBuffer, String, String> row = iterator.next();
            ByteBuffer byteBufferKey = row.getKey();
            UUID uuid = UUIDUtils.uuid(byteBufferKey);
            logger.info("uuid:" + uuid.toString());
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
        String cf = Product.COLUMN_FAMILY;
        batch.addDeletion(key, cf);
        batch.execute();
    }
}
