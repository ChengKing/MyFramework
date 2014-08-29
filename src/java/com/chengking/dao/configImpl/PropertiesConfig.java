package com.chengking.dao.configImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import org.apache.log4j.Logger;

import com.common.util.FileUtil;
import com.common.util.ResourceUtil;
import com.common.util.StringHelper;

public class PropertiesConfig extends AbstractPropertyConfig {
	/**
	 * 日志
	 */
	private static final Logger logger = Logger.getLogger(PropertiesConfig.class);

	@Override
	public void start(String fileName) {
		String url = ResourceUtil.getFullPath("/");
		String filePath = url + fileName;
		InputStream ins = FileUtil.getInputStream(filePath);
		try {
			configProperties.load(ins);
		} catch (IOException e) {
			logger.error("load global config failed !", e);
			throw new RuntimeException("load global config failed !", e);
		}
		Enumeration<Object> enums = configProperties.keys();
		while (enums.hasMoreElements()) {
			Object keyObj = enums.nextElement();
			Object valueObj = configProperties.get(keyObj);
			if(keyObj == null){
				continue;
			}
			configMap.put(keyObj.toString(), valueObj);
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public boolean getConifgAsBoolean(String key) {
		return StringHelper.parseBoolean(configProperties.getProperty(key));
	}

	@Override
	public Map<String, Object> allConfigs() {
		return configMap;
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IPropertyConifg#getConfig(java.lang.String, java.lang.String)
	 */
	@Override
	public String getConfig(String key, String defaultValue) {
		Object val = configProperties.get(key);
		String value = (val==null) ? "" : val.toString();
		return StringHelper.isEmpty(value) ? defaultValue : value;
	}

	/* (non-Javadoc)
	 * @see com.chengking.dao.IPropertyConifg#getConfigAsInt(java.lang.String, int)
	 */
	@Override
	public int getConfigAsInt(String key, int defaultValue) {
		return StringHelper.parseInt(configProperties.getProperty(key), defaultValue);
	}
}
