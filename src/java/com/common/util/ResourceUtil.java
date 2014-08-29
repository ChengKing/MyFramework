package com.common.util;

import java.net.URL;

/**
 * 获取资源文件的工具类. <BR>
 * 
 * @author ChengKing
 */
public class ResourceUtil {

	public static String getFullPath(String resourceName) {
		return getFullPath(ResourceUtil.class, resourceName);
	}

    public static String getFullPath(Class<?> clazz, String resourceName) {
		URL url = clazz.getResource(resourceName);
		if (url == null) {
			return null;
		}
		return UrlUtil.decode(url.getFile());
	}
}
