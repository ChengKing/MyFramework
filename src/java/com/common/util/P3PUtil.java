package com.common.util;

import javax.servlet.http.HttpServletResponse;

/**
 * P3P工具类 <BR>
 * 
 * @author ChengKing
 * @since IDS V3.0 Build3028
 */
public class P3PUtil {

	/**
	 * 写入P3P 让IE 6+接受第三方Cookie.
	 * @since ChengKing
	 */
	public static void accept3rdPartyCookie(HttpServletResponse response) {
		response.addHeader("P3P", "CP=\"CAO PSA OUR\"");
	}

}