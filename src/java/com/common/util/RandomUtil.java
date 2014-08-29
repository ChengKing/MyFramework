package com.common.util;

import java.util.Random;

public class RandomUtil {

	public static String getRondomNumberAsString() {
		Random random = new Random();
		return new Integer(Math.abs(random.nextInt())).toString();
	}

	/**
	 * 获得以 时间戳_随机数字 构成的时间数字，保证唯一性
	 * 
	 * @return
	 * @creator ChengKing
	 */
	 public static String getRandonCodeWihtTimeFlag() {
		String currentTime = new Long(System.currentTimeMillis()).toString();
		String randomCode = getRondomNumberAsString();
		return currentTime + "_" + randomCode;
	}

}