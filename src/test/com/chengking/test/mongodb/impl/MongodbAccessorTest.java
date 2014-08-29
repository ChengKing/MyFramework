package com.chengking.test.mongodb.impl;

import java.util.UUID;

import com.chengking.model.bo.User;
import com.chengking.test.mongodb.BaseMongodbTest;
import com.google.gson.Gson;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class MongodbAccessorTest extends BaseMongodbTest {

	/**
	 */
	public void testAdd() {

		User user = new User();
		user.setUserName("monkey1");
		user.setPassword("111111");
		user.setUserId(1);
		user.setUuid(UUID.randomUUID().toString());
		user.setCreateTime(System.currentTimeMillis());

		Gson gson = new Gson();
		// 转换成json字符串，再转换成DBObject对象
		DBObject dbObject = (DBObject) JSON.parse(gson.toJson(user));

		userCollection.insert(dbObject);

		DBCursor dbCursor = userCollection.find();
		while (dbCursor.hasNext()) {
			System.out.println(dbCursor.next());
		}
	}

	/**
	 */
	public void testUpdate() {

	}

	/**
	 */
	public void testQuery() {

	}

	/**
	 */
	public void testDelete() {

	}

}
