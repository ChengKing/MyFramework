package com.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 字符串校验的类. <BR>
 * 
 * @author ChengKing
 * @version 1.0
 */

/**
 * 
 * 
 * @author ChengKing
 */
public class StringValidator {

	/**
	 * 最基本的验证, 包括: 1. 非null 2. 非空串(即全空格) 3. 长度不超过255.
	 * 
	 * @param str
	 *            待验证的字符串
	 */
	public static boolean basicValidate(String str) {
		return basicValidate(str, 255);
	}

	/**
	 * 最基本的验证, 包括: 1. 非null 2. 非空串(即全空格) 3. 长度不超过指定长度.
	 * 
	 * @param str
	 *            待验证的字符串
	 * @param maxLength
	 *            可以通过验证的最大长度
	 * @return 验证通过返回true, 否则返回false
	 */
	public static boolean basicValidate(String str, int maxLength) {
		if (str == null) {
			return false;
		}
		if ("".equals(str.trim())) {
			return false;
		}
		if (str.length() > maxLength) {
			return false;
		}
		return true;
	}

	/**
	 * 最基本的验证, 包括: 1. 非null 2. 非空串(即全空格) 3. 长度位于两个指定长度之间. TODO 区分中文和英文的长度
	 * 
	 * @param str
	 *            待验证的字符串
	 * @param minLength
	 *            可以通过验证的最小长度
	 * @param maxLength
	 *            可以通过验证的最大长度
	 * @return 验证通过返回true, 否则返回false
	 */
	public static boolean basicValidate(String str, int minLength, int maxLength) {
		if (str == null) {
			return false;
		}
		if ("".equals(str.trim()) && (minLength != 0)) {
			return false;
		}
		if (str.length() < minLength) {
			return false;
		}
		if (str.length() > maxLength) {
			return false;
		}
		return true;
	}

	/**
	 * 根据正则表达式，校验字符串是否合法
	 * 
	 * @param str
	 *            待校验的字符串
	 * @param regex
	 *            正则表达式
	 * @return 字符串符合正则表达式，则返回true
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean isMatch(String str, String regex) {
		Pattern pattern = null;

		// 若正则表达式为空，总不校验
		if (StringHelper.isEmpty(regex)) {
			return true;
		}
		// 若输入的字符串为空，校验总是失败
		if (StringHelper.isEmpty(str)) {
			return false;
		}
		// 若正则表达式不合法，总是校验失败
		try {
			pattern = Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			return false;
		}

		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 验证一个字符串是否可作为手机号.<BR>
	 * 要是数字组成
	 * 
	 * @param str
	 *            待验证的字符串
	 * @return 验证通过返回true, 否则返回false
	 */
	public static boolean isValidCNMobile(String str) {
		if (str == null || str.trim().length() <= 0) {
			return false;
		}
		if (false == isNumeric(str))
			return false;

		// return isNumeric(str);
		// Pattern pattern = Pattern.compile("^(\\+86)?1[35]\\d{9}$");
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		// Pattern pattern = Pattern.compile("^([0-9+\\-(\\))(\\()]{1,255})$");
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 验证一个字符串是否可作为电话号.<BR>
	 * 只要是由数字 - + ( ) 组成即可。
	 * 
	 * @param str
	 */
	public static boolean isValidPhone(String str) {
		if (str == null || str.trim().length() <= 0) {
			return false;
		}
		// Pattern pattern = Pattern
		// .compile("^((\\()?0\\d{2,3}[\\)-]?)?(\\d{7,8})(-(\\d{1,5}))?$");
		Pattern pattern = Pattern.compile("^([0-9+\\-(\\))(\\()]{1,255})$");
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 验证一个字符串是否可作为EMail.
	 * <p>
	 * 可以通过验证的字符串必须包含"@"和"."字符, 并且这两个字符不能在头尾.
	 * 
	 * @param str
	 *            待验证的字符串
	 * @return 验证通过返回true, 否则返回false
	 */
	public static boolean isValidEmail(String str) {
		if (StringHelper.isEmpty(str))
			return false;
		Pattern pattern = Pattern.compile("^[\\w-_]+(\\.[\\w-_]+)*@([\\w-_]+\\.)+[\\w]{2,}$");
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 验证一个字符串是否是合法的多个EMail地址，多个Email使用分号间隔.
	 * <p>
	 * 可以通过验证的字符串必须包含"@"和"."字符, 并且这两个字符不能在头尾.
	 * 
	 * @param str
	 *            待验证的字符串
	 * @return 验证通过返回true, 否则返回false
	 */
	public static boolean isValidMultiEmails(String str) {
		if (StringHelper.isEmpty(str))
			return false;
		Pattern pattern = Pattern
				.compile("^([\\w-_]+(\\.[\\w-_]+)*@[\\w-]+(\\.[\\w-_]+)+;)+([\\w-_]+(\\.[\\w-_]+)*@[\\w-]+(\\.[\\w-_]+)+)$");
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 最基本的验证, 不过允许字符串为empty或者全部是空格, 验证内容包括: 1. 非null 2. 长度不超过指定长度.
	 * 
	 * @param str
	 *            待验证的字符串
	 * @param maxLength
	 *            可以通过验证的最大长度
	 * @return 验证通过返回true, 否则返回false
	 */
	public static boolean basicValidateAllowEmpty(String str, int maxLength) {
		if (str == null) {
			return false;
		}
		if (str.length() > maxLength) {
			return false;
		}
		return true;
	}

	/**
	 * 最基本的验证，如果字符串不为空，则判断其长度是否在允许的范围内
	 * 
	 * @param str
	 *            待验证的字符串
	 * @param maxLength
	 *            运行的最大长度
	 * @return 如果在允许的范围内，返回true，否则返回false
	 */
	public static boolean isInAllowLength(String str, int maxLength) {
		// 如果字符串为空，也返回true
		if (str == null)
			return true;

		if (str.length() < maxLength)
			return true;

		return false;
	}

	public static boolean startsWithLetter(String str) {
		if (isEmpty(str)) {
			return false;
		}
		return Character.isLetter(str.charAt(0));
	}

	/**
	 * @deprecated use {@link StringHelper#isEmpty(String)} instead!
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}

	/**
	 * 验证字符串是否包含中文字符
	 * 
	 * @param str
	 *            字符串
	 * @return 如果包含有中文字符，则返回true，不包含中文或为空、null，则返回false。
	 */
	public static boolean isIncludeChinese(String str) {
		if (StringHelper.isEmpty(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^\\s*\\S*[\\u4e00-\\u9fa5]+\\s*\\S*$");
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 是否含有中文以外的其它全角字符
	 * 
	 * @param str
	 *            字符串
	 * @return 如果包含有中文以外的其它全角字符，则返回true，否则返回false。
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean hasFullAngleSpecialCharacter(String str) {
		char[] charArray = str.toCharArray();
		for (int index = 0; index < charArray.length; index++) {
			if (!isHalf(charArray[index]) && !isChinese(charArray[index])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断字符是否是半角字符
	 * 
	 * @param c
	 *            待测试字符
	 * @return 判断结果
	 * @since v4.0
	 * @creator chenzigan @ 2013-9-23
	 */
	private static boolean isHalf(char c) {
		return (c >= '\u0020' && c <= '\u007e') || (c >= '\uff61' && c <= '\uffdc') || (c >= '\uffe8' && c <= '\uffee');
	}

	/**
	 * 判断字符是否是中文字符
	 * 
	 * @param c
	 *            待测试字符
	 * @return 判断结果
	 * @since v1.0
	 * @creator ChengKing
	 */
	private static boolean isChinese(char c) {
		return (c >= '\u4e00' && c <= '\u9fcc') || (c >= '\u3400' && c <= '\u4db5') || (c >= '\uf900' && c <= '\ufad9');
	}

	/**
	 * 验证URL格式是否合法
	 * 
	 * @deprecated by {@link org.apache.commons.validator.routines.UrlValidator.getInstance().isValid(}<br>
	 *             该方法对于“http://English192.168.0.3/wcm/index.txt”这种IP不合法的情况无法校验，被废弃；<br>
	 * 
	 * @param url
	 *            url字符串
	 * @return 如果URL格式合法则返回true，否则返回false;
	 */
	public static boolean validateUrl(String url) {
		if (url == null)
			return false;
		// 如果url中间存在空格，则url是不合法的
		if (url.indexOf(" ") != -1) {
			return false;
		}
		String regEx = "^(http(s?)://)((((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]))|localhost|(([a-zA-Z-]{1,62}(\\d*))(\\.[a-zA-Z0-9-]{1,62})+))(:\\d{1,5})?(/\\w+)*(/?|(/\\w+\\.?\\w+))?(\\/[^#$]+)*)";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(url);
		return m.find();
	}

	/**
	 * 验证身份证号是否合法 允许为15位和18位
	 * 
	 * @param creditID
	 *            身份证号
	 * @return 如果身份证号合法则返回true，否则返回false;
	 */
	public static boolean validateCreditID(String creditID) {
		if (creditID == null)
			return false;
		String regEx18 = "^[1-9]\\d{5}((19\\d{2})|(200\\d))((0\\d)|(1[0-2]))(([0-2]\\d)|(3[0-1]))\\d{3}(\\d|X|x)$";
		Pattern p18 = Pattern.compile(regEx18);
		Matcher m18 = p18.matcher(creditID.trim());

		String regEx15 = "^[1-9]\\d{5}\\d{2}((0\\d)|(1[0-2]))(([0-2]\\d)|(3[0-1]))\\d{3}$";
		Pattern p15 = Pattern.compile(regEx15);
		Matcher m15 = p15.matcher(creditID.trim());
		return m18.find() || m15.find();
	}

	/**
	 * 验证邮政编码是否符合规范
	 * 
	 * @param post
	 * @return 如果邮政编码合法则返回true，否则返回false;
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean isValidPost(String post) {
		if (false == isNumeric(post))
			return false;
		return post.length() == 6;
	}

	/***
	 * 判断是否为非负整数
	 * 
	 * @param input
	 * @return 如果是数字则返回true，否则返回false;
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean isNumeric(String input) {
		if (StringHelper.isEmpty(input))
			return false;
		Pattern pattern = Pattern.compile("^\\d+$");
		return pattern.matcher(input).matches();
	}

	/**
	 * 验证传真是否符合规范
	 * 
	 * @param fax
	 * @return 如果传真合法则返回true，否则返回false;
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean isFax(String fax) {
		if (StringHelper.isEmpty(fax))
			return false;
		Pattern pattern = Pattern.compile("^(((\\+|0)[0-9]{2,3})-)?((0[0-9]{2,3})-)?([0-9]{8})(-([0-9]{3,4}))?$");
		return pattern.matcher(fax).matches();
	}

	/**
	 * 不允许为空，只能使用英文字母，数字和下划线
	 * 
	 * @param dbName
	 *            数据库名
	 * @return 合法返回true，否则返回false
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean isValidDBName(String dbName) {
		if (StringHelper.isEmpty(dbName))
			return false;
		Pattern pattern = Pattern.compile("^[0-9a-zA-Z_]+$");
		Matcher matcher = pattern.matcher(dbName);
		return matcher.find();
	}
}