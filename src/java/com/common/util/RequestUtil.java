package com.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * 封装HttpRequest相关操作的工具类 <BR>
 * 
 * @author ChengKing
 */
public class RequestUtil {

	private static final Logger LOG = Logger.getLogger(RequestUtil.class);

	/**
	 * IBM的DataPower等设备的request的header头
	 */
	private static final String ORIGINAL_REMOTE_IP_ADDRESS_TOKEN_CLIENT_IP = "X-Client-IP";

	/**
	 * 通过HTTP代理或负载均衡方式连接到Web服务器的客户端最原始的IP地址的HTTP请求头字段
	 */
	private static final String ORIGINAL_REMOTE_IP_ADDRESS_TOKEN_FORWARDED_FOR = "X-Forwarded-For";
	/**
	 * 初始地址
	 */
	private static final String ORIGINAL_REMOTE_IP_ADDRESS_TOKEN_ORIGINALING_IP = "X-Originating-IP";

	/**
	 * 获取指定request的指定参数的整数值.
	 * 
	 * @param request
	 *            指定request
	 * @param param
	 *            指定参数
	 * @return 参数值的整数形式. 如果该参数不存在或者解析整数时发生了异常, 则返回0.
	 */
	public static int getParameterAsInt(HttpServletRequest request, String param) {
		return getParameterAsInt(request, param, 0);
	}

	/**
	 * @param request
	 * @param param
	 * @return
	 * @creator yaonengjun @ 2009-7-29
	 */
	public static int getParameterAsPositiveInt(HttpServletRequest request, String param, int defaultValue) {
		int result = getParameterAsInt(request, param, 0);
		if (result <= 0)
			result = defaultValue;

		if (result <= 0)
			result = 15;

		return result;
	}

	/**
	 * 获取指定request的指定参数的整数值. 如果该参数不存在或者解析整数时发生了异常, 则返回给定的默认值.
	 * 
	 * @param request
	 *            指定request
	 * @param param
	 *            指定参数
	 * @param defaultValue
	 *            给定的默认值.
	 * @return 参数值的整数形式. 如果该参数不存在或者解析整数时发生了异常, 则返回给定的默认值.
	 */
	public static int getParameterAsInt(HttpServletRequest request, String param, int defaultValue) {
		final String value = request.getParameter(param);
		try {
			return (value == null || value.trim().length() == 0) ? defaultValue : Integer.parseInt(value);
		} catch (Exception e) {
			LOG.error(e + ", param=" + param + ", value=" + value + ". skip the exception and return " + defaultValue);
		}
		return defaultValue;
	}

	/**
	 * 获取指定request的指定参数的整数值. 如果该参数不存在或者解析整数时发生了异常, 则返回给定的默认值.
	 * 
	 * @param request
	 *            指定request
	 * @param param
	 *            指定参数
	 * @param defaultValue
	 *            给定的默认值.
	 * @return 参数值的整数形式. 如果该参数不存在或者解析整数时发生了异常, 则返回给定的默认值.
	 */
	public static double getParameterAsDouble(HttpServletRequest request, String param, int defaultValue) {
		final String value = request.getParameter(param);
		try {
			return (value == null || value.trim().length() == 0) ? defaultValue : Double.parseDouble(value);
		} catch (Exception e) {
			LOG.error(e + ", param=" + param + ", value=" + value + ". skip the exception and return " + defaultValue);
		}
		return defaultValue;
	}

	/**
	 * 获得请求的实际地址，支持X-Forwarded-For/X-Client-IP/X-Originating-IP等。<br>
	 * 并且支持自定义的Forwarded-For的HTTP头。<br>
	 * 
	 * @param request
	 *            HTTP请求
	 * @return 如果有反向代理转发则获取到第一个起始地址，否则返回当前请求的request.getRemoteAddr()
	 * @since v1.0
	 * @creator yaonengjun @ Oct 9, 2010
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String xForwardedFor = getXForwardedFor(request);
		String remoteAddr = "";
		if (false == StringHelper.isEmpty(xForwardedFor)) {
			remoteAddr = StringHelper.split(xForwardedFor, ",")[0];
		}
		return StringHelper.isEmpty(remoteAddr) ? request.getRemoteAddr() : remoteAddr;
		// return StringHelper.split(xForwardedFor, ",")[0];
		// // // 根据设置的变量提取X-Forwarded-For/X-Client-IP/X-Originating-IP
		// // String xForwardedForName = request.getHeader("XForwardedFor-Name");
		// // if (StringHelper.isEmpty(xForwardedForName)) {//
		// // 如果没有设置XForwardedFor名称的话，则一次取X-Forwarded-Fox、X-Client-IP、X-Originating-IP
		// // xForwardedFor = request.getHeader(xForwardedForName);
		// // }
		// // //
		// // String remoteAddr = "";
		// // if (false == StringHelper.isEmpty(xForwardedFor)) {
		// // remoteAddr = StringHelper.split(xForwardedFor, ",")[0];
		// // }
		// // return StringHelper.isEmpty(remoteAddr) ? request.getRemoteAddr() : remoteAddr;
		// // String clientIPs = request.getHeader(ORIGINAL_REMOTE_IP_ADDRESS_TOKEN_CLIENT_IP);
		// // if (false == StringHelper.isEmpty(clientIPs)) {
		// // remoteAddr = StringHelper.split(clientIPs, ",")[0];
		// // }
		// // return remoteAddr;
	}

	/**
	 * 根据管理台配置的原始请求IP标记，获取获得请求的实际地址。
	 * 
	 * @param request
	 * @param originalClientIPHttpHeaderToken
	 * @return
	 * @since v1.0
	 * @creator shixin @ 2010-11-24
	 * @deprecated
	 */
	public static String getRemoteAddr(HttpServletRequest request, String originalClientIPHttpHeaderToken) {
		return RequestUtil.getRemoteAddr(request);
		// String remoteAddr = request.getRemoteAddr();
		// String clientIPs = null;
		// if (false == StringHelper.isEmpty(originalClientIPHttpHeaderToken)) {
		// clientIPs = request.getHeader(originalClientIPHttpHeaderToken);
		// if (false == StringHelper.isEmpty(clientIPs)) {
		// remoteAddr = StringHelper.split(clientIPs, ",")[0];
		// }
		// LOG.debug("get original client ip by originalClientIPHttpHeaderToken: " + originalClientIPHttpHeaderToken
		// + ", client ips: " + clientIPs + ", return original client ip: " + remoteAddr);
		// }
		// return remoteAddr;
	}

	/**
	 * 获取指定request的指定参数的所有整数值. 如果该参数不存在,则返回length为0的数组;如果解析某个整数时发生了异常,则将该整数设为给定的默认值.
	 * 
	 * @param request
	 *            指定request
	 * @param param
	 *            指定参数名
	 * @param defaultValue
	 *            给定的默认值
	 * @return 包括所有参数值的整数数组
	 * @creator huangshengbo @ 2009-7-9
	 */
	public static int[] getParameterValuesAsInt(HttpServletRequest request, String param, int defaultValue) {
		String[] values = request.getParameterValues(param);
		if (values == null)
			return new int[] {};
		int[] result = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			try {
				result[i] = (value == null || value.trim().length() == 0) ? defaultValue : Integer.parseInt(value);
			} catch (Exception e) {
				LOG.error(e + ", param=" + param + ", value=" + value + ". skip the exception and return "
						+ defaultValue);
			}
		}
		return result;
	}

	/**
	 * 获取指定request的指定参数的所有整数值. 如果该参数不存在,则返回length为0的数组;如果解析某个整数时发生了异常,则将该整数设为给定的默认值.
	 * 
	 * @param request
	 *            指定request
	 * @param param
	 *            指定参数名
	 * 
	 * @return 包括所有参数值的字符串数组
	 * @creator lizhuping @ 2009-9-15
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String[] getParamterValuesAndTrim(HttpServletRequest request, String param) {
		String[] values = request.getParameterValues(param);
		if (values == null)
			return new String[] {};
		List result = new ArrayList();
		for (int i = 0; i < values.length; i++) {
			if (StringHelper.isEmpty(values[i])) {
				continue;
			}
			result.add(values[i]);
		}
		return (String[]) result.toArray(new String[0]);
	}

	/**
	 * 以基本类型boolean值返回给定的Request对象中的指定参数的取值. 如果Request对象中找不到该指定参数, 则返回false.
	 * 
	 * @param request
	 *            给定的Request对象
	 * @param param
	 *            指定参数
	 * @return 给定的Request对象中的指定参数的boolean取值
	 * @see RequestUtil#getParameterAsBool(HttpServletRequest, String, boolean)
	 */
	public static boolean getParameterAsBool(HttpServletRequest request, String param) {
		return getParameterAsBool(request, param, false);
	}

	/**
	 * 以基本类型boolean值返回给定的Request对象中的指定参数的取值. 如果Request对象中找不到该指定参数, 则返回给定的默认值. <BR>
	 * 表示布尔值的字符串大小写无关. 当且仅当表示布尔值的字符串为"true"时(忽略大小写), 返回true. 例如: <tt>"True"</tt> 返回 <tt>true</tt>.<br>
	 * 再如: <tt>"yes"</tt> 返回 <tt>false</tt>.
	 * 
	 * @param request
	 *            给定的Request对象
	 * @param param
	 *            指定参数
	 * @param defaultValue
	 *            默认取值
	 * @return 给定的Request对象中的指定参数的boolean取值
	 */
	public static boolean getParameterAsBool(HttpServletRequest request, String param, boolean defaultValue) {
		boolean result = defaultValue;
		if (null == request) {
			return result;
		}
		String paramValue = request.getParameter(param);
		if (!StringHelper.isEmpty(paramValue)) {
			result = "true".equalsIgnoreCase(paramValue);
		}
		return result;
	}

	/**
	 * 获取指定request的指定参数的值. 如果不存在该参数, 返回空串而不是null. 不再做转码处理.
	 * 
	 * @param req
	 *            指定request
	 * @param param
	 *            指定参数
	 * @return 如果不存在该参数, 返回空串而不是null.
	 * @deprecated ls@07-0521 由于在Filter中前置处理了编码, 不再有必要使用该方法. 用{@link #getParameterAndTrim(HttpServletRequest, String)}
	 *             代替.
	 */
	public static String getParameterAsGBK(HttpServletRequest req, String param) {
		String result = req.getParameter(param);
		return (result == null) ? "" : result.trim();
		// return getParamByEncoding(req, param, "ISO-8859-1", "GBK");
	}

	/**
	 * 获取指定request的指定参数的值并作trim处理. 如果不存在该参数, 返回"".
	 * 
	 * @return 指定参数的值并作trim处理
	 * @since ls@08.0107
	 */
	public static String getParameterAndTrim(HttpServletRequest req, String param) {
		return getParameterAndTrim(req, param, null);
	}

	/**
	 * 获取指定request的指定参数的值并作trim处理. 如果不存在该参数, 返回"".
	 * 
	 * @return 指定参数的值并作trim处理
	 * @since ls@08.0107
	 */
	public static String getParameterAndTrim(HttpServletRequest req, String param, String defaultValue) {
		String reslut = req.getParameter(param);

		if (StringHelper.isEmpty(reslut)) {
			reslut = defaultValue;
		}

		return (reslut == null) ? "" : reslut.trim();
	}

	/**
	 * 跨站脚本的攻击已经由部门组件提供处理，参见 TRSIDS-1874。因此本函数已经失去其作用，废弃。<br>
	 * 请转调 {@link #getParameterAndTrim(HttpServletRequest, String)}函数<br>
	 * <br>
	 * 对输入的参数进行判断，避免跨站式脚本攻击
	 * 
	 * @param req
	 * @param param
	 * @return
	 */
	public static String getParameterSafe(HttpServletRequest req, String param) {
		return getParameterAndTrim(req, param);
	}

	public static String getAttributeAsTrimStr(HttpServletRequest req, String attrName) {
		return getAttributeAsTrimStr(req, attrName, "");
	}

	public static String getAttributeAsTrimStr(HttpServletRequest req, String attrName, String defValue) {
		Object obj = req.getAttribute(attrName);
		return (obj != null) ? (obj.toString()).trim() : defValue;
	}

	/**
	 * 检验是否存在指定的参数项.
	 * 
	 * @param req
	 *            指定的request(Http请求)
	 * @param param
	 *            指定的参数项
	 * @return 存在返回true, 否则返回false
	 * @deprecated ls@07-0529 没有必要提供这样的方法.
	 */
	public static boolean existParameter(HttpServletRequest req, String param) {
		if (param == null) {
			return false;
		}
		return req.getParameter(param) != null;
	}

	/**
	 * 获取表示给定HTTP请求的完整URL字符串(包括QueryString). <BR>
	 */
	public static String getFullGetStr(HttpServletRequest req) {
		final String qryStr = req.getQueryString();
		if (qryStr == null) {
			return req.getRequestURL().toString();
		}

		return req.getRequestURL().append('?').append(qryStr).toString();
	}

	/**
	 * 返回页面文件名.比如访问的是http://xxx.com/app1/path1/path2/page3.jsp, 则该方法返回page3.jsp.
	 */
	public static String getCurrentPage(HttpServletRequest req) {
		final String requestURI = req.getRequestURI();
		return requestURI.substring(requestURI.lastIndexOf('/') + 1);
	}

	public static String getCurPageWithQryStr(HttpServletRequest req) {
		return getCurPageWithQryStr(req, null);
	}

	public static String getCurPageWithQryStr(HttpServletRequest req, String excludeParam) {
		String qryStr = removeQryParam(req.getQueryString(), excludeParam);
		if (qryStr == null) {
			return getCurrentPage(req);
		}
		return new StringBuffer(getCurrentPage(req)).append('?').append(qryStr).toString();
	}

	/**
	 * 排除链接中的某些参数。<br>
	 * 连接可以是如下形式：<br>
	 * <ul>
	 * <li>http://192.9.200.72:7070/ids/admin/list.jsp?pageNo=1&pageSize=15</li><br>
	 * <li>ids/admin/list.jsp?pageNo=1&pageSize=15</li> <br>
	 * <li>list.jsp?pageNo=1&pageSize=15</li> <br>
	 * </ul>
	 * 
	 * @param hrefWithParam
	 *            带参数的连接地址
	 * @param excludeParam
	 *            要排除的参数
	 * @return
	 * @creator yaonengjun @ 2009-9-10
	 */
	public static String removeQryParamOfURI(String hrefWithParam, String excludeParam) {
		if (StringHelper.isEmpty(hrefWithParam))
			return "";

		if (StringHelper.isEmpty(excludeParam))
			return hrefWithParam;

		if ((StringHelper.split(hrefWithParam, "?")).length != 2)
			return hrefWithParam;

		if (hrefWithParam.indexOf(excludeParam) < 0)
			return hrefWithParam;

		String link = hrefWithParam.substring(0, hrefWithParam.indexOf("?"));
		String queryString = hrefWithParam.substring(hrefWithParam.indexOf("?") + 1, hrefWithParam.length());
		String excludedQueryString = removeQryParam(queryString, excludeParam);
		if (!StringHelper.isEmpty(excludedQueryString))
			link += "?" + excludedQueryString;

		return link;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getCurPageWithQryStr(HttpServletRequest req, String param, String excludeParam) {
		String qryStr = removeQryParam(req.getQueryString(), param);
		Map resultMap = new HashMap();
		if (!StringHelper.isEmpty(qryStr)) {
			Map qryStrMap = StringHelper.String2Map(qryStr, "&", "=");

			if (qryStrMap != null) {
				for (Iterator i = qryStrMap.keySet().iterator(); i.hasNext();) {
					String key = (String) i.next();
					if (key.equals(excludeParam))
						continue;

					String value = (String) qryStrMap.get(key);
					resultMap.put(key, value);
				}
			}

		}

		if (resultMap.size() > 0)
			qryStr = StringHelper.Map2String(resultMap, "&", "=");

		if (qryStr == null) {
			return getCurrentPage(req);
		}
		return new StringBuffer(getCurrentPage(req)).append('?').append(qryStr).toString();
	}

	public static String removeQryParam(String qryStr, String param) {
		if (qryStr == null || param == null) {
			return qryStr;
		}
		String[] params = StringHelper.split(qryStr, "&");
		StringBuffer sb = new StringBuffer(qryStr.length());
		for (int i = 0; i < params.length; i++) {
			if (params[i].startsWith(param + "=")) {
				continue;
			}
			sb.append(params[i]).append('&');
		}
		return (sb.length() > 0) ? sb.deleteCharAt(sb.length() - 1).toString() : null;
	}

	/**
	 * 给指定的URL添加参数
	 * 
	 * @param qryStr
	 *            原请求URL
	 * @param param
	 *            参数表达式 param1=values&param2=value2.....
	 * @return 返回包含param的完整URL
	 * 
	 */
	public static String addQryParam(String qryStr, String param) {
		if (qryStr == null || param == null) {
			return qryStr;
		}
		if (qryStr.indexOf('?') == -1 && qryStr.indexOf('=') == -1) {
			return qryStr + '?' + param;
		}

		// TODO 增加工具方法，检验参数和URL的格式，同时把非法的URL转变为合法的URL
		if (qryStr.endsWith("?"))
			qryStr = qryStr.substring(0, qryStr.indexOf('?'));
		return qryStr + (qryStr.indexOf('?') >= 0 ? '&' : '?') + param;
	}

	// HTTP标准头
	public static final String HEADER_USER_AGENT = "user-agent";
	public static final String HEADER_REFER = "referer";
	public static final String HEADER_ENCODING = "encoding";


	public static String getRequestInfo(HttpServletRequest req) {
		StringBuffer sb = new StringBuffer(320);
		sb.append("[Req]");
		sb.append(req.getClass().getName());
		sb.append(": (").append(req.getScheme()).append(')').append(req.getServerName()).append(':')
				.append(req.getServerPort());
		sb.append(", ").append(req.getMethod()).append(' ').append(req.getProtocol());
		sb.append(", uri=").append(req.getRequestURI());
		sb.append(", ctx=").append(req.getContextPath());
		sb.append(", servlet=").append(req.getServletPath());
		sb.append(", qryStr=").append(req.getQueryString());
		sb.append(", refer=").append(req.getHeader(HEADER_REFER));
		sb.append(", useragt=").append(req.getHeader(HEADER_USER_AGENT));
		sb.append(", ip=").append(req.getRemoteAddr());
		sb.append(", clientIP=").append(req.getHeader(ORIGINAL_REMOTE_IP_ADDRESS_TOKEN_CLIENT_IP));
		sb.append(", encoding=").append(req.getCharacterEncoding());
		return sb.toString();
	}

	/**
	 * 获取给定的request中全部的Header信息.
	 * 
	 * @param req
	 *            给定的request
	 * @return 全部Header信息构成的字符串.
	 */
	@SuppressWarnings("rawtypes")
	public static String getAllHeadersStr(HttpServletRequest req) {
		StringBuffer sb = new StringBuffer(256);
		String header = null;
		for (Enumeration headers = req.getHeaderNames(); headers.hasMoreElements();) {
			header = (String) headers.nextElement();
			sb.append(header);
			sb.append("=");
			sb.append(req.getHeader(header));
			sb.append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * 获取用户客户端信息，即位于header中的userAGent信息
	 * 
	 * @param request
	 * @return 如果request为空，或者取不到useragent，都返回空字符串
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getUserAgent(HttpServletRequest request) {
		if (null == request) {
			return "";
		}
		String userAgent = request.getHeader(HEADER_USER_AGENT);
		if (StringHelper.isEmpty(userAgent)) {
			return "";
		}
		return userAgent;
	}

	/**
	 * 获取给定的Http请求的Referer URL, 即上一个页面. <BR>
	 * 
	 * @param req
	 *            给定的Http请求
	 * @return 给定Http请求的referer头的值. 如果不存在, 返回""而不是null.
	 */
	public static String getReferUrl(HttpServletRequest req) {
		String referUrl = req.getHeader(HEADER_REFER);
		return (referUrl == null) ? "" : referUrl;
	}

	/**
	 * 获取给定字符串在给定请求的URL(相对于该应用)中的位置. <BR>
	 * 对动态页面,等价于<code>req.getServletPath().indexOf(someUri)</code> 例子: requestURI: /app/login.htm; ctx: /app; uri:
	 * /login.htm; return: 0
	 * 
	 * @param req
	 *            给定请求
	 * @param someUri
	 *            给定字符串
	 * @return 给定字符串在请求URL中的位置. 如果给定字符串(someUri)为null或"", 返回-2.
	 */
	public static int getPageUriPosInRequest(HttpServletRequest req, String someUri) {
		if (someUri == null || someUri.trim().length() == 0) {
			return -2;
		}
		return getRelativePath(req).indexOf(someUri);
		// return req.getServletPath().indexOf(someUri);
	}

	/**
	 * 获取反向代理情况下的ContextRoot
	 * 
	 * @param request
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getContextRoot(HttpServletRequest request, String originalHostHttpHeader) {
		String sysUrl = getOriginalHost(request, originalHostHttpHeader);
		if (StringHelper.isEmpty(sysUrl)) {
			sysUrl = request.getRequestURL().toString();
		}

		if (sysUrl.indexOf(request.getServletPath()) == -1) {
			return request.getScheme() + "://" + sysUrl + request.getContextPath();
		}
		return sysUrl.substring(0, sysUrl.indexOf(request.getServletPath()));
	}

	/**
	 * Return the webapp root path.<br>
	 * Example:<br>
	 * if request "http://localhost:8080/app1/dir1/page1.jsp", the method return "http://localhost:8080/app1".
	 */
	public static String getContextRoot(HttpServletRequest request) {
		return getContextRoot(request, null);
	}

	/**
	 * for Dynamic Pages, this method as same as <code>req.getServletPath()</code>, but the method also valid for Static
	 * Content, such as html, gif, css etc.<br>
	 * Example:<br>
	 * if request "http://localhost:8080/app1/dir1/page1.jsp", the method return "/dir1/page1.jsp".
	 * 
	 * @param req
	 *            the spec request
	 * @return the relative url
	 */
	public static String getRelativePath(HttpServletRequest req) {
		// ls@2005-11-02
		// req.getRequestURI().substring(req.getContextPath().length()) ==
		// req.getServletPath() ? NO! i.e.WebLogic!
		return req.getRequestURI().substring(req.getContextPath().length());
	}

	/**
	 * @param application
	 * @return ServletContainerInfo
	 */
	public static String getServletContainerInfo(final ServletContext application) {
		StringBuffer sb = new StringBuffer(64);
		sb.append(application.getServerInfo());
		sb.append(" (Servlet ").append(application.getMajorVersion()).append('.').append(application.getMinorVersion())
				.append(')');
		return sb.toString();
	}

	/**
	 * simple log method for jsp page.
	 * 
	 * @param obj
	 * @param req
	 */
	public static void log(Object obj, HttpServletRequest req) {
		StringBuffer sb = new StringBuffer(256);
		sb.append(new java.sql.Timestamp(System.currentTimeMillis()));
		if (req != null) {
			sb.append('\t').append(req.getRequestURI());
		}
		sb.append('\t').append(obj);
		System.out.println(sb);
	}

	/**
	 * simple log method for jsp page.
	 * 
	 * @param req
	 */
	public static void log(HttpServletRequest req) {
		log(getRequestInfo(req), null);
	}

	/**
	 * 
	 * @creator ChengKing
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getAllParameters(HttpServletRequest req) {
		Map parameters = new Hashtable();
		for (Enumeration params = req.getParameterNames(); params.hasMoreElements();) {
			String key = (String) params.nextElement();
			String value = req.getParameter(key);
			if (value != null) {
				parameters.put(key, value);
			}
		}
		return parameters;
	}

	/**
	 * 获得HttpServletRequest中的get参数
	 * 
	 * @param queryString
	 *            调用HttpServletRequest的getQueryString() 方法获得的参数，其形式如 paramA=aa&paramB=bb
	 * @return 以Map的方式保存HttpServletRequest中的get参数
	 * @creator ChengKing
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Map getGetParametersAsMap(String queryString) {
		Map parameters = new HashMap();
		if (StringHelper.isEmpty(queryString)) {
			LOG.debug("queryString is null, return empty parameters");
			return parameters;
		}

		try {
			String[] parametersAsStringArray = StringHelper.split(queryString, "&");
			for (int i = 0; i < parametersAsStringArray.length; i++) {
				String parameterString = parametersAsStringArray[i];
				String[] keyValues = StringHelper.splitAtFirstToken(parameterString, "=");
				parameters.put(keyValues[0], keyValues[1]);
			}
		} catch (Exception e) {
			LOG.error("get GetParam fail, origal queryString from HttpRequest: " + queryString, e);
			return new HashMap();
		}

		return parameters;
	}

	@SuppressWarnings("rawtypes")
	public static Map getGetParametersAsMap(HttpServletRequest request) {
		return getGetParametersAsMap(request.getQueryString());
	}

	public static String getGetParametersAsString(HttpServletRequest request) {
		return request.getQueryString();
	}

	@SuppressWarnings("rawtypes")
	public static Map getPostParametersAsMap(HttpServletRequest request) {
		Map allParameters = RequestUtil.getAllParameters(request);
		Map getParameters = RequestUtil.getGetParametersAsMap(request.getQueryString());
		return RequestUtil.getPostParametersAsMap(allParameters, getParameters);
	}

	@SuppressWarnings("rawtypes")
	public static String getPostParametersAsString(HttpServletRequest request) {
		Map allParameters = RequestUtil.getAllParameters(request);
		Map getParameters = RequestUtil.getGetParametersAsMap(request.getQueryString());
		return RequestUtil.getPostParametersAsString(allParameters, getParameters);
	}

	/**
	 * 获得以Map方式保存的Post参数
	 * 
	 * @param allParameters
	 *            调用{@link RequestUtil#getAllParameters(HttpServletRequest)}方法得到的包含所有参数（包括Get和Post的）的Map
	 * @param getParameters
	 *            调用{@link RequestUtil#getGetParametersAsMap(HttpServletRequest)}方法得到的包含Get参数的Map
	 * @return 获得以Map方式保存的Post参数
	 * @creator ChengKing
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Map getPostParametersAsMap(Map allParameters, Map getParameters) {
		if (allParameters == null || allParameters.size() == 0)
			return new HashMap();

		if (getParameters == null || getParameters.size() == 0)
			return allParameters;

		// 2、遍历所有参数,排除掉Get参数中的值，剩下的即为Post参数的值
		Map postParameters = new HashMap();
		Iterator it = allParameters.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();

			if (getParameters.containsKey(key)) {
				continue;
			} else {
				postParameters.put(key, value);
			}
		}
		return postParameters;
	}

	/**
	 * 获得以String方式保存的Post参数,把Map中的参数全部保存为类似 paramA=aa&paramB=bb&paramC=cc的字符串
	 * 
	 * 
	 * @param allParameters
	 *            调用{@link RequestUtil#getAllParameters(HttpServletRequest)}方法得到的包含所有参数（包括Get和Post的）的Map
	 * @param getParameters
	 *            调用{@link RequestUtil#getGetParametersAsMap(HttpServletRequest)}方法得到的包含Get参数的Map
	 * @return 获得以String方式保存的Post参数
	 * @creator ChengKing
	 */
	@SuppressWarnings("rawtypes")
	protected static String getPostParametersAsString(Map allParameters, Map getParameters) {
		Map postParameters = getPostParametersAsMap(allParameters, getParameters);

		if (postParameters.size() == 0)
			return null;

		StringBuffer strBuff = new StringBuffer();
		Iterator it = postParameters.entrySet().iterator();
		int counter = 0;
		int mapSize = postParameters.size();
		while (it.hasNext()) {
			counter++;
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			strBuff.append(key);
			strBuff.append("=");
			strBuff.append(value);

			if (counter < mapSize)
				strBuff.append("&");

		}

		return strBuff.toString();

	}

	/**
	 * 连接两个url
	 * 
	 * @param head
	 * @param tail
	 * @return
	 * @since v5.0
	 * @creator ChengKing
	 */
	public static String concatURL(String head, String tail) {
		if (StringHelper.isEmpty(head) && StringHelper.isEmpty(tail)) {
			return "";
		} else if (StringHelper.isEmpty(head)) {
			return tail;
		} else if (StringHelper.isEmpty(tail)) {
			return head;
		} else if (head.endsWith("/") && tail.startsWith("/")) {
			return head + tail.substring(1);
		} else if (head.endsWith("/") || tail.startsWith("/")) {
			return head + tail;
		} else {
			return head + "/" + tail;
		}
	}

	/**
	 * 获得应用到应用根的全路径，例如 http://192.9.200.72:9090/demo1
	 * 
	 * @param request
	 * @return
	 * @creator ChengKing
	 */
	public static String getAppBaseURL(HttpServletRequest request) {
		return getContextRoot(request);
	}

	public static String getParameterIndirect(HttpServletRequest request, String paramName) {
		return getParameterIndirect(request, paramName, null);
	}

	/**
	 * HttpRequest.getParameter的替代方法。<br>
	 * 由于GeneralSSOFilter中有时直接调用HttpRequest的getParameter会有乱码问题，因为需要这个替代方法。<br>
	 * <br>
	 * 
	 * 取到HttpRequest的 queryString，将其做字符串解析。
	 * 
	 * @param request
	 * @param paramName
	 * @param defaultValue
	 * @return
	 * @creator ChengKing
	 */
	public static String getParameterIndirect(HttpServletRequest request, String paramName, String defaultValue) {
		String returnValue = getParameterIndirectInternal(request.getQueryString(), paramName);
		if (!StringHelper.isEmpty(returnValue))
			return returnValue;

		if (!StringHelper.isEmpty(defaultValue))
			return defaultValue;

		return null;
	}

	@SuppressWarnings("rawtypes")
	protected static String getParameterIndirectInternal(String queryString, String paramName) {
		if (StringHelper.isEmpty(queryString))
			return null;

		Map parameters = StringHelper.String2Map(queryString, "&", "=");
		if (parameters == null || parameters.size() == 0)
			return null;

		return (String) parameters.get(paramName);
	}

	/**
	 * 判断Http请求的类型是否是"multipart/form-data"
	 * 
	 * Attribute for <FORM ...> ENCTYPE = "multipart/form-data" | "application/x-www-form-urlencoded" | "text/plain" In
	 * most cases you will not need to use this attribute at all. The default value (i.e. if you don't use this
	 * attribute at all) is "application/x-www-form-urlencoded", which is sufficient for almost any kind of form data.
	 * The one exception is if you want to do file uploads. In that case you should use "multipart/form-data".
	 * 
	 * @param req
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean isMultipartRequest(HttpServletRequest req) {
		String type = null;
		String type1 = req.getHeader("Content-Type");
		String type2 = req.getContentType();
		// If one value is null, choose the other value
		if (type1 == null && type2 != null) {
			type = type2;
		} else if (type2 == null && type1 != null) {
			type = type1;
		}
		// If neither value is null, choose the longer value
		else if (type1 != null && type2 != null) {
			type = (type1.length() > type2.length() ? type1 : type2);
		}
		if (type == null || !type.toLowerCase().startsWith("multipart/form-data")) {
			return false;
		}
		return true;
	}

	/**
	 * 是否为一个反向代理的请求。<br>
	 * 反向代理请求是指经过了反向代理软件的HTTP请求。根据在Agent端或者IDS端配置的http header进行判断。
	 * 
	 * @return 如果以上判断为true，则返回
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static boolean isReverseProxyRequest(HttpServletRequest request, String originalHostHttpHeader) {
		if (StringHelper.isEmpty(originalHostHttpHeader)) {
			return false;
		}

		String originalHost = getXForwardedHost(request);
		LOG.debug("request.getRequestURL(): " + request.getRequestURL() + "; originalHostHttpHeader: "
				+ originalHostHttpHeader + "; originalHost in request: " + originalHost);

		if (StringHelper.isEmpty(originalHost)) {
			LOG.debug("request.getRequestURL(): " + request.getRequestURL() + "; Can not get value http header["
					+ originalHostHttpHeader
					+ "], configuration of trsids-agent.properties or Reverse Proxy might be error.");
			return false;
		}

		return true;
	}

	/**
	 * 根据配置的HttpHeader，获得当前Http请求中的原始地址。<br>
	 * 如果该HTTP头中有多个host，尝试用分号将其分隔开并返回第一个。<br>
	 * 
	 * @return
	 * @deprecated {@link RequestUtil#getOriginalHost(HttpServletRequest)}
	 */
	public static String getOriginalHost(HttpServletRequest request, String originalHostHttpHeader) {
		return RequestUtil.getOriginalHost(request);
		// if (StringHelper.isEmpty(originalHostHttpHeader)) {
		// return request.getServerName();
		// }
		//
		// String originalHostInRequest = request.getHeader(originalHostHttpHeader);
		// if (LOG.isDebugEnabled())
		// LOG.debug("request.getRequestURL(): " + request.getRequestURL() + "; OriginalHostHttpHeader: "
		// + originalHostHttpHeader + "; originalHost in request: " + originalHostInRequest);
		// if (StringHelper.isEmpty(originalHostInRequest))
		// return request.getServerName();
		//
		// String[] originalHosts = StringHelper.splitAndTrim(originalHostInRequest, ",");
		// String originalHostZero = originalHosts[0];
		//
		// if (LOG.isDebugEnabled())
		// LOG.debug("After split from comma: " + originalHosts + "; originalHosts[0]: " + originalHostZero);
		// return originalHostZero;
	}

	/**
	 * 根据配置的HttpHeader，获得当前Http请求中的原始地址。<br>
	 * 如果该HTTP头中有多个host，尝试用分号将其分隔开并返回第一个。<br>
	 * 
	 * @return
	 */
	public static String getOriginalHost(HttpServletRequest request) {
		// 根据设置的变量提取X-Forwarded-For/X-Client-IP/X-Originating-IP
		String xForwardedHostName = (String) request.getAttribute("XForwardedHost-Name");
		if (StringHelper.isEmpty(xForwardedHostName)) {// 如果从Attribute里面取不到的话，则再从Header里面再取一次
			xForwardedHostName = request.getHeader("XForwardedHost-Name");
		}
		if (StringHelper.isEmpty(xForwardedHostName)) {// 如果取到设置的XForwardedHost-Name，则从Header里面取值
			return request.getServerName();
		}
		//
		String xForwardedHost = request.getHeader(xForwardedHostName);
		if (LOG.isDebugEnabled())
			LOG.debug("request.getRequestURL(): " + request.getRequestURL() + "; OriginalHostHttpHeader: "
					+ xForwardedHostName + "; originalHost in request: " + xForwardedHost);
		if (StringHelper.isEmpty(xForwardedHost))
			return request.getServerName();
		String originalHost = StringHelper.splitAndTrim(xForwardedHost, ",")[0];
		if (LOG.isDebugEnabled())
			LOG.debug("After split from comma: " + xForwardedHost + "; originalHosts[0]: " + originalHost);
		return originalHost;
	}

	/**
	 * 从Http头中获取到反向代理Host，
	 * 
	 * @param request
	 * @param originalHostHttpHeader
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getOriginalUrlWithParam(HttpServletRequest request, String originalHostHttpHeader) {
		if (!isReverseProxyRequest(request, originalHostHttpHeader)) {
			String fullGetUrl = getFullGetStr(request);
			if (LOG.isDebugEnabled())
				LOG.debug("originalHostHttpHeader is null, may be not configure, so return request.getFullGetStr(request):"
						+ fullGetUrl);
			return fullGetUrl;
		}

		String originalHostBeforeReverseProxy = RequestUtil.getOriginalHost(request, originalHostHttpHeader);
		String requestURI = request.getRequestURI();
		String queryString = request.getQueryString();
		// 2、构造完整的
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("http://");
		strBuffer.append(originalHostBeforeReverseProxy);
		if (!StringHelper.isEmpty(requestURI))
			strBuffer.append(requestURI);
		if (!StringHelper.isEmpty(queryString))
			strBuffer.append("?").append(queryString);

		String returnUrl = strBuffer.toString();
		LOG.debug("originalHostBeforeReverseProxy: " + originalHostBeforeReverseProxy + "; requestURI: " + requestURI
				+ "; queryString: " + queryString);

		return returnUrl;

	}

	/**
	 * 返回应用的根URL，包含结尾的<code>/</code>。<br>
	 * 兼容存在反向代理的情况：首先判断是否有x-forwarded-host头，如有则从中得出原始请求的HOST头，再计算.
	 * (详见http://httpd.apache.org/docs/2.2/mod/mod_proxy.html) <b>例子</b>: if request
	 * "http://localhost:8080/app1/dir1/page1.jsp", the method return "http://localhost:8080/app1".
	 * 
	 * @since liushen @ Mar 28, 2011
	 */
	// public static String getOriginalContextUrl(HttpServletRequest request, String originalHostHttpHeader) {
	// String xForwardedHost = null;
	//
	// request.getHeader("X-Forwarded-Host");
	// String scheme = request.getScheme();
	// String host = StringHelper.isEmpty(xForwardedHost) ? request.getHeader("Host") : xForwardedHost;
	//
	// return scheme + "://" + host + getContextPathWithSlash(request);
	// }
	//
	// /**
	// * 获取当前应用的上下文, 总以<code>/</code>结束.
	// *
	// * @param request
	// * @return
	// * @creator liushen @ Apr 16, 2009
	// */
	// public static final String getContextPathWithSlash(HttpServletRequest request) {
	// String contextPath = request.getContextPath();
	// return StringHelper.smartAppendSlashToEnd(contextPath);
	// }

	/**
	 * 获取当前请求的URI + 携带的参数(即reqeust.getQueryString())。
	 * 
	 * @param req
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getCurrentRequestURLWithPara(HttpServletRequest req) {
		StringBuffer fullRequestURL = new StringBuffer("");
		fullRequestURL.append(req.getRequestURI());
		String queryString = req.getQueryString();
		if (false == StringHelper.isEmpty(queryString))
			fullRequestURL.append("?").append(queryString);

		return fullRequestURL.toString();
	}

	/**
	 * 获取当前请求的相对路径(从上下文开始，不包括上下文) + 携带的参数(即reqeust.getQueryString())。
	 * 例如：请求http://127.0.0.1:9080/ids/admin/login.jsp?username=aa&login=dologin<br>
	 * 那么，return /admin/login.jsp?username=aa&login=dologin
	 * 
	 * @param req
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getRelativePathWithPara(HttpServletRequest req) {
		StringBuffer relativePathWithPara = new StringBuffer("");
		relativePathWithPara.append(getRelativePath(req));

		String queryString = req.getQueryString();

		if (false == StringHelper.isEmpty(queryString)) {
			relativePathWithPara.append("?").append(queryString);
		}

		return relativePathWithPara.toString();
	}

	/**
	 * 获取所有名为“checkbox”元素的value，其值不要求为整数
	 * 
	 * @param request
	 * @param param
	 * @return 字符串数组形式返回所有名为“checkbox”元素的value，如果没有此元素，返回一个长度为0的字符串数组
	 * @creator ChengKing
	 */
	public static String[] getCheckBoxValues(HttpServletRequest request, String param) {
		String[] values = request.getParameterValues(param);
		if (values == null) {
			values = new String[0];
		}
		return values;
	}

	/**
	 * 从request中获取requestBody并返回String格式的数据
	 * 
	 * @param request
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getRequestBody(HttpServletRequest request) {
		InputStream isr = null;
		try {
			isr = request.getInputStream();
			return getBody(isr, false);
		} catch (Throwable e) {
			LOG.error("failed to get requestBody from request[" + request + "]", e);
		} finally {
			try {
				isr.close();
			} catch (Throwable e) {
				LOG.error("failed to close isr[" + isr + "]", e);
			}
		}
		return "";
	}

	/**
	 * 根据inputStream构造String格式的信息并返回
	 * 
	 * @param isr
	 * @return
	 * @throws IOException
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getBody(InputStream isr, boolean closeInputStream) {
		BufferedReader br = null;
		try {
			StringBuffer requestBody = new StringBuffer("");
			br = new BufferedReader(new InputStreamReader(isr));
			String lineText = null;
			while ((lineText = br.readLine()) != null) {
				requestBody.append(lineText);
			}
			return requestBody.toString();
		} catch (Throwable e) {
			LOG.error("failed to getRequestBody from inputStream [" + isr + "] .", e);
		} finally {
			try {
				br.close();
				if (closeInputStream)
					isr.close();
			} catch (Throwable e) {
				LOG.error("failed to close br [" + br + "] or isr [" + isr + "].", e);
			}
		}

		return "";
	}

	/**
	 * 获取客户端IP. 在有X-Forwarded-For/X-HOST、X-Client-IP、X-Original-IP等头的情况下，则从这些头中提取原始IP地址。 X-Forwarded-For值的例子(注意顺序)：
	 * X-Forwarded-For: client1, proxy1, proxy2 详见http://en.wikipedia.org/wiki/X-Forwarded-For
	 * 
	 * @param request
	 *            请求信息
	 * @return 请求发起的客户端IP，支持反向代理
	 * @since v1.0
	 * @creator ChengKing
	 * @deprecated {@link RequestUtil#getRemoteAddr}
	 */
	public static final String getClientIP(HttpServletRequest request) {
		return RequestUtil.getRemoteAddr(request);
		// String xForwardedFor = "";
		// // 枚举不同设备下的request的header
		// for (OriginalHostHttpHeader originalHostHttpHeader : OriginalHostHttpHeader.values()) {
		// xForwardedFor = request.getHeader(originalHostHttpHeader.getName());
		// if (!StringHelper.isEmpty(xForwardedFor)) {
		// break;
		// }
		// }
		// String remoteAddr = request.getRemoteAddr();
		// if (xForwardedFor != null && xForwardedFor.length() > 4) {
		// int index = xForwardedFor.indexOf(",");
		// if (index != -1) {
		// remoteAddr = xForwardedFor.substring(0, index);
		// } else {
		// remoteAddr = xForwardedFor;
		// }
		// }
		// return remoteAddr;
	}

	/**
	 * 获取所有的代理IP地址。<br>
	 * 支持通过HTTP Attribute或者Header设置的XForwardedFor-Name来定制反向代理的Host重写
	 * 
	 * @param request
	 * @return 如果有的话返回代理的IP地址链，否则返回空串()
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getXForwardedFor(HttpServletRequest request) {
		String xForwardedFor = "";
		// 根据设置的变量提取X-Forwarded-For/X-Client-IP/X-Originating-IP
		String xForwardedForName = (String) request.getAttribute("XForwardedFor-Name");
		if (StringHelper.isEmpty(xForwardedForName)) {// 如果从Attribute里面取不到的话，则再从Header里面取一次
			xForwardedForName = request.getHeader("XForwardedFor-Name");
		}
		if (!StringHelper.isEmpty(xForwardedForName)) {// 如果取到设置的XForwardedFor-Name，则从Header里面取值
			xForwardedFor = request.getHeader(xForwardedForName);
		}
		return xForwardedFor;
	}

	/**
	 * 获取经过反向代理的原始Host。<br>
	 * 支持通过HTTP Attribute或者Header设置的XForwardedHost-Name来定制反向代理的Host重写
	 * 
	 * @param request
	 * @return 经过反向代理的原始Host
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getXForwardedHost(HttpServletRequest request) {
		String xForwardedHost = "";
		// 根据设置的变量提取X-Forwarded-For/X-Client-IP/X-Originating-IP
		String xForwardedHostName = (String) request.getAttribute("XForwardedHost-Name");
		if (StringHelper.isEmpty(xForwardedHostName)) {// 如果从Attribute里面取不到的话，则再从Header里面再取一次
			xForwardedHostName = request.getHeader("XForwardedHost-Name");
		}
		if (!StringHelper.isEmpty(xForwardedHostName)) {// 如果取到设置的XForwardedHost-Name，则从Header里面取值
			xForwardedHost = request.getHeader(xForwardedHostName);
		}
		return xForwardedHost;
	}

	/**
	 * 获取经过反向代理的ServerName/HTTP Request:Host
	 * 
	 * @param request
	 * @return 经过反向代理的原始Host
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static String getServerName(HttpServletRequest request) {
		String xForwardedHost = getXForwardedHost(request);
		String serverName = "";
		if (false == StringHelper.isEmpty(xForwardedHost)) {
			serverName = StringHelper.split(StringHelper.split(xForwardedHost, ",")[0], ":")[0];
		}
		return StringHelper.isEmpty(serverName) ? request.getServerName() : serverName;
	}

	/**
	 * 
	 * 枚举不同设备下的request的header <BR>
	 * 
	 * @since ChengKing
	 */
	public static enum ForwardedIPHeader {
		X_ORIGINAL_IP(ORIGINAL_REMOTE_IP_ADDRESS_TOKEN_ORIGINALING_IP), X_CLIENT_IP(
				ORIGINAL_REMOTE_IP_ADDRESS_TOKEN_CLIENT_IP), X_ORWARDED_FOR(
				ORIGINAL_REMOTE_IP_ADDRESS_TOKEN_FORWARDED_FOR);

		String name;

		ForwardedIPHeader(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}

	}

	/**
	 * 进行上传操作时，获取当前操作的文件物理路径
	 * 
	 * @param request
	 * @return 上传文件为D:\a.txt时，返回D:\
	 * @since v1.0
	 * @creator zhangshi @ 2012-8-27
	 */
	// public static final String getRealPath(HttpServletRequest request) {
	// // fix TRSIDS-2104:不提供此方法
	// String realPath = request.getSession().getServletContext().getRealPath("/");
	// if (!realPath.endsWith(File.separator)) {
	// realPath = realPath + File.separator;
	// }
	// return realPath;
	// }

	/**
	 * 判断一个请求是否是安全的非钓鱼请求
	 * 
	 * @param request
	 *            HttpServletRequest的实例对象
	 * @param toRedirectUrl
	 *            重定向的URL
	 * @param serverName
	 *            合法重定向的URL中需要包含的URL。<br>
	 *            当传入空值时，尝试获取serverName的顺序如下：<br>
	 *            1)从HttpRequest中获取 x-forwared-host的最后一个值<br>
	 *            2)如果上一步的值为空，从request.getServerName()直接获取。
	 * @return 安全请求返回true，不安全的请求返回false
	 * @since v1.0
	 * @creator ChengKing
	 */
	private static boolean isSecureRedirectUrl(HttpServletRequest request, String toRedirectUrl, String serverName) {
		String host = UrlUtil.getDomainByLevel(toRedirectUrl, 10);

		if (!StringHelper.isEmpty(host) && !StringHelper.isEmpty(serverName)) {
			return host.contains(serverName);
		}

		String xFforwardedHost = request.getHeader("x-forwarded-host");
		String actualServerName = "";
		if (StringHelper.isEmpty(xFforwardedHost)) {
			actualServerName = request.getServerName();
		} else {
			String[] serverNamesByProxy = StringHelper.split(xFforwardedHost, ",");
			// 取最后 一个host
			actualServerName = serverNamesByProxy[serverNamesByProxy.length - 1];
		}

		if (toRedirectUrl.contains(actualServerName)) {
			return true;
		} else
			return false;

	}

	/**
	 * 判断一个请求是否是安全的非钓鱼请求
	 * 
	 * @param request
	 *            HttpServletRequest的实例对象
	 * @param toRedirectUrl
	 *            重定向的URL
	 * @param serverName
	 *            合法重定向的URL中需要包含的URL。<br>
	 *            当传入空值时，尝试获取serverName的顺序如下：<br>
	 *            1)从HttpRequest中获取 x-forwared-host的最后一个值<br>
	 *            2)如果上一步的值为空，从request.getServerName()直接获取。
	 * @return 安全请求返回true，不安全的请求返回false
	 * @since v1.0
	 * @creator ChengKing
	 */
	private static boolean isSecureRedirectUrl(HttpServletRequest request, String toRedirectUrl, String[] serverNames) {

		if (null != serverNames && serverNames.length > 0) {
			for (int i = 0; i < serverNames.length; i++) {
				String serverName = serverNames[i];
				if (isSecureRedirectUrl(request, toRedirectUrl, serverName)) {
					return true;
				}
			}
			return false;
		}

		return isSecureRedirectUrl(request, toRedirectUrl, "");

	}

	/**
	 * 执行一次安全的重定向，从而避免重定向时发生钓鱼的情况。
	 * 
	 * @param request
	 *            HttpServletRequest的实例对象
	 * @param toRedirectUrl
	 *            重定向的URL
	 * @param serverName
	 *            合法重定向的URL中需要包含的URL。<br>
	 *            当传入空值时，尝试获取serverName的顺序如下：<br>
	 *            1)从HttpRequest中获取 x-forwared-host的最后一个值<br>
	 *            2)如果上一步的值为空，从request.getServerName()直接获取。
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static void sendRedirectSecure(HttpServletRequest request, HttpServletResponse response,
			String toRedirectUrl, String[] serverNames, boolean actualRedirect) throws IOException {
		if (!actualRedirect) {
			response.sendRedirect(toRedirectUrl);
			return;
		}
		if (StringHelper.isEmpty(toRedirectUrl)) {
			return;
		}
		boolean isSecureRedirectUrl = isSecureRedirectUrl(request, toRedirectUrl, serverNames);
		if (isSecureRedirectUrl) {
			response.sendRedirect(toRedirectUrl);
		} else {
			StringBuilder redirectInfo = new StringBuilder();
			// redirectInfo.append("<script type=\"text/javascript\">");
			// redirectInfo.append("var num = 5;");
			// redirectInfo.append("function countDown() {");
			// redirectInfo.append("if (num >= 0) {");
			// redirectInfo.append("var str = '';");
			// redirectInfo
			// .append("str += '您访问的可能不是一个安全的地址，系统将在 ' + num + ' 秒钟后自动跳转。<br><br>也可以点击以下网址进行跳转：<br> <a href=\""
			// + StringHelper.filterXSS(toRedirectUrl) + "\">" + StringHelper.filterXSS(toRedirectUrl)
			// + "<\\/a>';");
			// redirectInfo.append("document.getElementById('autoDir').innerHTML = str;");
			// redirectInfo.append("num--;");
			// redirectInfo.append("setTimeout(countDown, 1000);}");
			// redirectInfo.append("else {");
			// redirectInfo.append("window.location.href = \"" + StringHelper.filterXSS(toRedirectUrl) + "\" ");
			// redirectInfo.append("}}");
			// redirectInfo.append("window.onload = countDown;");
			// redirectInfo.append("</script>");
			// redirectInfo.append("<span id=\"autoDir\" name=\"autoDir\"></span>");

			redirectInfo.append("您即将离开当前站点，如果确认以下网址是安全的地址，请点击链接进行跳转：<br> <a href=\""
					+ StringHelper.filterXSS(toRedirectUrl) + "\">" + StringHelper.filterXSS(toRedirectUrl) + "</a>");
			outPrintMessage(response, redirectInfo.toString());

		}
	}

	/**
	 * 
	 * @param response
	 * @param htmlMessage
	 * @throws IOException
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static void outPrintMessage(HttpServletResponse response, String htmlMessage) throws IOException {
		response.setContentType("text/html; charset=GBK");
		PrintWriter pw = response.getWriter();
		StringBuffer sb = new StringBuffer(256);
		sb.append("<HTML><HEAD><TITLE>TRS身份服务器提示信息</TITLE><body>");
		sb.append(htmlMessage);
		pw.println(sb);
		pw.println("</body></html>");
	}
}