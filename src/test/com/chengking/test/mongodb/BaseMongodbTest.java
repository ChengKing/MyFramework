package com.chengking.test.mongodb;
import java.net.UnknownHostException;
import junit.framework.TestCase;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class BaseMongodbTest extends TestCase {

	private static final String host = "localhost";
	
	private static final int port = 27017;
	
	private static final String database = "junitTest";
	
	protected DBCollection userCollection;
	
	protected MongoClient mongoClient; 
	
	public void setUp() throws Exception{
		super.setUp();
		init();
	}
	
	/**
	 * 启动mongo db
	 */
	private void init() {
		try {
			mongoClient = new MongoClient(host, port);
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		}
		assertNotNull(mongoClient);
		DB db = mongoClient.getDB(database);
		assertNotNull(db);
		userCollection = db.getCollection("test_user");
		assertNotNull(userCollection);
	}
	
	/**
	 * 停止mongo db
	 */
	public void tearDown() throws Exception{
		super.tearDown();
		assertNotNull(mongoClient);
		DB db = mongoClient.getDB(database);
		assertNotNull(db);
		db.dropDatabase();
		mongoClient.close();
	}
}
