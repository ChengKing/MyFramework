package com.common.util;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;

import org.apache.log4j.Logger;

/**
 * 支持分页连接查询的LdapHelper <BR>
 * 
 */
public class LdapHelperV2 {

	private static final Logger logger = Logger.getLogger(LdapHelperV2.class);

	/**
	 * 根据给定连接设置返回LDAP连接.
	 * 
	 * @param ldapEnv
	 *            给定连接设置
	 * @return 一个LDAP连接. 如果连接发生异常返回null.
	 * @throws NamingException
	 */
	@SuppressWarnings("rawtypes")
	public static InitialLdapContext openContext(Hashtable ldapEnv, Control[] connCtls) throws NamingException {
		try {
			return new InitialLdapContext(ldapEnv, connCtls);
		} catch (NamingException e) {
			logger.error("fail on connect ldap server!" + ldapEnv, e);
			throw e;
		}
	}

	/**
	 * 关闭一个LDAP连接.
	 * 
	 * @param ctx
	 *            给定的连接Context对象.
	 */
	public static void close(DirContext ctx) {
		if (ctx != null) {
			try {
				ctx.close();
			} catch (NamingException e) {
				logger.error("fail on close ldap context!", e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static NamingEnumeration searchData(Hashtable ldapEnv, String baseDN, Attributes attr) throws Exception {
		LdapContext ctx = null;
		try {
			ctx = openContext(ldapEnv, null);
			return ctx.search(baseDN, attr);
		} catch (NamingException e) {
			logger.error("fail on search!baseDN=" + baseDN, e);
			throw new Exception("搜索LDAP数据时出错!", e);
		} finally {
			close(ctx);
		}
	}

	/**
	 * 
	 * @param ldapEnv
	 * @param guidFormated
	 *            如AD域机构ID：<GUID=284e56d3-2180-4e20-9b39-72b1717f970>
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Attributes searchAttributes(Hashtable ldapEnv, String guidFormated) throws Exception {
		LdapContext ctx = null;
		try {
			ctx = openContext(ldapEnv, null);

			// Specify the attributes to return
			return ctx.getAttributes(guidFormated, null);
		} catch (NamingException e) {
			logger.error("fail on search attributes! guidFormated=" + guidFormated, e);
			throw new Exception("搜索LDAP数据时出错!", e);
		} finally {
			close(ctx);
		}
	}

	@SuppressWarnings("rawtypes")
	public static NamingEnumeration searchData(Hashtable ldapEnv, String baseDN, String filter,
			SearchControls constraints, int maxResult) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("dn=" + baseDN + ", filter=" + filter + ", maxResult=" + maxResult);
		}
		LdapContext ctx = null;

		// 初始化控件集合
		Control[] connCtls = null;
		try {
			connCtls = new Control[] { new PagedResultsControl(maxResult, Control.CRITICAL) };
		} catch (IOException e) {
			logger.error("error while new PagedResultsControl, maxResult:" + maxResult + ", error:" + e);
		}

		try {
			ctx = openContext(ldapEnv, connCtls);
			NamingEnumeration answers = ctx.search(baseDN, filter, constraints);
			return answers;
		} catch (NamingException e) {
			logger.error("fail on search!baseDN=" + baseDN + ", filter=" + filter + ", maxResult=" + maxResult, e);
			throw new Exception("搜索LDAP数据时出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}

	@SuppressWarnings("rawtypes")
	public static NamingEnumeration searchData(Hashtable ldapEnv, String baseDN, String filter,
			SearchControls constraints, Control[] connCtls) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("dn=" + baseDN + ", filter=" + filter);
		}
		LdapContext ctx = null;
		try {
			ctx = openContext(ldapEnv, null);
			ctx.setRequestControls(connCtls);
			NamingEnumeration answers = ctx.search(baseDN, filter, constraints);
			return answers;
		} catch (NamingException e) {
			logger.error("fail on search!baseDN=" + baseDN + ", filter=" + filter, e);
			throw new Exception("搜索LDAP数据时出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}

	@SuppressWarnings("rawtypes")
	public static NamingEnumeration searchData(Hashtable ldapEnv, String baseDN, String filter,
			SearchControls constraints) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("dn=" + baseDN + ", filter=" + filter);
		}
		LdapContext ctx = null;
		try {
			ctx = openContext(ldapEnv, null);
			NamingEnumeration answers = ctx.search(baseDN, filter, constraints);
			return answers;
		} catch (NamingException e) {
			logger.error("fail on search!baseDN=" + baseDN + ", filter=" + filter, e);
			throw new Exception("搜索LDAP数据时出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}

	/**
	 * 测试给定的用户DN和密码是否能连接到给定的LDAP URL. 使用simple bind方式进行认证.
	 * 
	 * @param ldapUrl
	 *            给定的LDAP URL
	 * @param userDN
	 *            给定的用户DN名称
	 * @param pwd
	 *            给定的用户密码
	 * @param ldapDriver
	 *            给定的ldap驱动类(Provider class)的类名
	 * @return 如果能成功连接, 返回true; 否则返回false.
	 * @throws NamingException
	 *             测试时将异常抛出
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean tryConnect(String ldapUrl, String userDN, String pwd) throws NamingException {
		Hashtable env = new Hashtable(5);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, userDN);
		env.put(Context.SECURITY_CREDENTIALS, pwd);
		if (logger.isDebugEnabled()) {
			logger.debug("[ldap connection setting]: " + env);
		}
		boolean result = false;
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			result = true;
		} catch (AuthenticationException e) {
			logger.warn("ldap user password incorrect! ldapUrl=" + ldapUrl + ",userdn=" + userDN, e);
			throw e;
		} catch (NamingException e) {
			logger.error("ldap naming error! ldapUrl=" + ldapUrl + ",userdn=" + userDN, e);
			throw e;
		} finally {
			close(ctx);
		}
		return result;
	}

	/**
	 * 根据指定的属性名查询已存在的DN
	 * 
	 * @param ldapEnv
	 *            LDAP上下文
	 * @param attrs
	 *            属性集合
	 * @param baseDN
	 *            根DN
	 * @param uniqueAttrName
	 *            唯一属性名，如: userPrincipalName
	 * @param uniqueAttrValue
	 *            唯一属性值
	 * @return 已存在的DN，如不存在返回""
	 * @throws Exception
	 * @creator ChengKing
	 */
	@SuppressWarnings("rawtypes")
	public static String searchExistUserDN(Hashtable ldapEnv, String baseDN, String uniqueAttrName,
			String uniqueAttrValue) throws Exception {
		LdapContext ctx = null;
		try {
			ctx = openContext(ldapEnv, null);
			if (StringHelper.isEmpty(uniqueAttrValue)) {
				if (logger.isDebugEnabled()) {
					logger.debug("get uniqueAttrValue[" + uniqueAttrValue + "] is empty by uniqueAttrName["
							+ uniqueAttrName + "]");
				}
				return "";
			}
			String ldapFilter = "(&(objectclass=person)(" + uniqueAttrName + "=" + uniqueAttrValue + "))";
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration<SearchResult> results = ctx.search(baseDN, ldapFilter, constraints);
			if (results != null && results.hasMoreElements()) {
				SearchResult sr = results.next();
				String oldUserDN = sr.getNameInNamespace();
				if (logger.isDebugEnabled()) {
					logger.debug("get oldUserDN[" + oldUserDN + "] by uniqueAttrName[" + uniqueAttrValue + "]");
				}
				return oldUserDN;
			}
		} catch (NamingException e) {
			logger.error("run error while search oldDN by uniqueAttrName[" + uniqueAttrValue + "]", e);
			return "";
		} finally {
			close(ctx);
		}
		return "";
	}

	/**
	 * 重命名LDAP对象
	 * 
	 * @param ldapEnv
	 *            LDAP上下文
	 * @param oldDN
	 *            历史DN
	 * @param newDN
	 *            新DN
	 * @throws Exception
	 * @creator ChengKing
	 */
	@SuppressWarnings("rawtypes")
	public static void rename(Hashtable ldapEnv, String oldDN, String newDN) throws Exception {
		LdapContext ctx = null;
		try {
			ctx = openContext(ldapEnv, null);
			ctx.rename(oldDN, newDN);
		} catch (NamingException e) {
			logger.error("run error while rename by oldDN[" + oldDN + "], newDN[" + newDN + "].", e);
			throw new Exception("保存新数据到LDAP出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}

	/**
	 * 保存对象属性集合
	 * 
	 * @param ldapEnv
	 *            上下文连接环境
	 * @param dn
	 *            DN
	 * @param attrs
	 *            属性集合
	 * @throws Exception
	 * @creator ChengKing
	 */
	@SuppressWarnings("rawtypes")
	public static void saveNewData(Hashtable ldapEnv, String dn, Attributes attrs) throws Exception {
		LdapContext ctx = null;
		try {
			ctx = openContext(ldapEnv, null);
			ctx.createSubcontext(dn, attrs); 
		} catch (NamingException e) {
			logger.error("add err! dn=" + dn, e);
			logger.error("object:" + attrs);
			throw new Exception("保存新数据到LDAP出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}
}