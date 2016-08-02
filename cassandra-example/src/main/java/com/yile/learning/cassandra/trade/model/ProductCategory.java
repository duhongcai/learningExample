package com.yile.learning.cassandra.trade.model;

/**
 * 产品分类信息,分类名称作为ColumnFamily的key，实际存储数据如:
 * <p/>
 * <code>
 * 	Buyer = {
 * 		sport:{  //分类名称
 * 			{name:"8177ec99-b9df-11df-94a6-1b9323c915f2",value:"",timestamp:123456789}
 * 		}
 * 	}
 * </code>
 * @author justin.liang
 */
public class ProductCategory {
    private String name;
    private String value;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
