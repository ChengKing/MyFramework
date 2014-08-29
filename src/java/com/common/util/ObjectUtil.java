package com.common.util;

/**
 * Java中的Object对象的工具类 <BR>
 * 
 * @since ChengKing
 */
public class ObjectUtil {

	/**
	 * 比较两个对象是否相等br>
	 * 本方法对字符串进行了特殊处理，null和空字符串判断为相等�?
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean isDifferent(Object obj1, Object obj2) {

		if (obj1 == null && obj2 == null) {
			return false;
		}

		if (obj1 == null) {
			if (obj2 instanceof String && StringHelper.isEmpty((String) obj2)) {
				return false;
			}
		}

		if (obj2 == null) {
			if (obj1 instanceof String && StringHelper.isEmpty((String) obj1)) {
				return false;
			}
		}

		boolean equals = ((obj1 == null) ? (obj2 == null) : obj1.equals(obj2));
		return !equals;
	}

	/**
	 * 比较两个对象是否相等，能避免空指针；语义{@link Object#equals(Object)} 完全相同
	 * 
	 * @creator ChengKing
	 */
	public static boolean equals(Object obj1, Object obj2) {
		if (obj1 == null) {
			return obj2 == null;
		}
		return obj1.equals(obj2);
	}

}
