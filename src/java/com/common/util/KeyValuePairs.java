package com.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装名值对集合的对象. <BR>
 * 
 * @author ChengKing
 */
public class KeyValuePairs {
    
    @SuppressWarnings("rawtypes")
	private Map map = new HashMap();
    @SuppressWarnings("rawtypes")
	private List lst = new ArrayList();
    
    /**
     * 增加一个新的名值对.
     * @param key 如果已存在, 则会替换旧值
     * @param value
     */
    @SuppressWarnings("unchecked")
	public void add(String key, Object value) {
        if (key == null) {
            return;
        }
        
        map.put(key, value);
        lst.add(key);
    }
    
    /**
     * 删除给定名称的名值对.
     */
    public void remove(String key) {
        if (key == null) {
            return;
        }
        
        map.remove(key);
        lst.remove(key);
    }

    /**
     * 清空本对象.
     */
    public void clear() {
        map.clear();
        lst.clear();
    }
    
    /**
     * 获取指定名称的取值. 不存在返回null.
     */
    public Object getValue(String key) {
        return map.get(key);
    }
    
    /**
     * 包含的名值对的个数. 
     */
    public int size() {
        return map.size();
    }
    
    /**
     * 获取指定序号名值对的名称. 
     */
    public String getKey(int index) {
        if (index < 0 || index >= lst.size()) {
            return null;
        }
        
        return (String) lst.get(index);
    }
    
    /**
     * 获取指定序号名值对的取值.
     */
    public Object getValue(int index) {
        String key = getKey(index);
        return getValue(key);
    }
    
    public boolean containsKey(String key) {
        if (key == null) {
            return false;
        }
        return map.containsKey(key);
    }

}