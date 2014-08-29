/*
 * Title: 	  MyFrameProject
 * Copyright: Copyright (c) 2004-2014, company. All rights reserved.
 * License:   see the license file.
 * Company:   monkey.com
 * 
 * Created: MONKEY@2014年5月16日 下午10:35:10
 */

package com.chengking.dao.accessor;

import java.util.List;

import javax.security.auth.callback.ConfirmationCallback;

import org.hibernate.Hibernate;
import org.hibernate.cfg.Configuration;

import com.chengking.dao.IDBAccessor;
import com.chengking.dao.IPropertyConifg;
import com.chengking.dao.sch.SearchCondition;
import com.chengking.model.ObjectEntry;

 /**
 * hibernate3的数据存储实现 <BR>
 * 
 * @author ChengKing
 * @since MONKEY@2014年5月16日
 */
public class Hb3DataAccessor implements IDBAccessor {

	/* (non-Javadoc)
	 * @see com.chengking.dao.IDBAccessor#start(com.chengking.dao.IPropertyConifg)
	 */
	@Override
	public void start(IPropertyConifg config) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IDBAccessor#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IDBAccessor#add(java.lang.Class, com.chengking.model.ObjectEntry)
	 */
	@Override
	public void add(Class<?> clazz, ObjectEntry obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IDBAccessor#update(java.lang.Class, com.chengking.model.ObjectEntry)
	 */
	@Override
	public void update(Class<?> clazz, ObjectEntry obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IDBAccessor#delete(java.lang.Class, com.chengking.model.ObjectEntry)
	 */
	@Override
	public void delete(Class<?> clazz, ObjectEntry obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IDBAccessor#findObj(java.lang.Class, java.lang.Object, java.lang.Object)
	 */
	@Override
	public ObjectEntry findObj(Class<?> clazz, Object key, Object value)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IDBAccessor#findObjs(java.lang.Class, com.chengking.dao.sch.SearchCondition)
	 */
	@Override
	public List<ObjectEntry> findObjs(Class<?> clazz, SearchCondition sc)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
