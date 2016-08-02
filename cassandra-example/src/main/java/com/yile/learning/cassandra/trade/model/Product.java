package com.yile.learning.cassandra.trade.model;

import java.util.UUID;

/**
 * 用于保存产品的信息,使用TimeUUID作为ColumnFamily的key，实际存储数据如:
 * <p/>
 * <code>
 * 	Product = {
 * 		8177ec99-b9df-11df-94a6-1b9323c915f2:{
 * 			{name:"name",value:"足球",timestamp:123456789}
 * 			{name:"sellerUserName",value:"lily",timestamp:123456789}
 * 			{name:"desc",value:"白色真皮球鞋",timestamp:123456789}
 * 			{name:"price",value:"98.8",timestamp:123456789}
 * 		}		
 * 	}
 * </code>
 * 
 * @author justin.liang
 */

public class Product {
	public static final String COLUMN_FAMILY = "Product";
	
	private UUID uuid;

	private String name;

	private String sellerUserName;

	private String desc;

	private double price;

	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSellerUserName() {
		return sellerUserName;
	}
	public void setSellerUserName(String sellerUserName) {
		this.sellerUserName = sellerUserName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
}
