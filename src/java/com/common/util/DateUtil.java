package com.common.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 日期和时间相关工具方法 <BR>
 * 
 * @author chengking
 */
public class DateUtil {

	/**
	 * 精确到毫秒数的时间格式
	 */
	private static final String DEFAULT_MIILITIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";

	/**
	 * 将使用的毫秒数转化为可读的字符串, 如1天1小时1分1秒. <BR>
	 * <code>assertEquals("1天1小时1分1秒", DateUtil.timeToString(90061000));</code>
	 * 
	 * @param msUsed
	 *            使用的毫秒数.
	 * @return 可读的字符串, 如1天1小时1分1秒.
	 */
	public static String timeToString(long msUsed) {
		if (msUsed < 0) {
			return String.valueOf(msUsed);
		}
		if (msUsed < 1000) {
			return String.valueOf(msUsed) + "毫秒";
		}
		// 长于1秒的过程，毫秒不�?
		msUsed /= 1000;
		if (msUsed < 60) {
			return String.valueOf(msUsed) + "秒";
		}
		if (msUsed < 3600) {
			long nMinute = msUsed / 60;
			long nSecond = msUsed % 60;
			return String.valueOf(nMinute) + "分" + String.valueOf(nSecond) + "秒";
		}
		// 3600 * 24 = 86400
		if (msUsed < 86400) {
			long nHour = msUsed / 3600;
			long nMinute = (msUsed - nHour * 3600) / 60;
			long nSecond = (msUsed - nHour * 3600) % 60;
			return String.valueOf(nHour) + "小时" + String.valueOf(nMinute) + "分" + String.valueOf(nSecond) + "秒";
		}

		long nDay = msUsed / 86400;
		long nHour = (msUsed - nDay * 86400) / 3600;
		long nMinute = (msUsed - nDay * 86400 - nHour * 3600) / 60;
		long nSecond = (msUsed - nDay * 86400 - nHour * 3600) % 60;
		return String.valueOf(nDay) + "天" + String.valueOf(nHour) + "小时" + String.valueOf(nMinute) + "分"
		+ String.valueOf(nSecond) + "秒";
	}

	/**
	 * 取本周一.
	 * 
	 * @return 本周一
	 */
	public static Calendar getThisMonday() {
		return getThatMonday(Calendar.getInstance());
	}

	/**
	 * 获取cal所在周的周一.
	 * 
	 * @param cal
	 *            给定日期
	 * @return cal所在周的周一
	 */
	public static Calendar getThatMonday(Calendar cal) {
		int n = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
		cal.add(Calendar.DATE, n);
		return cal;
	}

	/**
	 * 取本周日.
	 * 
	 * @return 本周日
	 */
	public static Calendar getThisSunday() {
		return getThatSunday(Calendar.getInstance());
	}

	/**
	 * 获取cal所在周的周日.
	 * 
	 * @param cal
	 *            给定日期
	 * @return cal所在周的周日
	 */
	public static Calendar getThatSunday(Calendar cal) {
		int n = (Calendar.SUNDAY + 7) - cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DATE, n);
		return cal;
	}

	/**
	 * 获取两个日期相差的天数，取绝对值.
	 * 
	 * @return 两个日期相差的天数。只可能为零或者正数。
	 */
	public static int betweenDays(Calendar begin, Calendar end) {
		long msBegin = begin.getTimeInMillis();
		long msEnd = end.getTimeInMillis();
		long between_days = (msEnd - msBegin) / (1000 * 3600 * 24);
		between_days = Math.abs(between_days);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 获取两个日期相差的天数，不取绝对值.
	 * 
	 * @param calBegin
	 *            开始日期
	 * @param calEnd
	 *            结束日期
	 * @return
	 * @creator chengking
	 */
	public static int minusWithoutAbs(Calendar calBegin, Calendar calEnd) {
		long msBegin = calBegin.getTimeInMillis();
		long msEnd = calEnd.getTimeInMillis();
		long between_days = (msEnd - msBegin) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 获取两个日期相差的天数，不取绝对值
	 * 
	 * @param begin
	 * @param end
	 * @return
	 * @creator chengking
	 */
	public static int minusWithoutAbs(Date begin, Date end) {
		long between_days = (end.getTime() - begin.getTime()) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 判断给定日期是否早于今天.
	 * 
	 * @param someDate
	 * @return
	 * @creator chengking
	 */
	@SuppressWarnings("deprecation")
	public static boolean isBeforeToday(Date someDate) {
		Date today = new Date();

		// 如果相差不到一天,则单独处理
		if (today.getTime() - someDate.getTime() > 0 && today.getTime() - someDate.getTime() < 1000 * 3600 * 24
				&& today.getDay() != someDate.getDay()) {
			return true;
		}
		return minusWithoutAbs(someDate, new Date()) > 0;
	}

	/**
	 * 指定日期是否晚于今天。
	 * 
	 * @param expireDate
	 * @return 如果晚于今天返回<code>true</code>, 否则返回<code>false</code>.
	 * @creator chengking
	 */
	public static boolean isAfterToday(Date date) {
		return minusWithoutAbs(Calendar.getInstance(), toCalendar(date)) > 0;
	}

	/**
	 * 从Date对象得到Calendar对象. <BR>
	 * JDK提供了Calendar.getTime()方法, 可从Calendar对象得到Date对象, 但没有提供从Date对象得到Calendar对象的方�?
	 * 
	 * @param date
	 *            给定的Date对象
	 * @return 得到的Calendar对象. 如果date参数为null, 则得到表示当前时间的Calendar对象.
	 */
	public static Calendar toCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		return cal;
	}

	/**
	 * 完成日期串到日期对象的转义 <BR>
	 * 
	 * @param dateString
	 *            日期字符串
	 * @param dateFormat
	 *            日期格式
	 * @return date 日期对象
	 */
	@SuppressWarnings("rawtypes")
	public static Date stringToDate(String dateString, String dateFormat) {
		if ("".equals(dateString) || dateString == null) {
			return null;
		}
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
			simpleDateFormat.setLenient(false);
			return simpleDateFormat.parse(dateString);

		} catch (Exception e) {
			for (Iterator iterator = prepareDefaultPatterns().iterator(); iterator.hasNext();) {
				String pattern = (String) iterator.next();
				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
					simpleDateFormat.setLenient(false);
					return simpleDateFormat.parse(dateString);
				} catch (Exception e1) {
				}
			}
			return null;
		}
	}

	public static String DateToString(Date date, String dateFormat) {
		if (date == null)
			return "";

		try {
			return new SimpleDateFormat(dateFormat).format(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * IDS系统默认的时间格式
	 */
	public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 将date 转成系统默认的时间格式（ yyyy-MM-dd HH:mm:ss）的方法
	 * 
	 * @param date
	 *            日期
	 * @return
	 * @creator chengking
	 */
	public static String DateToString(Date date) {
		return DateToString(date, DEFAULT_TIME_FORMAT);
	}

	/**
	 * 将timeMillis 转成系统默认的时间格式（ yyyy-MM-dd HH:mm:ss）的方法
	 * 
	 * @param timeMillis
	 * @return
	 * @creator chengking
	 */
	public static String timeMillisToString(long timeMillis) {
		return timeMillisToString(timeMillis, DEFAULT_TIME_FORMAT);
	}

	/**
	 * 完成timeMillis到日期类型的字符串的转换
	 * 
	 * @param timeMillis
	 *            以毫秒数记的long型的时间
	 * @param dateFormat
	 *            日期格式
	 * @return
	 */
	public static String timeMillisToString(long timeMillis, String dateFormat) {
		if (timeMillis == 0)
			return "";

		if ("".equals(dateFormat) || dateFormat == null) {
			return "";
		}
		try {
			return new SimpleDateFormat(dateFormat).format(new Date(timeMillis));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 完成日期串到日期对象的转换,要求时间串的格式为系统默认的时间格式(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param dateString
	 * @return 如果日期串的格式不正确，那么将返回null
	 * @creator chengking
	 */
	public static Date stringToDate(String dateString) {
		return stringToDate(dateString, DEFAULT_TIME_FORMAT);
	}

	/**
	 * 完成将指定时间格式的时间串转化成毫秒数.<br>
	 * <br>
	 * 建议输入的时间串格式与输入的时间格式保持一致,如果不一致,会尝试使用预定义的时间格式进行转化,预定义时间格式的方法：{@link #prepareDefaultPatterns()}
	 * ,如果还是没有找到匹配的时间格式,那么将返回-1. <br>
	 * <br>
	 * <b>预定义的时间格式：</b> <br>
	 * <br>
	 * 1） yyyy-MM-dd HH:mm:ss:SSS <br>
	 * 2） yyyy-MM-dd HH:mm:ss<br>
	 * 3） yyyy-MM-dd HH:mm <br>
	 * 4） yyyy-MM-dd HH <br>
	 * 5） yyyy-MM-dd <br>
	 * 6） yyyyMMdd
	 * 
	 * <br>
	 * <br>
	 * <b>Eg:</b> <br>
	 * <br>
	 * 将时间串"2011-03-08 10:15:20:621" 按照时间格式"yyyy-MM-dd HH:mm:ss:SSS"转成毫秒数(long型):1299549600000.<br>
	 * 1299549600000 = stringToTimeMillis("2011-03-08 10:15:20:621","yyyy-MM-dd HH:mm:ss:SSS");
	 * 
	 * @param dateStr
	 *            指定时间格式的时间串.
	 * @param dateFormat
	 *            时间格式;如果没有输入该参数,那么将使用默认的时间格式：{@link #DEFAULT_MIILITIME_PATTERN}
	 * @return 1) 如果没有输入参数dateStr,那么返回-1; <br>
	 *         2) 如果输入的时间串与输入的时间格式不匹配,并且在预定义的时间格式中也找不到匹配项,那么返回-1;
	 * @since v3.5
	 * @creator chengking
	 */
	public static long stringToTimeMillis(String dateStr, String dateFormat) {
		if (dateStr == null || "".equals(dateStr)) {
			return -1;
		}

		if (StringHelper.isEmpty(dateFormat)) {
			dateFormat = DEFAULT_MIILITIME_PATTERN;
		}

		Date date = DateUtil.stringToDate(dateStr, dateFormat);
		return date == null ? -1 : date.getTime();
	}

	/**
	 * 获取以系统默认的时间格式（ yyyy-MM-dd HH:mm:ss）展现的当前系统时间
	 * 
	 * @return
	 * @creator chengking
	 */
	public static String getCurrentDateTime() {
		return getCurrentDateTime(DEFAULT_TIME_FORMAT);
	}

	/**
	 * 获得以String表示的当前系统时间
	 * 
	 * @param dateFormat
	 * @return
	 * @creator chengking
	 */
	public static String getCurrentDateTime(String dateFormat) {
		return timeMillisToString(System.currentTimeMillis(), dateFormat);
	}

	/**
	 * 获取和指定cal对象相隔指定天数的cal对象. 大于0表示之后, 小于0表之前
	 * 
	 * @param cal
	 *            指定cal对象
	 * @param relativeDay
	 *            相隔指定天数
	 * @return cal对象
	 */
	public static Calendar getCalendar(Calendar cal, int relativeDay) {
		cal.add(Calendar.DATE, relativeDay);
		return cal;
	}

	/**
	 * 获取和当天相隔指定天数的Date对象. 大于0表示之后, 小于0表之前
	 * 
	 * @param relativeDay
	 *            相隔指定天数
	 * @return Date对象
	 * @see #getCalendar(Calendar, int)
	 */
	public static Date getDate(int relativeDay) {
		return getCalendar(Calendar.getInstance(), relativeDay).getTime();
	}

	/**
	 * 获取和给定日期相隔指定天数的Date对象. 大于0表示之后, 小于0表之前
	 * 
	 * @param date
	 *            给定日期
	 * @param relativeDay
	 *            相隔天数
	 * @return
	 * @creator chengking
	 */
	public static Date getDate(Date date, int relativeDay) {
		return getCalendar(toCalendar(date), relativeDay).getTime();
	}

	public static int month2second(int month) {
		return month * 30 * 24 * 3600;
	}

	public static int month2second(double month) {
		return (int) (month * 30 * 24 * 3600);
	}

	/**
	 * @param expireDate
	 * @return
	 * @creator chengking
	 */
	public static String formatDate(Date date) {
		return DateToString(date, "yyyy-MM-dd");
	}

	/**
	 * 
	 * @param date
	 * @return
	 * @since v3.5
	 * @creator chengking
	 */
	public static Date parseDate(String date) {
		return parseDate(date, prepareDefaultPatterns());
	}

	/**
	 * @param date
	 * @return
	 * @since v3.5
	 * @creator chengking
	 */
	@SuppressWarnings("rawtypes")
	public static Date parseDate(String date, List patterns) {
		if (date == null) {
			return null;
		}

		for (Iterator iterator = patterns.iterator(); iterator.hasNext();) {
			String pattern = (String) iterator.next();
			try {
				return new SimpleDateFormat(pattern).parse(date);
			} catch (Exception e) {
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List prepareDefaultPatterns() {
		List patterns = new ArrayList();
		patterns.add("yyyy-MM-dd HH:mm:ss:SSS");
		patterns.add("yyyy-MM-dd HH:mm:ss");
		patterns.add("yyyy-MM-dd HH:mm");
		patterns.add("yyyy-MM-dd HH");
		patterns.add("yyyy-MM-dd");
		//@ Dec 18, 2009: yyyyMMdd必须放到最后, 否则会影响解析结果!
		// 比如把2010-03-28解析为20090210
		patterns.add("yyyyMMdd");
		return patterns;
	}

	/**
	 * 获取当天凌晨时间long�?
	 * 
	 * @return
	 * @since v3.5
	 * @creator shixin @ 2012-4-12
	 */
	public static long getThisMorning() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	/**
	 * 获知指定天数的�?毫秒�?
	 * 
	 * @param day
	 * @return 总的毫秒�?
	 * @since v3.5
	 * @creator wangcheng @ 2012-8-31
	 */

	public static int getMillisecond(int day) {
		int totlMillisecond = day * 24 * 60 * 60 * 1000;
		return totlMillisecond;
	}

	/**
	 * 将long类型的字符串转成Date类型
	 * 
	 * @param longStr
	 * @return
	 */
	public static Date longStrToDate(String longStr) {
		long dateAsLong = StringHelper.parseLong(longStr);
		String dateStr = timeMillisToString(dateAsLong);
		return stringToDate(dateStr);
	}
}