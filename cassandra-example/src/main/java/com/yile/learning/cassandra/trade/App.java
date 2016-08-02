package com.yile.learning.cassandra.trade;

import com.yile.learning.cassandra.service.CassandraService;
import com.yile.learning.cassandra.trade.handler.*;
import com.yile.learning.cassandra.trade.model.Buyer;
import com.yile.learning.cassandra.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 交易系统入口类
 *
 * @author justin.liang
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    private DataBaseHandler dataBaseHandler;
    private BuyerHandler buyerHandler;
    private CommentHandler commentHandler;
    private ProductHandler productHandler;
    private SellerHandler sellerHandler;

    public static void main(String[] args) {
        App app = new App();
        app.before();
//        app.init(); //第一次运行时初始化.
        app.insert();
        app.query();
        app.del();
        app.after();
    }


    public void init() {
        dataBaseHandler.createKeyspace(Constants.KEYSPACENAME);
        dataBaseHandler.setupColumnFamily();
    }

    public void insert() {
//        insertBuyer();  //初始化插入买家信息
    }

    public void query() {
        queryBuyer();
    }

    public void del() {
//        buyerHandler.delByKey("zhangsan");
    }

    private void queryBuyer() {
        buyerHandler.queryByKey("zhangsan");
    }

    private void insertBuyer() {
        Buyer buyer = new Buyer();
        buyer.setAddress("湖南岳阳市");
        buyer.setAge(20);
        buyer.setName("张三");
        buyer.setSex("男");
        buyer.setUserName("zhangsan");

        Buyer buyer2 = new Buyer();
        buyer2.setAddress("广东深圳");
        buyer2.setAge(20);
        buyer2.setName("李四");
        buyer2.setSex("男");
        buyer2.setUserName("lisi");
        buyerHandler.insertBuyer(buyer);
        buyerHandler.insertBuyer(buyer2);
    }

    private void before() {
        CassandraService cassandraService = new CassandraService("Test Cluster",
                "192.168.2.113:9160", null, null);
        dataBaseHandler = new DataBaseHandler(cassandraService);
        buyerHandler = new BuyerHandler(cassandraService);
        commentHandler = new CommentHandler(cassandraService);
        productHandler = new ProductHandler(cassandraService);
        sellerHandler = new SellerHandler(cassandraService);
    }

    private void after() {
        dataBaseHandler.destroy();
    }

}
