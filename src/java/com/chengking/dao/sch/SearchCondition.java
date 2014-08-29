/*
 * Title: 	  MyFrameProject
 * Copyright: Copyright (c) 2004-2014, company. All rights reserved.
 * License:   see the license file.
 * Company:   monkey.com
 * 
 * Created: MONKEY@2014年5月17日 下午6:51:01
 */

package com.chengking.dao.sch;

import java.util.Vector;

/**
 * 检索条件 <BR>
 * 
 * @author ChengKing
 * @since MONKEY@2014年5月17日
 */
public class SearchCondition {

	private int startPos = 1;

	private int maxSize = -1;

	private Vector<Condition> conditions = new Vector<SearchCondition.Condition>();

	public SearchCondition() {
		this.maxSize = 500;
	}

	public SearchCondition(int startPos, int maxSize) {
		this.startPos = startPos;
		this.maxSize = maxSize;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * 条件检索条件，操作符为“=”
	 * 
	 * @param key
	 *            属性名
	 * @param value
	 *            属性值
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月17日
	 */
	public void addEqCondition(String key, Object value) {
		conditions.add(new Condition("=", key, value));
	}

	/**
	 * 添加检索条件
	 * 
	 * @param op
	 *            连接符
	 * @param key
	 *            属性名
	 * @param value
	 *            属性值
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月17日
	 */
	public void addCondition(String op, String key, Object value) {
		if (isValidOp(op)) {
			conditions.add(new Condition(op, key, value));
		}
	}

	/**
	 * 检索条件的总数
	 * 
	 * @return
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月17日
	 */
	public int getCount() {
		return conditions.size();
	}

	/**
	 * 获取检索条件的key值
	 * @param index 第几个检索条件
	 * @return
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月19日
	 */
	public String getKey(int index){
		Condition cond = conditions.get(index);
		return (cond == null)? "" :cond.key;
	}

	/**
	 * 获取检索条件值
	 * @param index
	 * @return
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月19日
	 */
	public Object getValue(int index){
		Condition cond = conditions.get(index);
		return (cond == null)? "" : cond.value;
	}
	
	/**
	 * 连接符是否合法
	 * 
	 * @param op
	 *            连接符
	 * @return
	 * @since v1.0
	 * @creator MONKEY @ 2014年5月17日
	 */
	private boolean isValidOp(String op) {
		if ("=".equals(op) || "!=".equals(op) || ">".equals(op)
				|| ">=".equals(op) || "<".equals(op) || "<=".equals(op)
				|| "like".equals(op) || "between".equals(op) 
				|| "in".equals(op) || "not in".equals(op)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 单个检索条件 <BR>
	 * 
	 * @author ChengKing
	 * @since MONKEY@2014年5月17日
	 */
	private class Condition {
		/**
		 * 条件符，如：=，>=,<=,!=,like,in,between
		 */
		String op;

		/**
		 * 属性名
		 */
		String key;

		/**
		 * 属性值
		 */
		Object value;

		Condition(String op, String key, Object value) {
			this.op = op;
			this.key = key;
			this.value = value;
		}

		public String toString() {
			return "Condition : op [" + op + "] , key [" + key + "] , value ["
					+ value + "] !";
		}

	}
}
