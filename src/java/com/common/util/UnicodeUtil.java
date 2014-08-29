package com.common.util;
/*
package com.common.util;

/**
 * 
 * 
 * @author ChengKing
 */
public class UnicodeUtil {

	/**
	 * 对于形如OU=上海123,DC=PCCCTESTOA,DC=中文aaa,DC=COM,DC=123测试,DC=cn的LdapDN，
	 * 在使用\admin\js\trs\ids\Base64Util.js进行Base64加密以后，反解出来的Base64编码混合了Url编码和中文的Unicode编码，以及英文原文和数字。
	 * 
	 * 本方法专门针对使用Base64Util.js中的encode64方法加密后的LdapDN字符串，可以支持LdapDN包含有中文、数字和字母。
	 * 
	 * @param base64ResultOfJS
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String decodeBase64LdapDN(String base64ResultOfJS) {
		// 1、先把Base64解码
		String base64Result = Base64Util.decode(base64ResultOfJS);

		// 2、将解码结果的中文，从Unicode转成实际的中文
		// 2.1 先按逗号拆成数组
		String[] arraysOfEqual = StringHelper.splitWithMutiChar(base64Result, "%2C");
		// 遍历数组，如果有Unicode中文的，解成实际的中文，然后做URLCodeing再放回去
		if (arraysOfEqual == null || arraysOfEqual.length == 0) {
			System.out.println("No ");
			return null;
		}

		StringBuffer strBuff = new StringBuffer();
		for (int i = 0; i < arraysOfEqual.length; i++) {
			String str = arraysOfEqual[i];
			String[] keyValue = StringHelper.splitWithMutiChar(str, "%3D");
			String key = keyValue[0];
			String value = keyValue[1];
			value = decodeMixCharacterWithUnicodeRecursive(value, null);
			strBuff.append(key);
			strBuff.append("=");
			strBuff.append(value);
			if (i != (arraysOfEqual.length - 1)) {
				strBuff.append(",");
			}
		}

		return strBuff.toString();
	}

	/**
	 * 
	 * 
	 * @param original
	 * @param result
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	private static String decodeMixCharacterWithUnicodeRecursive(String original, StringBuffer result) {
		if (StringHelper.isEmpty(original)) {
			return null;
		}

		String toSplit = original;
		if (result == null)
			result = new StringBuffer();
		int index = toSplit.indexOf("%u");
		if (index >= 0) {
			result.append(toSplit.substring(0, index));
			String unicode = "\\u" + toSplit.substring(index + 2, index + 6);
			String chn = decodeUnicode(unicode);
			result.append(chn);
			toSplit = toSplit.substring(index + 6, toSplit.length());
			decodeMixCharacterWithUnicodeRecursive(toSplit, result);
		} else {
			result.append(original);
		}

		return result.toString();

	}

	public static String gbEncoding(final String gbString) {
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		System.out.println("unicodeBytes is: " + unicodeBytes);
		return unicodeBytes;
	}

	public static String decodeUnicode(final String dataStr) {
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			end = dataStr.indexOf("\\u", start + 2);
			String charStr = "";
			if (end == -1) {
				charStr = dataStr.substring(start + 2, dataStr.length());
			} else {
				charStr = dataStr.substring(start + 2, end);
			}
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制 parse整形字符串。
			buffer.append(new Character(letter).toString());
			start = end;
		}
		return buffer.toString();
	}

}