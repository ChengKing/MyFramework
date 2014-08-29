package com.common.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * 可以用于辅助生成密钥 <BR>
 * 
 * @since ChengKing
 */
public class KeyGeneratorUtil {

	/**
	 * 生成Base64格式的DES密钥
	 * @return
	 */
	public static String generateKey(){
		return generateKey("DES");
	}
	/**
	 * 生成Base64格式的指定算法的对称密钥
	 * @return
	 */
	public static String generateKey(String algorithm){
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance( algorithm );
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		//
		kg.init( new SecureRandom() );
		SecretKey key = kg.generateKey();
		return Base64Util.encode(key.getEncoded());
	}
}