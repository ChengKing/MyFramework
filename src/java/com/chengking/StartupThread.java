/*
 * Title: 	  MyFrameProject
 * Copyright: Copyright (c) 2004-2014, company. All rights reserved.
 * License:   see the license file.
 * Company:   monkey.com
 * 
 * Created: DF-ChengK@2014年7月31日 上午9:37:35
 */

package com.chengking;

import org.apache.log4j.Logger;

 /**
 * 系统启动线程，系统不随应用服务器启动而启动，由此线程启动系统，并初始上下文 <BR>
 * 
 * 该线程只随系统启动一次
 * 
 * @author ChengKing
 * @since 2014年7月31日
 */
public class StartupThread extends Thread {
	/**
	 * 系統上下文
	 */
	private MkContext context;
	
	/**
	 * 日志
	 */
	private static final Logger logger = Logger.getLogger(StartupThread.class);
	
	public StartupThread(MkContext context){
		this.context = context;
	}
	
	@Override
	public void run(){
		try {
			sleep(10 * 1000L);
		} catch (InterruptedException e) {
			logger.error("startup thread failed !", e);
		}
		if(context ==  null){
			throw new RuntimeException("project context ["+context+"] is null ! failed to startup system !");
		}
		context.start();
	}
}
