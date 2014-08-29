package com.chengking.dao.configImpl;

import java.io.File;
import java.util.Map;

public class XMLConfig extends AbstractPropertyConfig{

	/* (non-Javadoc)
	 * @see com.chengking.dao.IPropertyConifg#start(java.io.File)
	 */
	@Override
	public void start(String file) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IPropertyConifg#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IPropertyConifg#getConfig(java.lang.String, java.lang.String)
	 */
	@Override
	public String getConfig(String key, String defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IPropertyConifg#getConfigAsInt(java.lang.String, int)
	 */
	@Override
	public int getConfigAsInt(String key, int defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IPropertyConifg#getConifgAsBoolean(java.lang.String)
	 */
	@Override
	public boolean getConifgAsBoolean(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IPropertyConifg#allConfigs()
	 */
	@Override
	public Map<String, Object> allConfigs() {
		// TODO Auto-generated method stub
		return null;
	}

}
