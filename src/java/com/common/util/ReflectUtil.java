package com.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 反射操作工具类. <BR>
 * 
 * @author ChengKing
 */
public class ReflectUtil {

	private static final Logger LOG = Logger.getLogger(ReflectUtil.class);

	/**
	 * 获取给定对象给定field的取值. 如果field不存在返回null. 如果该field不为public, 也返回null.
	 */
	@SuppressWarnings("rawtypes")
	public static Object getFieldValue(Object obj, String fieldName) {
		Class clazz = obj.getClass();
		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			// SecurityException, NoSuchFieldException
			e.printStackTrace();
			return null;
		}

		try {
			return field.get(obj);
		} catch (Exception e) {
			// IllegalArgumentException, IllegalAccessException
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static Field getField(Class clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (Exception e) {
			// SecurityException, NoSuchFieldException
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 将给定对象转换为Map对象.
	 * 
	 * @param obj
	 *            给定对象
	 * @return 该对象的所有field构成的Map对象，键为field名称，值为field对象本身.
	 * @creator liushen @ Jun 8, 2009
	 */
	public static Map<String, Object> toMap(Object obj) {
		if (obj == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (false == field.isAccessible()) {
				field.setAccessible(true);
			}
			String fieldName = field.getName();
			try {
				map.put(fieldName, field.get(obj));
			} catch (Exception e) {
				LOG.error("skip an error: get value of field [" + fieldName + "] fail ", e);
			}
		}
		return map;
	}

}