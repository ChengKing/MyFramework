package com.common.util;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;

/**
 * 处理LDAP的工具方法类. <BR>
 * 
 */
public class LdapHelper {

	private static final Logger logger = Logger.getLogger(LdapHelper.class);

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

	/**
	 * 测试给定的用户DN和密码是否能连接到给定的LDAP URL.
	 * 
	 * @param ldapUrl
	 *            给定的LDAP URL
	 * @param userDN
	 *            给定的用户DN名称
	 * @param pwd
	 *            给定的用户密码
	 * @return 如果能成功连接, 返回true; 否则返回false.
	 * @see #tryConnect(String, String, String, String)
	 */
	public static boolean tryConnect(String ldapUrl, String userDN, String pwd) {
		return tryConnect(ldapUrl, userDN, pwd, "com.sun.jndi.ldap.LdapCtxFactory");
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
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean tryConnect(String ldapUrl, String userDN, String pwd, String ldapDriver) {
		Hashtable env = new Hashtable(5);
		env.put(Context.INITIAL_CONTEXT_FACTORY, ldapDriver);
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, userDN);
		env.put(Context.SECURITY_CREDENTIALS, pwd);
		// env.put(Context.)
		if (logger.isDebugEnabled()) {
			logger.debug("[ldap connection setting]: " + env);
		}
		boolean result = false;
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			result = true;
		} catch (AuthenticationException e) {
			logger.warn("ldap user password incorrect! ldapUrl=" + ldapUrl + ",driver=" + ldapDriver + ",userdn="
					+ userDN, e);
		} catch (NamingException e) {
			logger.error("ldap naming error! ldapUrl=" + ldapUrl + ",driver=" + ldapDriver + ",userdn=" + userDN, e);
		} finally {
			close(ctx);
		}
		return result;
	}

	public static String getAttrValue(Attributes attrs, String name) throws NamingException {
		return getAttrValue(attrs, name, "");
	}

	public static String getAttrValue(Attributes attrs, String name, String defaultValue) throws NamingException {
		Attribute attrib = attrs.get(name);
		if (attrib == null) {
			return defaultValue;
		}
		Object attributeObj = attrib.get();
		String attributeStr = null;
		if (attributeObj != null && attributeObj.getClass().isArray()) {
			byte[] attributeByteArray = (byte[]) attributeObj;
			attributeStr = new String(attributeByteArray);
			return attributeStr;
		}
		if (attributeObj != null && attributeObj instanceof String) {
			attributeStr = (String) attributeObj;
			return attributeStr;
		}
		return (attributeObj == null) ? defaultValue : (String) attributeObj;
	}

	public static boolean getAttrValueAsBool(Attributes attrs, String name) throws NamingException {
		return getAttrValueAsBool(attrs, name, false);
	}

	public static boolean getAttrValueAsBool(Attributes attrs, String name, boolean defaultValue)
			throws NamingException {
		Attribute attrib = attrs.get(name);
		if (attrib == null) {
			return defaultValue;
		}
		Object value = attrib.get();
		if (value instanceof String) {
			return Boolean.valueOf((String) value).booleanValue();
		}
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		return defaultValue;
	}

	/**
	 * 获取给定LDAP属性集中, 给定属性名的整数值. 如果给定属性名不存在或给定属性名的取值无法转化为整数, 则返回0.
	 * 
	 * @param attrs
	 *            给定的LDAP属性集
	 * @param string
	 *            给定属性名
	 * @return 整数值.
	 * @throws NamingException
	 */
	public static int getAttrValueAsInt(Attributes attrs, String string) throws NamingException {
		return getAttrValueAsInt(attrs, string, 0);
	}

	/**
	 * 获取给定LDAP属性集中, 给定属性名的整数值. 如果给定属性名不存在或给定属性名的取值无法转化为整数, 则返回默认的整数值.
	 * 
	 * @param attrs
	 *            给定的LDAP属性集
	 * @param name
	 *            给定属性名
	 * @param defaultValue
	 *            默认的整数值
	 * @return 整数值.
	 * @throws NamingException
	 */
	public static int getAttrValueAsInt(Attributes attrs, String name, int defaultValue) throws NamingException {
		Attribute attrib = attrs.get(name);
		if (attrib == null) {
			return defaultValue;
		}
		Object value = attrib.get();
		if (value instanceof String) {
			return Integer.parseInt(((String) value).trim());
		}
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		return defaultValue;
	}

	public static String getAttrInfo(Attribute attr) {
		StringBuffer sb = new StringBuffer(256);
		sb.append("Attribute:");
		sb.append(attr.getID());
		try {
			sb.append(" schema=").append(attr.getAttributeDefinition());
			sb.append(" syntax=").append(attr.getAttributeSyntaxDefinition());
		} catch (NamingException e) {
			sb.append(" [getSchemaAndSyntaxFail]").append(e);
		}
		sb.append(" ordered=").append(attr.isOrdered());
		sb.append(" size=").append(attr.size());
		logger.info(sb);
		return sb.toString();
	}

	/**
	 * 根据给定的属性名\取值\BaseDN来创建一个LDAP的DN名称(相当于索引作用).
	 * 
	 * @deprecated replace by {@link #creatDNByParentGroupDN(String, String, String)}
	 * @param attr
	 *            给定的属性名
	 * @param value
	 *            给定的属性取值
	 * @param baseDN
	 *            给定的BaseDN名称
	 * @return 所创建的LDAP的DN名称
	 */
	public static String createDN(String attr, String value, String baseDN) {
		StringBuffer sb = new StringBuffer(64);
		sb.append(attr);
		sb.append('=');
		sb.append(value);
		sb.append(',');
		sb.append(baseDN);
		return sb.toString();
	}

	/**
	 * 根据给定的属性名\取值\父组织DN来创建一个LDAP的DN名称(相当于索引作用).
	 * 
	 * @param attr
	 *            给定的属性名
	 * @param value
	 *            给定的属性取值
	 * @param parentGroupDN
	 *            给定的组织的父组织DN名称/给定用户的所属组织DN名称
	 * @return
	 * @creator ChengKing
	 */
	public static String creatDNByParentGroupDN(String attr, String value, String parentGroupDN) {
		StringBuffer sb = new StringBuffer(64);
		sb.append(attr);
		sb.append('=');
		sb.append(value);
		sb.append(',');
		sb.append(parentGroupDN);
		return sb.toString();
	}

	/**
	 * 根据给定连接设置返回LDAP连接.
	 * 
	 * @param ldapEnv
	 *            给定连接设置
	 * @return 一个LDAP连接. 如果连接发生异常返回null.
	 * @throws NamingException
	 */
	@SuppressWarnings("rawtypes")
	public static DirContext openContext(Hashtable ldapEnv) throws NamingException {
		try {
			return new InitialDirContext(ldapEnv);
		} catch (NamingException e) {
			logger.error("fail on connect ldap server!" + ldapEnv, e);
			throw e;
		}
	}

	// 该方法只能查找baseDN下的数据
	// TODO 该方法不能查询到EveryOne等其它DN下的数据？不可用？
	@SuppressWarnings("rawtypes")
	public static NamingEnumeration searchData(Hashtable ldapEnv, String baseDN, Attributes attr) throws Exception {
		DirContext ctx = null;
		try {
			ctx = openContext(ldapEnv);
			return ctx.search(baseDN, attr);
		} catch (NamingException e) {
			logger.error("fail on search!baseDN=" + baseDN, e);
			throw new Exception("搜索LDAP数据时出错!", e);
		} finally {
			close(ctx);
		}
	}

	/**
	 * 根据条件进行搜索.<BR>
	 * <BR>
	 * <b>LDAP的Filter示例:</b> <li>(objectclass=*) <li>(objectclass=idsGroup) <li>
	 * (&(objectclass=idsGroup)(groupLevel=2)) <li>
	 * (&(objectclass=idsUser)(idsUN=*)) <BR>
	 * 
	 * @param ldapEnv
	 *            建立LDAP连接的环境参数.
	 * @param baseDN
	 *            搜索LDAP数据的BaseDN. JDK文档描述: "the name of the context or object to search"
	 * @param filter
	 *            LDAP搜索的Filter. JDK文档描述: "the filter expression to use for the search; may not be null"
	 * @param scope
	 *            搜索范围, 取值只能是接口 <code>javax.naming.directory.SearchControls</code> 中的常量OBJECT_SCOPE, ONELEVEL_SCOPE,
	 *            SUBTREE_SCOPE中之一.
	 * @param limit
	 *            搜索返回的最大条数. 0表示无限制.
	 * @param returnAttrs
	 *            返回的属性集. JDK文档描述:
	 *            "null indicates that all attributes will be returned. An empty array indicates no attributes are returned."
	 *            如果希望搜索到modifytimestamp等Operational Attributes, 这些属性（LDAP Server自己负责维护的, 用户无法修改的）必须要在参数attrs中指定, LDAP
	 *            Server才会返回给客户端. 如果用户希望返回全部User Attributes的同时, 返回指定的Operational Attributes, 可使用便捷方式: 只需传入
	 *            <code>new String[]{"*", "createtimestamp"}</code> 这样的参数即可; "*"号即代表了全部的User Attributes.
	 *            这一点在Sun关于JNDI的文档中并未说明, LDAP协议对此的说明在 http://www.ietf.org/rfc/rfc2251.txt 第28页.
	 * @return 返回的数据组成的JNDI NamingEnumeration.
	 * @throws Exception
	 *             搜索LDAP数据出错时
	 */
	@SuppressWarnings("rawtypes")
	public static NamingEnumeration searchData(Hashtable ldapEnv, String baseDN, String filter, int scope, int limit,
			String[] returnAttrs) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("dn=" + baseDN + ", filter=" + filter + ", scope=" + scope + ", limit=" + limit
					+ ", returnAttrs=" + StringHelper.toString(returnAttrs, true));
		}
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(scope);
		constraints.setCountLimit(limit);
		constraints.setReturningAttributes(returnAttrs);
		DirContext ctx = null;
		try {
			ctx = openContext(ldapEnv);
			NamingEnumeration answers = ctx.search(baseDN, filter, constraints);
			return answers;
		} catch (NamingException e) {
			logger.error("fail on search!baseDN=" + baseDN + ", filter=" + filter + ", limit=" + limit, e);
			throw new Exception("搜索LDAP数据时出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}

	/**
	 * @param ldapEnv
	 * @param dn
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void removeData(Hashtable ldapEnv, String dn) throws Exception {
		DirContext ctx = null;
		try {
			ctx = openContext(ldapEnv);
			ctx.destroySubcontext(dn);
		} catch (NamingException e) {
			logger.error("error to remove! dn=" + dn, e);
			throw new Exception("删除LDAP数据出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void renameDN(Hashtable ldapEnv, String oldDN, String newDN) throws Exception {
		if (newDN == null || newDN.trim().length() == 0) {
			return;
		}
		if (newDN.equals(oldDN)) {
			return;
		}
		DirContext ctx = null;
		try {
			ctx = openContext(ldapEnv);
			ctx.rename(oldDN, newDN);
		} catch (NamingException e) {
			logger.error("fail to rename entry! oldDN=" + oldDN + ", newDN=" + newDN, e);
			throw new Exception("LDAP实体改名出错!", e);
		} finally {
			close(ctx);
		}
	}

	/**
	 * 保存新添加的数据.
	 * 
	 * @param ldapEnv
	 * @param dn
	 * @param attrs
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void saveNewData(Hashtable ldapEnv, String dn, Attributes attrs) throws Exception {
		DirContext ctx = null;
		try {
			ctx = openContext(ldapEnv);
			ctx.createSubcontext(dn, attrs);
		} catch (NamingException e) {
			logger.error("add err! dn=" + dn, e);
			logger.error("object:" + attrs);
			throw new Exception("保存新数据到LDAP出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void update(Hashtable ldapEnv, String dn, Attributes attrs) throws Exception {
		DirContext ctx = null;
		try {
			ctx = openContext(ldapEnv);
			ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			logger.error("update err! dn=" + dn, e);
			logger.error("attrs:" + attrs);
			throw new Exception("更新数据到LDAP出错!" + e.getMessage(), e);
		} finally {
			close(ctx);
		}
	}

	/**
	 * 
	 * @param attrs
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String attrs2String(Attributes attrs) {
		NamingEnumeration attrIds = attrs.getIDs();
		StringBuffer sb = new StringBuffer(512);
		try {
			for (; attrIds.hasMore();) {
				String attrId = (String) attrIds.next();
				sb.append(attrId).append('=').append(attrs.get(attrId)).append(';');
			}
			sb.append(". ").append(attrs.size()).append(" attributes.");
		} catch (Exception e) {
			sb.append("failed!").append(e.getMessage());
		}
		return sb.toString();
	}

	/**
	 * @param attrs
	 * @param string
	 * @return
	 * @since v3.5
	 * @creator shixin @ 2010-9-29
	 */
	public static String getObjectGUIDAttrValueOfAD(Attributes attrs) {
		byte[] GUID = null;
		try {
			GUID = (byte[]) attrs.get("objectGUID").get();
		} catch (NamingException e) {
			logger.error("get objectGUID attribute value error:" + e);
			e.printStackTrace();
		} catch (Exception ex) {
			logger.error("get objectGUID attribute value error:" + ex);
			ex.printStackTrace();
		}
		String strGUID = "";
		String byteGUID = "";
		// Convert the GUID into string using the byte format
		for (int c = 0; c < GUID.length; c++) {
			byteGUID = byteGUID + "\\" + AddLeadingZero(GUID[c] & 0xFF);
		}
		// convert the GUID into string format
		strGUID = "{";
		strGUID = strGUID + AddLeadingZero(GUID[3] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[2] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[1] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[0] & 0xFF);
		strGUID = strGUID + "-";
		strGUID = strGUID + AddLeadingZero(GUID[5] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[4] & 0xFF);
		strGUID = strGUID + "-";
		strGUID = strGUID + AddLeadingZero(GUID[7] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[6] & 0xFF);
		strGUID = strGUID + "-";
		strGUID = strGUID + AddLeadingZero(GUID[8] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[9] & 0xFF);
		strGUID = strGUID + "-";
		strGUID = strGUID + AddLeadingZero(GUID[10] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[11] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[12] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[13] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[14] & 0xFF);
		strGUID = strGUID + AddLeadingZero(GUID[15] & 0xFF);
		strGUID = strGUID + "}";
		// System.out.println("GUID (String format): " + strGUID);
		// System.out.println("GUID (Byte format): " + byteGUID);
		return strGUID;
	}

	static String AddLeadingZero(int k) {
		return (k <= 0xF) ? "0" + Integer.toHexString(k) : Integer.toHexString(k);
	}

	public static boolean isExist(DirContext ctx, String dn) {
		boolean isExist = false;
		try {
			ctx.getAttributes(dn);
			isExist = true;
		} catch (NamingException e) {
			logger.warn("dn=[" + dn + "] is not exist");
		}
		return isExist;
	}
}