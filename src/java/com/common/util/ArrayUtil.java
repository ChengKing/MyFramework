package com.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author chengking
 *
 */
public class ArrayUtil {

	/**
	 * 从指定的数组中取指定的子数组
	 */
	public static Object[] subArray(Object[] aArray, int startPos, int maxResult) {
		if (aArray == null) {
			return null;
		}
		if (startPos >= aArray.length) {
			return new Object[0];
		}
		if (startPos + maxResult > aArray.length) {
			maxResult = aArray.length - startPos;
		}
		Object[] dest = new Object[maxResult];
		System.arraycopy(aArray, startPos, dest, 0, maxResult);
		return dest;
	}

	/**
	 * 取两个列表集合的差值
	 * 
	 * @param newList 新的集合
	 * @param oldList 就得集合
	 * @return
	 * @creator chengking
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getDiffList(List newList, List oldList) {
		List diffList = new ArrayList();
		for (Iterator iter = newList.iterator(); iter.hasNext();) {
			Object item = iter.next();
			if (false == oldList.contains(item)) {
				diffList.add(item);
			}
		}
		return diffList;
	}

	/**
	 * List集合合并，返回新的List，对于重复的元素，合并为一个
	 * 
	 * @param firstList
	 * @param secondList
	 * @return
	 * @creator chengking
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List merge(List firstList, List secondList, boolean removeRepeat) {
		// 如果传进来的list均为空，则返回null
		if (firstList == null && secondList == null)
			return null;
		// 如果secondList为空，则让secondList为不为空的firstList
		if (secondList == null)
			secondList = firstList;
		for (Iterator iter = secondList.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (removeRepeat) {
				if (firstList.contains(obj)) {
					continue;
				}
			}
			firstList.add(obj);
		}
		return firstList;
	}

	/**
	 * 判断一个集合是否为空，空分为两种：null或者size()==0
	 * 
	 * @param objects
	 *            对象集合
	 * @return true表示为空,false表示不为空
	 * @since 
	 * @creator chengking
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(List objects) {
		return (objects == null || objects.size() == 0);
	}

	/**
	 * 对象构造成字符串数组输出
	 * 
	 * @param objects
	 *            待输出的对象集合
	 * @param seperator
	 *            分隔符，默认为半角逗号
	 * @return 以分隔符分割的对象数组
	 * @creator chengking 
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(List objects, String seperator) {
		if (isEmpty(objects)) {
			return "";
		}
		// 处理分隔符，默认为半角逗号
		if (StringHelper.isEmpty(seperator)) {
			seperator = ",";
		}
		//
		StringBuffer objectArrayBuffer = new StringBuffer();
		// 处理分隔符
		objectArrayBuffer.append(objects.get(0));
		for (int i = 1; i < objects.size(); i++) {
			objectArrayBuffer.append(seperator).append(objects.get(i));
		}
		return objectArrayBuffer.toString();
	}

	/**
	 * 取两个集合的交集
	 * 
	 * @param firstList
	 *            第一个集合
	 * @param secondList
	 *            第二个集合	 
	 * @return
	 * @since v4.0
	 * @creator chengking @ 2014-1-3
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List union(List firstList, List secondList) {
		List result = new ArrayList();
		// 若firstList或secondList任意一个为null，则返回空集合
		if (firstList == null || secondList == null) {
			return result;
		}
		// 若firstList或secondList任意一个为空，则返回空集合
		if (firstList.size() == 0 || secondList.size() == 0) {
			return result;
		}
		for (Iterator iter = firstList.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (result.contains(obj)) {
				continue;
			}
			if (secondList.contains(obj)) {
				result.add(obj);
			}
		}
		return result;
	}
}
