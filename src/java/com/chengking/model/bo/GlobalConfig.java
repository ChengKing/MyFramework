/*
 * Title: 	  MyFrameProject
 * Copyright: Copyright (c) 2004-2014, company. All rights reserved.
 * License:   see the license file.
 * Company:   monkey.com
 * 
 * Created: DF-ChengK@2014年7月31日 上午10:13:27
 */

package com.chengking.model.bo;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.chengking.model.ObjectEntry;

 /**
 * 系统全局配置项 <BR>
 * 
 * @author ChengKing
 * @since 2014年7月31日
 */
@Entity
@Table(name="mkglobalconfig")
public class GlobalConfig extends ObjectEntry {
	/**
	 * 自增序列
	 */
	@Column
	private int id;
	/**
	 * 配置项名称
	 */
	private String key;
	/**
	 * 配置项值
	 */
	private String value;
	/**
	 * 描述信息
	 */
	private String desc;
	/**
	 * Returns the {@link #id}.
	 * @return the id.
	 */
	
	public int getId() {
		return id;
	}
	/**
	 * Set {@link #id}.
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * Returns the {@link #key}.
	 * @return the key.
	 */
	
	public String getKey() {
		return key;
	}
	/**
	 * Set {@link #key}.
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * Returns the {@link #value}.
	 * @return the value.
	 */
	
	public String getValue() {
		return value;
	}
	/**
	 * Set {@link #value}.
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * Returns the {@link #desc}.
	 * @return the desc.
	 */
	
	public String getDesc() {
		return desc;
	}
	/**
	 * Set {@link #desc}.
	 * @param desc The desc to set.
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
