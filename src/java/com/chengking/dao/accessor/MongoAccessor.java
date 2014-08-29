package com.chengking.dao.accessor;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.chengking.constant.Const;
import com.chengking.dao.IDBAccessor;
import com.chengking.dao.IPropertyConifg;
import com.chengking.dao.sch.SearchCondition;
import com.chengking.model.ObjectEntry;
import com.chengking.model.bo.User;
import com.common.util.StringHelper;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

public class MongoAccessor implements IDBAccessor {

	/**
	 * 日志
	 */
	private static final Logger LOG = Logger.getLogger(MongoAccessor.class);

	/**
	 * 是否初始化成功,只有初始化成功，才可以使用mongodb
	 */
	private boolean init = false;

	/**
	 */
	private MongoClient mongoClient;

	/**
	 * 类似 数据库实例
	 */
	private DB database;

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean isInit) {
		this.init = isInit;
	}

	@Override
	public void start(IPropertyConifg config) {
		if(config.getConifgAsBoolean(Const.SYS_MONGO_IS_USE)){
			LOG.info("mongo db accessor switch not open , not to start !");
			return;
		}
		LOG.info("mongo db accessor begin to start ...");
		initClient(config);
	}

	/**
	 * 初始化Mongo连接
	 * 
	 * @param config
	 *            配置信息
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月17日
	 */
	private void initClient(IPropertyConifg config) {
		String mongoServers = config.getConfig(Const.SYS_MONGO_SERVERS,
				Const.SYS_MONGO_DEFAULT_SERVERS);
		if (StringHelper.isEmpty(mongoServers)) {
			LOG.error("mongo servers from properties is empty . not init mongo client !");
			return;
		}
		String[] servers = StringHelper.split(mongoServers, " ");
		List<ServerAddress> serverAddress = buildMongoServer(servers);
		mongoClient = new MongoClient(serverAddress);
		if (mongoClient != null) {
			String dbName = config.getConfig(Const.SYS_MONGO_DBNAME,
					Const.SYS_MONGO_DEFAULT_DBNAME);
			database = mongoClient.getDB(dbName);
			init = true;
		}
	}

	/**
	 * 构造mongo Server
	 * 
	 * @param servers
	 * @param serverAddress
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月17日
	 */
	private List<ServerAddress> buildMongoServer(String[] servers) {
		List<ServerAddress> mongoServers = new ArrayList<ServerAddress>();
		for (String server : servers) {
			String[] hostAndPort = StringHelper.splitAndTrim(server, ":");
			if (hostAndPort == null || hostAndPort.length != 2) {
				continue;
			}
			String host = hostAndPort[0];
			int port = StringHelper.parseInt(hostAndPort[1], 27017);
			ServerAddress address = null;
			try {
				address = new ServerAddress(host, port);
			} catch (UnknownHostException e) {
				LOG.error("create new mongo server failed !", e);
			}
			if (address == null) {
				continue;
			}
			mongoServers.add(address);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("build mongo servers [" + mongoServers
					+ "] by properties [" + servers + "] !");
		}
		return mongoServers;
	}

	@Override
	public void stop() {
		if (mongoClient != null) {
			mongoClient.close();
		}
		init = false;
	}

	@Override
	public void add(Class<?> clazz, ObjectEntry obj) throws Exception {
		if (clazz == null) {
			LOG.error("add obj failed , because clazz is null !");
			return;
		}
		if (obj == null) {
			LOG.error("add obj failed , beacuse obj is null !");
			return;
		}
		// 持久化时统一设置uuid，其他地方均不设值
		obj.setUuid(UUID.randomUUID().toString());
		obj.setCreateTime(System.currentTimeMillis());
		String className = clazz.getSimpleName();
		DBCollection collection = database.getCollection(className);
		Gson gson = new Gson();
		// 转换成json字符串，再转换成DBObject对象
		DBObject dbObject = (DBObject) JSON.parse(gson.toJson(obj));
		collection.save(dbObject);
	}

	@Override
	public void update(Class<?> clazz, ObjectEntry obj) throws Exception {
		if (clazz == null) {
			LOG.error("update obj failed , because clazz is null !");
			return;
		}
		if (obj == null) {
			LOG.error("update obj failed , beacuse obj is null !");
			return;
		}

		String uuid = obj.getUuid();
		if (StringHelper.isEmpty(uuid)) {
			LOG.error("the objEntry [" + obj
					+ "] uuid is empty , so update obj failed !");
			return;
		}
		String className = clazz.getSimpleName();
		DBCollection collection = database.getCollection(className);
		DBObject oldObj = collection.findOne(new BasicDBObject("uuid", uuid));
		if (oldObj == null) {
			LOG.error("find oldObj from mongo by uuid [" + uuid
					+ "] is null , so update obj failed !");
			return;
		}
		Gson gson = new Gson();
		DBObject newObj = (DBObject) JSON.parse(gson.toJson(obj));
		collection.update(oldObj, newObj);
	}

	@Override
	public void delete(Class<?> clazz, ObjectEntry obj) throws Exception {
		if (clazz == null) {
			LOG.error("delete obj failed , because clazz is null !");
			return;
		}
		if (obj == null) {
			LOG.error("delete obj failed , beacuse obj is null !");
			return;
		}

		String uuid = obj.getUuid();
		if (StringHelper.isEmpty(uuid)) {
			LOG.error("the objEntry [" + obj
					+ "] uuid is empty , so delete obj failed !");
			return;
		}

		String className = clazz.getSimpleName();
		DBCollection collection = database.getCollection(className);
		DBObject objInDB = collection.findOne(new BasicDBObject("uuid", uuid));
		collection.remove(objInDB);
	}

	@Override
	public ObjectEntry findObj(Class<?> clazz, Object key, Object value)
			throws Exception {
		if (clazz == null) {
			LOG.error("add obj failed , because clazz is null !");
			return null;
		}
		String className = clazz.getSimpleName();
		DBCollection collection = database.getCollection(className);
		DBObject dbObj = collection.findOne(new BasicDBObject(
				(key == null) ? "" : key.toString(), value));
		Gson gson = new Gson();
		return gson.fromJson(dbObj.toString(), User.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chengking.dao.IDBAccessor#findObjs(java.lang.Class,
	 * com.chengking.dao.sch.SearchCondition)
	 */
	@Override
	public List<ObjectEntry> findObjs(Class<?> clazz, SearchCondition sc)
			throws Exception {
		if (clazz == null) {
			LOG.error("clazz is null , so return empty objEntry list !");
			return new ArrayList<ObjectEntry>();
		}

		DBCollection collection = database.getCollection(clazz.getSimpleName());
		if (collection == null) {
			LOG.error("collection is null by className = ["
					+ clazz.getSimpleName()
					+ "] , so return empty objEntry list !");
		}
		if (sc == null) {
			LOG.info("search condition is null , so new a sc !");
			sc = new SearchCondition();
		}

		for (int i = 0; i < sc.getCount(); i++) {
			String key = sc.getKey(i);
			Object valueObj = sc.getValue(i);
			if(StringHelper.isEmpty(key)){
				continue;
			}
		}

		return null;
	}
}
