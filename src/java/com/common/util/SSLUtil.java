package com.common.util;

/**
 * 
 * SSL工具类. <BR>
 * 
 * @since ChengKing
 */
public class SSLUtil {

	/**
	 * 设置SSL证书
	 * 
	 * @param keyStorePassword
	 *            javax.net.ssl.keyStorePassword参数值
	 * @param trustStorePassword
	 *            javax.net.ssl.trustStorePassword参数值
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static void setSSLCertificates(String keyStorePassword, String trustStorePassword) {
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
	}

}