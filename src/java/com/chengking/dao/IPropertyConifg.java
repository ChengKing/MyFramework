package com.chengking.dao;
import java.util.Map;

public interface IPropertyConifg {
	
	/**
	 * 配置项启动
	 */
	public void start(String file);
	
	/**
	 * ֹͣ配置项停止
	 */
	public void stop();
	
	/**
	 * 重新加载配置项
	 */
	public void reload();
	
	/**
	 *  获取字符串类型的配置项
	 * 
	 * @param key 配置项名
	 * @param defaultValue 默认值
	 * @return
	 */
	public String getConfig(String key,String defaultValue);
	
	/**
	 * 获取整形形式的配置项
	 * 
	 * @param key 配置项名
	 * @param defaultValue 默认值
	 * @return 如果配置项不存在，则返回null
	 */
	public int getConfigAsInt(String key,int defaultValue);
	
	/**
	 * 获取布尔形式的配置项
	 * 
	 * @param key 配置项名
	 * 
	 * @return 如果配置项不存在则返回null，其他情况则返回true或者false
	 */
	public boolean getConifgAsBoolean(String key);
	
	/**
	 * 获取所有配置项（map）
	 * 
	 * @return
	 */
	public Map<String,Object> allConfigs();

	/**
	 * toString 输出所有配置项信息
	 * 
	 * @return
	 */
	public String toString();
}
