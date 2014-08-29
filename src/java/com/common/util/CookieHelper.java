package com.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 存取Cookie的工具类. <BR>
 * 
 */
public class CookieHelper {

	private HttpServletRequest request;

	private HttpServletResponse response;

	@SuppressWarnings("rawtypes")
	private Map cookiesMap;

	private String cookiesHeader;

	private String domain;


	/**
	 * 初始化, 从request获取所有Cookie并保存到内部成员, 供后续高效获取.
	 * 
	 * @param request
	 * @param response
	 */
	public CookieHelper(HttpServletRequest request, HttpServletResponse response) {
		initCookieHelper(request, response);
		// TODO parse and save queryString
	}

	/**
	 * 声明domain的初始化，根据level和原始domain得到cookie的放置domain
	 * 
	 * @param request
	 * @param response
	 * @param domainOfCookie
	 *            完整的domain信息
	 * @param domainLevel
	 *             需要放置cookie的domain级别
	 */
	public CookieHelper(HttpServletRequest request, HttpServletResponse response, String domainOfCookie, int domainLevel) {
		initCookieHelper(request, response);
		this.domain = UrlUtil.getDomainByLevel(domainOfCookie, domainLevel);
	}

	/**
	 * 初始化, 从request获取所有Cookie并保存到内部成员, 供后续高效获取.
	 * 
	 * @param request
	 *            HttpServletRequest 请求
	 * @param response
	 *            HttpServletResponse 请求
	 * @param domainLevel
	 *            cookie domain级别 TODO 详细说明不同值对应的域
	 */
	public CookieHelper(HttpServletRequest request, HttpServletResponse response, int domainLevel) {
		initCookieHelper(request, response);
		this.domain = UrlUtil.getDomainByLevel(request.getServerName(), domainLevel);
	}

	/**
	 * 初始化cookie Helper
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initCookieHelper(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;

		cookiesHeader = request.getHeader("Cookie");
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			cookiesMap = Collections.EMPTY_MAP;
		} else {
			cookiesMap = new HashMap(cookies.length);
			for (int i = 0; i < cookies.length; i++) {
				cookiesMap.put(cookies[i].getName(), cookies[i]);
			}
		}
	}

	public Cookie getCookie(String name) {
		return (Cookie) cookiesMap.get(name);
	}

	public String getValue(String name) {
		Cookie cookie = getCookie(name);
		if (cookie == null) {
			return null;
		}
		return cookie.getValue();
	}

	public void removeCookie(String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setMaxAge(0);
		cookie.setPath(getContextPath(request));
		response.addCookie(cookie);
	}

	public void removeCookie(String name, String path, String domain) {
		Cookie cookie = new Cookie(name, null);
		cookie.setMaxAge(0);
		cookie.setPath(path);
		if (domain != null) {
			cookie.setDomain(domain);
		}
		response.addCookie(cookie);
	}

	/**
	 * 写入一个会话期Cookie.
	 * 
	 * @param name
	 * @param value
	 * @since chengking
	 */
	public void addCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		// cookie.setMaxAge(-1); // not store if setMaxAge(-1) in weblogic9.1, result 【com.trs.ids.guest.blog Received
		// null Thursday, 01-Jan-1970 01:00:00 GMT�?
		cookie.setPath(getContextPath(request));
		if (false == StringHelper.isEmpty(domain)) {
			cookie.setDomain(domain);
		}
		response.addCookie(cookie);
	}

	@SuppressWarnings("rawtypes")
	public String getCookieStrings() {
		StringBuffer sb = new StringBuffer(64 * cookiesMap.size());
		for (Iterator iter = cookiesMap.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			sb.append(name).append('=').append(cookiesMap.get(name)).append("; ");
		}
		return sb.toString();
	}

	/**
	 * Returns the {@link #request}.
	 * 
	 * @return the request.
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Returns the {@link #cookiesHeader}.
	 * 
	 * @return the cookiesHeader.
	 */
	public String getCookiesHeader() {
		return cookiesHeader;
	}

	/**
	 * 根据request获取当前应用的上下文路径
	 * 
	 * @return
	 */
	private String getContextPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (contextPath == null || contextPath.length() == 0) {
			contextPath = "/";
		}
		return contextPath;
	}
}