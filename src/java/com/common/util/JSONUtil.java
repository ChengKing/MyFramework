package com.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JavaIdentifierTransformer;
import net.sf.json.util.PropertyFilter;

import org.apache.log4j.Logger;

/**
 * JSON工具类.<br>
 * 如果本工具类不能更细粒度控制JSON生成的需要，可以看看 {@link JSONBuilder}.
 * 
 * @since 2012/3/6 将集合转JSON串时，空集合从[{}]改为[]；
 */
public class JSONUtil {

	private static final Logger LOG = Logger.getLogger(JSONUtil.class);

	/**
	 * 将数组类型的对象转换成JSON数组格式的字符串.
	 * 
	 * @param objArray
	 *            待转换的数组对象
	 * @return JSON数组格式的字符串
	 * @since chengking
	 */
	public static final String toJSONArray(Object[] objArray) {
		if (objArray == null || objArray.length == 0) {
			return null;
		}
		JSONArray jsonArray = new JSONArray();
		for (Object obj : objArray) {
			try {
				jsonArray.add(obj);
			} catch (JSONException e) {
				LOG.error("Transfer ObjArray to String fail", e);
			}
		}
		return jsonArray.toString();
	}

	/**
	 * 将集合对象转换成JSON数组格式的字符串.
	 * 
	 * @param entities
	 *            待转换的对象
	 * @return JSON数组格式的字符串
	 * @since chengking
	 */
	public static String toJSONArray(List<? extends Object> entities) {
		if (entities == null || entities.size() == 0) {
			return "[]";
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setRootClass(entities.get(0).getClass());
		return JSONArray.fromObject(entities, jsonConfig).toString();
	}

	/**
	 * 将对象转换成JSON格式的字符串.
	 * 
	 * @param obj
	 *            待转换的对象
	 * @return JSON格式的字符串
	 * @creator chengking
	 */
	@SuppressWarnings("unchecked")
	public static String toJSON(Object obj) {
		if (obj instanceof Map) { // 防止把Map直接JSON化
			return toMapJSON((Map<String, Object>) obj);
		}
		Map<String, Object> map = ReflectUtil.toMap(obj);
		return toJSON(map);
	}

	/**
	 * 根据对象的指定属性列表生成JSON字符串
	 * 
	 * @param object
	 *            对象
	 * @param fields
	 *            属性列表
	 * @return JSON字符串
	 * @since chengking
	 */
	public static String toJSON(Object object, String... fields) {
		JsonConfig jsonConfig = new JsonConfig();
		if (fields != null) {
			final List<String> fieldIndexes = new ArrayList<String>();
			for (int i = 0; i < fields.length; i++) {
				fieldIndexes.add(fields[i]);
			}
			// Arrays.asList(a)
			jsonConfig.setRootClass(object.getClass());
			jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
				public boolean apply(Object source, String name, Object value) {
					return !fieldIndexes.contains(name);
				}
			});
			// jsonConfig.registerJsonValueProcessor(Date.class,
			// new DateJsonValueProcessor());
		}
		return JSONObject.fromObject(object, jsonConfig).toString();
	}

	/**
	 * 
	 * @param obj
	 * @param excludeFields
	 * @return
	 * @since chengking
	 */
	public static String toJSONExclude(Object obj, String... excludeFields) {
		JsonConfig jsonConfig = new JsonConfig();
		if (excludeFields != null) {
			final List<String> fieldIndexes = Arrays.asList(excludeFields);
			jsonConfig.setRootClass(obj.getClass());
			jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
				public boolean apply(Object source, String name, Object value) {
					return fieldIndexes.contains(name);
				}
			});
			// jsonConfig.registerJsonValueProcessor(Date.class, null);
		}
		return JSONObject.fromObject(obj, jsonConfig).toString();
	}

	/**
	 * 将给定的map转换为JSON格式的字符串.
	 * 
	 * @param map
	 *            待转换的Map对象
	 * @return JSON格式的字符串.
	 * @creator chengking
	 */
	public static String toJSON(Map<String, Object> map) {
		return toMapJSON(map);
	}

	/**
	 * @param map
	 * @return
	 */
	public static String toMapJSON(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return "{}";
		}
		JSONObject jsonObject = new JSONObject();
		Set<String> keys = map.keySet();
		for (String objKey : keys) {
			try {
				jsonObject.put(objKey, map.get(objKey));
			} catch (JSONException e) {
				LOG.error("Transfer Map to JSon String fail", e);
			}
		}
		return jsonObject.toString();
	}

	/**
	 * 将数组类型的对象转换成JSON格式
	 * 
	 * @param objArray
	 *            待转换的数组对象
	 * @return 转换后的JSON格式
	 * @creator chengking
	 * @deprecated Use {@link #toJSONArray(Object[])} instead
	 */
	@Deprecated
	public static final String toJSON(Object[] objArray) {
		return toJSONArray(objArray);
	}

	/**
	 * 将集合对象转换成JSON字符串；
	 * 
	 * @param entities
	 *            待转换的对象
	 * @return 转换后的JSON字符串
	 * @since chengking
	 * @deprecated Use {@link #toJSONArray(List<? extends Object>)} instead
	 */
	@Deprecated
	public static String toJSON(List<? extends Object> entities) {
		return toJSONArray(entities);
	}

	/**
	 * 根据JSON串转换成Java Bean对象
	 * 
	 * JavaBean不能是嵌套类
	 * 
	 * @param json
	 *            JavaBean的JSON串
	 * @param objClazz
	 *            JavaBean对象类
	 * @return 对象
	 * @since chengking
	 */
	public static Object fromJSON(String json, @SuppressWarnings("rawtypes") Class objClazz) {
		if (StringHelper.isEmpty(json)) {
			return null;
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setRootClass(objClazz);
		try {
			JSONObject jsonObject = JSONObject.fromObject(json, jsonConfig);
			if (jsonObject == null || jsonObject.isEmpty()) {
				return null;
			}
			return JSONObject.toBean(jsonObject, jsonConfig);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 判断一个字符串是否为JSON格式，能够按照指定的类型解析出JSON对象
	 * 
	 * @param json
	 *            待判断的JSON字符串
	 * @param objClass
	 *            目标的JSON对象类型
	 * @return true表示是匹配的JSON字符串，false表示不是
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isJsonFormat(String json, Class objClazz) {
		if (StringHelper.isEmpty(json)) {
			return false;
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setRootClass(objClazz);
		try {
			JSONObject jsonObject = JSONObject.fromObject(json, jsonConfig);
			if (jsonObject == null || jsonObject.isEmpty()) {
				return false;
			}
			return true;
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return false;
	}

	/**
	 * 根据JSON串转换成JavaBean对象集合
	 * 
	 * @param json
	 *            JavaBean对象集合的JSON串
	 * @param objClazz
	 *            JavaBean对象类
	 * @return 对象集合
	 * @since chengking
	 */
	public static List<Object> fromJSONArray(String json, Class<?> objClazz) {
		List<Object> objects = new ArrayList<Object>();
		if (StringHelper.isEmpty(json)) {
			return objects;
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setRootClass(objClazz);
		JSONArray jsonArray = JSONArray.fromObject(json, jsonConfig);
		if (jsonArray.isEmpty()) {
			return objects;
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			objects.add(JSONObject.toBean(jsonObject, objClazz));
		}
		return objects;
	}

	/**
	 * 获取JSON串中某个字段的值.
	 * 
	 * @param jsonStr
	 *            完整JSON串
	 * @param fieldName
	 *            字段名
	 * @return 该字段的值
	 * @since chengking
	 */
	public static String getValue(String jsonStr, String fieldName) {
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		return jsonObj.getString(fieldName);
	}

	/**
	 * 断言所给的字符串符合JSON语法.
	 * 
	 * @param jsonStr
	 *            字符串
	 * @since chengking
	 */
	public static void assertValidJSON(String jsonStr) {
		try {
			JSONObject.fromObject(jsonStr);
		} catch (Exception e) {
			LOG.error("assertValidJSON error", e);
		}
	}

	/**
	 * 将JSON转换成Java Bean对象 <br>
	 * 
	 * 支持嵌套类，如javabean中某一个参数是一个具体的javabean ，该方法忽略javaBean中key大小写<br>
	 * 
	 * 如：{"User":"user"}可以直接通过该方法转成指定的javabean
	 * 
	 * @param json
	 *            json格式的javaBean串
	 * @param objClazz
	 *            将被转换的javaBean对象类
	 * @param interParamClassMap
	 *            javabean中嵌套类的对象类，key为这个参数在javabean中的元素名称，value为这个参数的对象类<br>
	 * 
	 *            示例：A a = new A(); a.setObjectB(new B());这样的对象A的json串，调用fromJSON时<br>
	 *            需要传入Map<String, Class> map = new HashMap<String, Class>();map.put("objectB", B.class);<br>
	 *            调用为：fromJSON(a.toJson, A.class, map)
	 * 
	 * @return
	 * @creator chengking
	 */
	public static Object fromJSONNotSensitive(String ss, @SuppressWarnings("rawtypes") Class clazz, Map<String, Class<?>> interParamClassMap) {
		JSONObject jsonObject = JSONObject.fromObject(ss);
		JsonConfig config = new JsonConfig();
		config.setJavaIdentifierTransformer(new JavaIdentifierTransformer() {
			@Override
			// 忽略json的key的首字母大小写
			public String transformToJavaIdentifier(String str) {
				char[] chars = str.toCharArray();
				chars[0] = Character.toLowerCase(chars[0]);
				return new String(chars);
			}
		});
		config.setRootClass(clazz);
		config.setClassMap(interParamClassMap);
		return JSONObject.toBean(jsonObject, config);
	}
}
