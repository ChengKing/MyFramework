package com.chengking.dao;

import java.util.List;

import com.chengking.dao.sch.SearchCondition;
import com.chengking.model.ObjectEntry;

public interface IDBAccessor {
	
	/**
	 * 启动数据访问接口
	 */
	public void start(IPropertyConifg config);

	/**
	 * 停止数据访问接口
	 */
	public void stop();

	/**
	 * 添加数据记录
	 * 
	 * @param clazz 对象类型
	 * @param obj 待添加对象
	 * @throws Exception
	 */
	public void add(Class<?> clazz,ObjectEntry obj) throws Exception;
	
	/**
	 * 更新记录
	 * 
	 * @param clazz 对象类型
	 * @param obj 待更新对象
	 * @throws Exception
	 */
	public void update(Class<?> clazz,ObjectEntry obj) throws Exception;
	
	/**
	 * 删除记录
	 * 
	 * @param clazz 对象类型
	 * @param obj 待删除的对象
	 * @throws Exception
	 */
	public void delete(Class<?> clazz,ObjectEntry obj)throws Exception;
	
	/**
	 * 查询对象
	 * 
	 * @param clazz 对象类型
	 * @param key 查询条件名
	 * @param value 查询条件值
	 * @throws Exception
	 */
	public ObjectEntry findObj(Class<?> clazz ,Object key,Object value)throws Exception;
	
	/**
	 * 查询满足检索条件的集合
	 * 
	 * @param clazz 待查询对象实例
	 * @param sc 检索条件集合
	 * @return
	 * @throws Exception
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月17日
	 */
	public List<ObjectEntry> findObjs(Class<?> clazz, SearchCondition sc) throws Exception;
}
