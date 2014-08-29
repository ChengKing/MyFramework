package com.common.util;

import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * 创建类实例的工具类, 辅助完成各接口的实现类的注入过程. <BR>
 * 
 * @author TRS信息技术有限公司
 * @since JDK1.4
 */
public class ClassUtil {

	private static final Logger LOG = Logger.getLogger(ClassUtil.class);

	/**
	 * 根据指定类名得到其实例 调用时需要保证className是有效的类名, 否则会返回null.
	 * 
	 * @param className
	 *            指定类名
	 * @return 成功得到实例返回该实例 否则返回null.
	 */
	public static Object getInstanceByClassName(String className) throws Exception {
		if (className == null) {
			throw new IllegalArgumentException("className is null!");
		}
		className = className.trim();
		if (className.length() == 0) {
			throw new IllegalArgumentException("className is empty!");
		}
		try {
			return Class.forName(className).newInstance();
		} catch (Exception e) {
			LOG.error("fail to initialize class, className:" + className, e);
			throw new Exception("fail to initialize class, className:" + className);
		}
	}

	/**
	 * 根据指定类名得到其类对象(class对象). 调用时需要保证className是有效的类名, 否则会返回null.
	 * 
	 * @param className
	 *            指定类名
	 * @return 成功得到实例返回该实例 否则返回null.
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClassByName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			LOG.error("fail to find class: " + className, e);
		}
		return null;
	}

	/**
	 * 根据指定类对对象（class对象)得到其实例
	 * 
	 * @param clazz
	 *            指定类对象
	 * @return 成功得到实例返回该实例 否则返回null.
	 */
	@SuppressWarnings("rawtypes")
	public static Object getInstanceByClass(Class clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			LOG.error("fail to create instance! class=" + clazz, e);
		}
		return null;
	}

	/**
	 * 获取指定类的源位置.
	 * 
	 * @return 该clazz的源位置.
	 * @since JDK1.4
	 */
	@SuppressWarnings("rawtypes")
	public static String getSourceLocation(Class clazz) {
		if (clazz != null) {
			try {
				ProtectionDomain pd = clazz.getProtectionDomain();
				CodeSource cs = pd.getCodeSource();
				if (cs != null) {
					return cs.getLocation().toString();
				} else {
					return "unknown";
				}
			} catch (RuntimeException e) {
				return e.toString();
			}
		}
		return "unknown";
	}

	/**
	 * 判断一个类是否为Primitive基本类型的Wrapper类，如Interger、Long等
	 * 
	 * @param clz
	 * @return
	 * @creator chengking
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isPrimitiveWrapClass(Class clz) {
		try {
			return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断一个类路径是否合法：不校验这个类是否存在系统中，单从输入字符串上校验
	 * 
	 * @param classPath
	 *            只允许输入A-Z,a-z,0-9和.，其中必须以A-Z或者a-z开头不能以.结尾
	 * @return 合法则返回true，否则返回false
	 * @creator chengking
	 */
	public static boolean isLegal(String classPath) {
		if (StringHelper.isEmpty(classPath)) {
			return false;
		}
		String legal = "^[a-zA-Z]+[a-zA-Z0-9.]+[0-9A-Za-z]+$";
		if (classPath.length() == 2) {
			legal = "^[a-zA-Z]+[0-9A-Za-z]+$";
		}
		Pattern pattern = Pattern.compile(legal);
		Matcher matcher = pattern.matcher(classPath);
		return matcher.matches();
	}

}