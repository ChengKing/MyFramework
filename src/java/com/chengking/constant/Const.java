/*
 * Title: 	  MyFrameProject
 * Copyright: Copyright (c) 2004-2014, company. All rights reserved.
 * License:   see the license file.
 * Company:   monkey.com
 * 
 * Created: MONKEY@2014年5月17日 上午9:31:38
 */

package com.chengking.constant;

 /**
 * 系统相关的常量类 <BR>
 * 
 * @author ChengKing
 * @since MONKEY@2014年5月17日
 */
public class Const {

	/**
	 * 是否启用mongo
	 */
	public static final String SYS_MONGO_IS_USE = "sys.mongo.is.use";
	/**
	 * 默认mongo服务地址
	 */
	public static final String SYS_MONGO_DEFAULT_SERVERS="127.0.0.1:27017"; 
	
	/**
	 * mongo服务器地址
	 */
	public static final String SYS_MONGO_SERVERS = "sys.mongo.servers";
	
	/**
	 * mongo数据库名
	 */
	public static final String SYS_MONGO_DBNAME = "sys.mongo.dbname";

	/**
	 * 默认mongo数据名
	 */
	public static final String SYS_MONGO_DEFAULT_DBNAME = "test";
}
