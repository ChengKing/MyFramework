package com.common.util;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * 序列化读取对象的工具类. <BR>
 * 
 * @author ChengKing
 */
public class SerialUtil {
    
    private static final Logger LOG = Logger.getLogger(SerialUtil.class);

    /**
     * 从给定的资源文件中序列读取(即反序列化)一个Java Object对象.<BR>
     * 该方法相当于<code>loadObject(SerialUtil.class, resource)</code>
     * @param resource 给定的资源文件名.
     * @return 一个Java Object对象. 如果读取文件失败或反序列化失败, 返回null.
     * @see #loadObject(Class, String)
     */
    public static Object loadObject(String resource) {
        return loadObject(SerialUtil.class, resource);
    }

    /**
     * 从给定的资源文件中序列读取(即反序列化)一个Java Object对象.
     * @param clz 给定的类
     * @param resource 给定的资源文件名.
     * @return 一个Java Object对象. 如果读取文件失败或反序列化失败, 返回null.
     */
    @SuppressWarnings("rawtypes")
	public static Object loadObject(Class clz, String resource) {
    	if (clz == null || resource == null) {
    		LOG.error("either resource [ " + resource + " ] or class [ " + clz + " ] is null!");
    		return null;
    	}
    	URL resourceUrl = clz.getResource(resource);
    	if (resourceUrl == null) {
    		LOG.error("resource [ " + resource + " ] not found in [ " + clz.getResource("/") + " ]!");
    		return null;
    	}
        try {
			InputStream in = resourceUrl.openStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            LOG.error("fail on read [ " + resourceUrl + " ]", e);
            return null;
        }
    }

}