package com.yile.learning.oauth.utils;

import com.rabbitframework.commons.codec.Md5Hash;
import com.rabbitframework.commons.utils.UUIDGenerator;

public class DataTest {
	public static void main(String[] args) {
		String str = Md5Hash.md5("123");
		System.out.println(str);

		String uuid = UUIDGenerator.getTimeUUID36();
		System.out.println(uuid);
	}
}
