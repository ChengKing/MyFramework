package com.common.util;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具类. <BR>
 * 
 * @author chengking
 */
public class IPUtil {

	public static final String IP_SEGMENT_SPLITTER = "-";
	private static final String REG_IP_V4 = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
	private static final String REG_IP_V4_SEG = REG_IP_V4 + "\\-" + REG_IP_V4;

	/**
	 * 验证IP是否有效
	 * 
	 * @param ip
	 *            IP地址，如192.9.200.10，不支持通配符
	 * @return true if valid or empty, false otherwise.
	 */
	public static boolean isValidV4IP(String ip) {
		if (ip == null) {
			return false;
		}
		ip = ip.trim();
		if (ip.length() == 0) {
			return false;
		}
		String[] part_IP = ip.split("\\.");
		if (part_IP.length != 4) {
			return false;
		}
		int int_IP;
		for (int i = 0; i < 4; i++) {
			try {
				int_IP = Integer.parseInt(part_IP[i]);
				if (int_IP < 0 || int_IP > 255) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否是合法的IPV4或者IPV4段地址，以-为分隔符
	 * 
	 * @param ip
	 *            如192.9.200.10， 或者192.9.200.0-192.9.200.255，不支持通配符
	 * @return
	 * @creator chengking
	 */
	public static boolean isValidV4IPOrSegment(String ip) {
		if (StringHelper.isEmpty(ip)) {
			return false;
		}
		if (ip.matches(REG_IP_V4_SEG) || ip.matches(REG_IP_V4)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是合法的IP段，格式如：192.9.200.0-192.9.200.255，不支持通配符
	 * 
	 * @param ipSegment
	 * @return
	 * @creator chengking 
	 */
	public static boolean isValidIPSegment(String ipSegment) {
		if (StringHelper.isEmpty(ipSegment)) {
			return false;
		}
		return ipSegment.matches(REG_IP_V4_SEG);
	}

	/**
	 * 处理代理模式下获取原始客户端IP，优先获取Header里面X-Forwarded-For属性
	 * 
	 * @param request
	 * @return
	 * @creator chengking
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		String remoteAddr = request.getRemoteAddr();
		if (xForwardedFor != null && xForwardedFor.length() > 4) {
			int index = xForwardedFor.indexOf(",");
			if (index != -1) {
				remoteAddr = xForwardedFor.substring(0, index);
			} else {
				remoteAddr = xForwardedFor;
			}
		}
		return remoteAddr;
	}

	/**
	 * IP或者IP段是否包含IP地址
	 * 
	 * @param ipOrIpSegment
	 *            IP或者IP段 ， 如192.9.200.10， 或者192.9.200.0-192.9.200.255，不支持通配符
	 * @param ipAddress
	 *            IP地址，如192.9.200.10，不支持通配符
	 * @return
	 * @creator chengking
	 */
	public static boolean isIpOrSegmentContainsIpAddr(String ipOrIpSegment, String ipAddress) {
		if (ipOrIpSegment == null || ipOrIpSegment.length() == 0) {
			return false;
		}

		if (ipAddress == null || ipAddress.length() == 0) {
			return false;
		}
		// 如果是IP值，且相等
		if (-1 == ipOrIpSegment.indexOf(IP_SEGMENT_SPLITTER) && ipOrIpSegment.equals(ipAddress)) {
			return true;
		}
		// 如果都是IP段
		if (-1 != ipOrIpSegment.indexOf(IP_SEGMENT_SPLITTER) && -1 != ipAddress.indexOf(IP_SEGMENT_SPLITTER)) {
			return ipOrIpSegment.equals(ipAddress);
		}

		// 如果是IP段
		if (-1 != ipOrIpSegment.indexOf(IP_SEGMENT_SPLITTER)) {
			return isIpInSegment(ipOrIpSegment, ipAddress);
		}
		return false;
	}

	/**
	 * 两个IP段内是否有重复交叉，或者包含对方IP段；<br>
	 * 例如：192.9.1.10-192.9.1.20和192.9.1.15-192.9.1.30有交叉；<br>
	 * 如果IP段格式不合法，直接返回false；<br>
	 * 
	 * @param ipSegment1
	 *            IP段一，格式如：192.9.1.10-192.9.1.20，或者192.9.1.10，不支持通配符
	 * @param ipSegment2
	 *            IP段二，格式如：192.9.1.15-192.9.1.30，或者192.9.1.17，不支持通配符
	 * @return 有交叉返回true，不交叉返回false
	 * @creator chengking
	 */
	public static boolean containsOrCross(String ipSegment1, String ipSegment2) {
		if (StringHelper.isEmpty(ipSegment1) || StringHelper.isEmpty(ipSegment2)) {
			return false;
		}
		if (false == ipSegment1.matches(REG_IP_V4_SEG) && ipSegment1.matches(REG_IP_V4)) {
			ipSegment1 = ipSegment1 + "-" + ipSegment1;
		}
		if (false == ipSegment2.matches(REG_IP_V4_SEG) && ipSegment2.matches(REG_IP_V4)) {
			ipSegment2 = ipSegment2 + "-" + ipSegment2;
		}
		// 取IP段的前三位数字判断，如果前三位不相同，则直接返回false
		String[] segmentArray1 = StringHelper.split(ipSegment1, "-");
		if (segmentArray1 == null || segmentArray1.length != 2) {
			return false;
		}
		String prefixThreeLetterStr1 = segmentArray1[0].substring(0, segmentArray1[0].lastIndexOf("."));
		String tailThreeLetterStr1 = segmentArray1[1].substring(0, segmentArray1[1].lastIndexOf("."));

		String[] segmentArray2 = StringHelper.split(ipSegment2, "-");
		if (segmentArray2 == null || segmentArray2.length != 2) {
			return false;
		}
		String prefixThreeLetterStr2 = segmentArray2[0].substring(0, segmentArray2[0].lastIndexOf("."));
		String tailThreeLetterStr2 = segmentArray2[1].substring(0, segmentArray2[1].lastIndexOf("."));

		// 如果前三位数字没有相等的情况，直接返回false
		if (false == prefixThreeLetterStr1.equals(prefixThreeLetterStr2)
				&& false == prefixThreeLetterStr1.equals(tailThreeLetterStr2)
				&& false == tailThreeLetterStr1.equals(prefixThreeLetterStr2)
				&& false == tailThreeLetterStr1.equals(tailThreeLetterStr2)) {
			return false;
		}

		String lastMinLetterOfStr1 = segmentArray1[0].substring(segmentArray1[0].lastIndexOf(".") + 1);
		String lastMaxLetterOfStr1 = segmentArray1[1].substring(segmentArray1[1].lastIndexOf(".") + 1);

		String lastMinLetterOfStr2 = segmentArray2[0].substring(segmentArray2[0].lastIndexOf(".") + 1);
		String lastMaxLetterOfStr2 = segmentArray2[1].substring(segmentArray2[1].lastIndexOf(".") + 1);
		// 如果IP段1的最大值小于IP段2的最小值，或者IP段2的最大值小于IP段１的最小值，则不重复交叉
		if (Integer.parseInt(lastMaxLetterOfStr1) < Integer.parseInt(lastMinLetterOfStr2)
				|| Integer.parseInt(lastMaxLetterOfStr2) < Integer.parseInt(lastMinLetterOfStr1)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断IP是否在指定IP段范围内
	 * 
	 * @param ipSegment
	 *            IP段，如192.9.200.0-192.9.200.255，不支持通配符
	 * @param ipAddr
	 *            IP地址，如192.9.200.10，不支持通配符
	 * @return IP地址在IP段范围内，返回true，否则返回false
	 * @since v3.5
	 * @creator chengking
	 */
	public static boolean isIpInSegment(String ipSegment, String ipAddr) {
		if (ipSegment == null) {
			return false;
		}
		if (ipAddr == null) {
			return false;
		}
		ipSegment = ipSegment.trim();
		ipAddr = ipAddr.trim();
		if (!ipSegment.matches(REG_IP_V4_SEG) || !ipAddr.matches(REG_IP_V4)) {
			return false;
		}
		int splitterIndex = ipSegment.indexOf('-');
		String[] ipSegBeforeArray = ipSegment.substring(0, splitterIndex).split("\\.");
		String[] ipSegEndArray = ipSegment.substring(splitterIndex + 1).split("\\.");
		String[] ipAddrArray = ipAddr.split("\\.");
		long ipSegBeforeLongValue = 0L, ipSegEndLongValue = 0L, ipAddrLongValue = 0L;
		for (int i = 0; i < 4; ++i) {
			ipSegBeforeLongValue = ipSegBeforeLongValue << 8 | Integer.parseInt(ipSegBeforeArray[i]);
			ipSegEndLongValue = ipSegEndLongValue << 8 | Integer.parseInt(ipSegEndArray[i]);
			ipAddrLongValue = ipAddrLongValue << 8 | Integer.parseInt(ipAddrArray[i]);
		}
		if (ipSegBeforeLongValue > ipSegEndLongValue) {
			long t = ipSegBeforeLongValue;
			ipSegBeforeLongValue = ipSegEndLongValue;
			ipSegEndLongValue = t;
		}
		return ipSegBeforeLongValue <= ipAddrLongValue && ipAddrLongValue <= ipSegEndLongValue;
	}

	/**
	 * IP集合是否包含当前IP
	 * 
	 * @param ipSet
	 *            IP或者IP段集合
	 * @param ipAddress
	 *            IP地址
	 * @return 包含返回true，否则返回false
	 */
	@SuppressWarnings("rawtypes")
	public static boolean containIpAddr(Set ipSet, String ipAddress) {
		if (ipSet == null || ipSet.size() == 0) {
			return false;
		}
		Object[] ipArray = ipSet.toArray();
		for (int i = 0; i < ipArray.length; i++) {
			String ipOrSegment = (String) ipArray[i];
			if (isIpOrSegmentContainsIpAddr(ipOrSegment, ipAddress)) {
				return true;
			}
		}
		return false;
	}

	// /**
	// * 把IP地址转化为int<br>
	// * 1) 把IP地址转化为字节数组；<br>
	// * 2）通过左移位（<<）、与（&）、或（|）这些操作转为int
	// *
	// * @param ipAddr
	// * @return int
	// */
	// public static int ipToInt(String ipAddr) {
	// if (false == ipAddr.matches(REG_IP_V4)) {
	// throw new IllegalArgumentException(ipAddr + " is invalid IP");
	// }
	//
	// try {
	// return bytesToInt(ipToBytesByInet(ipAddr));
	// } catch (Exception e) {
	// throw new IllegalArgumentException(ipAddr + " is invalid IP");
	// }
	// }

	// /**
	// * 把IP地址转化为字节数组
	// *
	// * @param ipAddr
	// * @return byte[]
	// */
	// public static byte[] ipToBytesByInet(String ipAddr) {
	// try {
	// return InetAddress.getByName(ipAddr).getAddress();
	// } catch (Exception e) {
	// throw new IllegalArgumentException(ipAddr + " is invalid IP");
	// }
	// }

	// /**
	// * 根据位运算把 byte[] -> int
	// *
	// * @param bytes
	// * @return int
	// */
	// public static int bytesToInt(byte[] bytes) {
	// int addr = bytes[3] & 0xFF;
	// addr |= ((bytes[2] << 8) & 0xFF00);
	// addr |= ((bytes[1] << 16) & 0xFF0000);
	// addr |= ((bytes[0] << 24) & 0xFF000000);
	// return addr;
	// }

	// /**
	// * 把int转化为ip地址<br>
	// * 1)将整数值进行右移位操作（>>>），右移24位，再进行与操作符（&）0xFF，得到的数字即为第一段IP；<br>
	// * 2)将整数值进行右移位操作（>>>），右移16位，再进行与操作符（&）0xFF，得到的数字即为第二段IP；<br>
	// * 3)将整数值进行右移位操作（>>>），右移8位，再进行与操作符（&）0xFF，得到的数字即为第三段IP；<br>
	// * 4)将整数值进行与操作符（&）0xFF，得到的数字即为第四段IP；<br>
	// *
	// * @param ipInt
	// * @return String
	// */
	// public static String intToIp(int ipInt) {
	// return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.').append((ipInt >> 16) & 0xff).append('.')
	// .append((ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff)).toString();
	// }

	/**
	 * IP地址转为整数
	 * 
	 * @param ipAddr
	 *            IPV4地址，不支持IP段; <br>
	 *            如果IP地址不合法，返回-1;<br>
	 * @return 转换后的整数
	 * @since v3.5
	 * @creator chengking
	 */
	public static long ip2Long(String ipAddr) {
		if (false == ipAddr.matches(REG_IP_V4)) {
			return -1;
		}
		String[] items = ipAddr.split("\\.");
		return Long.valueOf(items[0]) << 24 | Long.valueOf(items[1]) << 16 | Long.valueOf(items[2]) << 8
				| Long.valueOf(items[3]);
	}

	/**
	 * 整数值转换回IP地址
	 * 
	 * @param ipLongValue
	 *            整数值
	 * @return IP地址
	 * @creator chengking
	 */
	public static String long2Ip(long ipLongValue) {
		// StringBuilder sb = new StringBuilder();
		// sb.append(ipLongValue & 0xFF).append(".");
		// sb.append((ipLongValue >> 8) & 0xFF).append(".");
		// sb.append((ipLongValue >> 16) & 0xFF).append(".");
		// sb.append((ipLongValue >> 24) & 0xFF);
		// return sb.toString();

		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((ipLongValue >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((ipLongValue & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((ipLongValue & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((ipLongValue & 0x000000FF)));
		return sb.toString();
	}

	/**
	 * 判断多个ip段是否是合法的ip段。只做：ip是否合法、ip段的前段是否小于后段 的校验 <br>
	 * 如： 192.9.100.2-192.9.100.3;192.9.100.100-193.193.2.2中，";"为outterSeparator；"-"为innerSeparator <br>
	 * 有任一不合法，都会返回false
	 * 
	 * @param ipSegment
	 *            多个ip段，如果为空，则直接返回false；如果解析出来的单个ip段包含两个以上的ip，也只对前两个ip做校验；
	 * @param innerSeperate
	 *            每个ip段的前半段和后半段的分隔符，如果为空，直接判断当前这个ip是否合法，不做前后半段的大小校验
	 * @param outterSeperate
	 *            每个ip段之间的分隔符，如果为空，则直接认为这个是一个ip段
	 * @return 不合法则直接返回false
	 * @creator chengking
	 */
	public static boolean isValidIpSegment(String ipSegment, String innerSeparator, String outterSeparator) {
		if (StringHelper.isEmpty(ipSegment)) {
			return false;
		}

		String[] ipSegments = StringHelper.split(ipSegment, outterSeparator);
		boolean isValid = true;
		for (String segmet : ipSegments) {
			String[] ips = StringHelper.split(segmet, innerSeparator);
			// 如果ip段为空，直接返回false
			if (null == ips || ips.length == 0) {
				return false;
			}
			if (ips.length == 1) {
				// 如果只有一个ip，则直接校验是否是合法ip
				isValid = isValidV4IP(ips[0]);
			} else {
				// 否则，除了校验是否是合法ip，还要校验两个ip段是否是大小关系
				isValid = isValidV4IP(ips[0]) && isValidV4IP(ips[1]) && (ip2Long(ips[0]) < ip2Long(ips[1]));
			}

			// 只要有任一ip段不符合，都返回false
			if (!isValid) {
				return false;
			}
		}
		return true;
	}
}