package com.chengking.dao.configImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.chengking.dao.IPropertyConifg;

public abstract class AbstractPropertyConfig implements IPropertyConifg {
	
	/**
	 * map形式的配置信息
	 */
	protected Map<String,Object> configMap = new HashMap<String, Object>();
	
	/**
	 * properties形式的配置项集合
	 */
	protected Properties configProperties = new Properties();
	
	/**
	 * 重新加载配置项
	 */
	public void reload() {
		
	}
}
