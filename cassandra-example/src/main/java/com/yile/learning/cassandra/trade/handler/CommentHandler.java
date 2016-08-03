package com.yile.learning.cassandra.trade.handler;


import com.rabbitframework.commons.utils.CodecUtils;
import com.rabbitframework.commons.utils.UUIDUtils;
import com.yile.learning.cassandra.hector.Serializers;
import com.yile.learning.cassandra.service.CassandraService;
import com.yile.learning.cassandra.trade.model.Comment;
import com.yile.learning.cassandra.utils.Constants;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.*;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.RangeSuperSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.rabbitframework.commons.utils.CodecUtils.byteBuffer;
import static com.rabbitframework.commons.utils.CodecUtils.toString;
import static com.yile.learning.cassandra.hector.Serializers.be;
import static com.yile.learning.cassandra.hector.Serializers.se;

/**
 * @author justin.liang
 */
public class CommentHandler {
    private static final Logger logger = LogManager.getLogger(CommentHandler.class);
    private CassandraService cassandraService;

    public CommentHandler(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

    public void insert(Comment comment) {
        Keyspace keyspace = cassandraService.getKeyspace(Constants.KEYSPACENAME);
        Mutator<ByteBuffer> batch = HFactory.createMutator(keyspace, Serializers.be);
        ByteBuffer uuidKey = byteBuffer(comment.getUuid());
        String cf = Comment.COLUMN_FAMILY;

        ByteBuffer nameKey = byteBuffer(comment.getName());
        List<HColumn<String, ByteBuffer>> columns = new ArrayList<HColumn<String, ByteBuffer>>();
        columns.add(HFactory.createColumn("content", byteBuffer(comment.getContent()), se, be));
        columns.add(HFactory.createColumn("commentUserName", byteBuffer(comment.getCommentUserName()), se, be));
        batch.addInsertion(uuidKey, cf, HFactory.createSuperColumn(nameKey, columns, be, se, be));
        batch.execute();
    }

    public void queryAll() {
        Keyspace keyspace = cassandraService.getKeyspace(Constants.KEYSPACENAME);

        RangeSuperSlicesQuery<ByteBuffer, ByteBuffer, String, String> sliceQuery = HFactory.
                createRangeSuperSlicesQuery(keyspace, be, be, se, se);
//        sliceQuery.setKeys("","");
        sliceQuery.setRange(null, null, false, 100);
        sliceQuery.setColumnFamily(Comment.COLUMN_FAMILY);

        QueryResult<OrderedSuperRows<ByteBuffer, ByteBuffer, String, String>> result = sliceQuery.execute();
        OrderedSuperRows<ByteBuffer, ByteBuffer, String, String> rows = result.get();
        Iterator<SuperRow<ByteBuffer, ByteBuffer, String, String>> iterator = rows.iterator();
        while (iterator.hasNext()) {
            SuperRow<ByteBuffer, ByteBuffer, String, String> row = iterator.next();
            ByteBuffer byteBufferKey = row.getKey();
            UUID uuid = UUIDUtils.uuid(byteBufferKey);
            logger.info("uuid:" + uuid.toString());
            SuperSlice<ByteBuffer, String, String> superSlice = row.getSuperSlice();
            List<HSuperColumn<ByteBuffer, String, String>> lists = superSlice.getSuperColumns();
            for (HSuperColumn<ByteBuffer, String, String> hSuperColumn : lists) {
                UUID str = UUIDUtils.uuid(hSuperColumn.getName());
                logger.info("superName:" + str.toString());
                List<HColumn<String, String>> hColumns = hSuperColumn.getColumns();
                for (HColumn<String, String> hColumn : hColumns) {
                    logger.info("columnName:" + hColumn.getName() + ",columnValue:" + hColumn.getValue());
                }
            }
        }
    }

    public void delByKey(String key) {
        Keyspace keyspace = cassandraService.getKeyspace(Constants.KEYSPACENAME);
        Mutator<String> batch = HFactory.createMutator(keyspace, Serializers.se);
        String cf = Comment.COLUMN_FAMILY;
        batch.addDeletion(key, cf);
        batch.execute();
    }
}
