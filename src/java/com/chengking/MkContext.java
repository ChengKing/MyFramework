/*
 * Title: 	  MyFrameProject
 * Copyright: Copyright (c) 2004-2014, company. All rights reserved.
 * License:   see the license file.
 * Company:   monkey.com
 * 
 * Created: DF-ChengK@2014年7月31日 上午9:51:25
 */
package com.chengking;
import org.apache.log4j.Logger;

import com.chengking.dao.IPropertyConifg;
import com.chengking.dao.configImpl.PropertiesConfig;
import com.chengking.dao.configImpl.XMLConfig;
import com.common.util.FileUtil;
 /**
 * 系统上下文 <BR>
 * 
 * @author ChengKing
 * @since 2014年7月31日
 */
public class MkContext {
	
	private static final Logger logger = Logger.getLogger(MkContext.class);
	
	private String globalCfg;
	
	/**
	 * Returns the {@link #globalCfg}.
	 * @return the globalCfg.
	 */
	
	public String getGlobalCfg() {
		return globalCfg;
	}


	/**
	 * Set {@link #globalCfg}.
	 * @param globalCfg The globalCfg to set.
	 */
	public void setGlobalCfg(String globalCfg) {
		this.globalCfg = globalCfg;
	}


	public void start(){
		IPropertyConifg conf = loadCfg();
		// 首次启动dbaccessor完成数据库的自动创建
		initDbAccessor(conf);
		System.out.println("----------------------");
		System.out.println(" Begin Startup Mk ...");
	}

	
	  /**
	 * @param conf
	 * @since v1.0
	 * @creator DF-ChengK @ 2014年8月1日
	 */
	private void initDbAccessor(IPropertyConifg conf) {
	}


	/**
	  * 装载系统配置项信息
	 * @return
	 * @since v1.0
	 * @creator 2014年7月31日
	 */
	private IPropertyConifg loadCfg() {
		if(globalCfg == null){
			logger.error("the globalCfg ["+globalCfg+"] is null , failed to start system !");
			throw new RuntimeException("failed to start context , the globalconfig file is null !");
		}
		String fileType = FileUtil.getFileExtension(globalCfg);
		if(!isValidCfgType(fileType)){
			logger.error("the globalCfg ["+globalCfg+"] is illegal , failed to start system !");
			throw new RuntimeException("failed to start context , the globalconfig file illegal !");
		}
		IPropertyConifg cfg = null;
		if("xml".equals(fileType)){
			cfg = new XMLConfig();
		}else{
			cfg = new PropertiesConfig();
		}
		cfg.start(globalCfg);
		return cfg;
	}

	/**
	 * 判断全局配置文件格式是否合法
	 * 
	 * @param fileType 系统配置文件类型
	 * @return 目前支持xml,properties,ini,conf格式的配置文件
	 * @since v1.0
	 * @creator 2014年7月31日
	 */
	private boolean isValidCfgType(String fileType){
		if("xml".equals(fileType)){
			return true;
		}else if("properties".equals(fileType)){
			return true;
		}else if("ini".equals(fileType)){
			return true;
		}else if("conf".equals(fileType)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 停止系统上下文
	 * 
	 * @since v1.0
	 * @creator 2014年7月31日
	 */
	public void stop(){
		System.out.println("----------------------");
		System.out.println(" Begin Stop Mk ...");
	}
}
