package com.yile.learning.cassandra.trade.model;

/**
 * 用于保存卖家的信息,卖家的用户名作为ColumnFamily的key，实际存储数据如:
 * <p/>
 * <code>
 * 	Seller = {
 * 		lily:{  //卖家名称,对称字段为userName
 * 			{name:"name",value:"李娜",timestamp:123456789}
 * 			{name:"age",value:"23",timestamp:123456789}
 * 			{name:"sex",value:"女",timestamp:123456789}
 * 			{name:"address",value:"辽宁省大连市",timestamp:123456789}
 * 		}		
 * 	}
 * </code>
 * 
 * @author justin.liang
 */
public class Seller {
	public static final String COLUMN_FAMILY = "Seller";
	private String userName;
	private String name;
	private int age;
	private String sex;
	private String address;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
