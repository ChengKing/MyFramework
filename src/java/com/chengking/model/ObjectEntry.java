/*
 * Title: 	  MyFrameProject
 * Copyright: Copyright (c) 2004-2014, company. All rights reserved.
 * License:   see the license file.
 * Company:   monkey.com
 * 
 * Created: MONKEY@2014年5月16日 下午10:46:16
 */

package com.chengking.model;
 /**
 * 所有领域对象的基类 <BR>
 * 
 * @author ChengKing
 * @since MONKEY@2014年5月16日
 */
public class ObjectEntry {
	
	/**
	 * 全局唯一标识
	 */
	public String uuid ;
	
	/**
	 * 创建时间
	 */
	public long createTime;
	
	/**
	 * 最后修改时间
	 */
	public long lastEditTime;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(long lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
}
