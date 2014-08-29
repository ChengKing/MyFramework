package com.common.util;

/**
 * 数据库和JDBC驱动信息. <BR>
 * 
 */
public class DbInfo {

	/**
	 * 配置了JNDI以后的JNDI名称
	 */
	String jndiName;

	/**
	 * 数据库名称
	 */
	String dbProductName;
	/**
	 * 数据库版本
	 */
	String dbProductVersion;
	/**
	 * JDBC驱动名称.
	 */
	String jdbcDriverName;
	/**
	 * JDBC驱动类名.
	 */
	String jdbcDriverClassName;
	/**
	 * JDBC驱动版本.
	 */
	String jdbcDriverVersion;
	/**
	 * JDBC驱动版本详细信息.
	 */
	String jdbcDriverVersionDetail;
	/**
	 * 该驱动实现的JDBC规范版本.
	 */
	String jdbcVersion;

	/**
	 * 连接数据库的URL.
	 * 
	 */
	String jdbcUrl;

	/**
	 * 连接数据库的用户.
	 * 
	 */
	String jdbcUser;

	/**
	 * 使用的hibernate数据库方言
	 * 
	 */
	String dialect;

	/**
	 * Returns the {@link #dbProductVersion}.
	 * 
	 * @return the dbProductVersion.
	 */
	public String getDbProductVersion() {
		return dbProductVersion;
	}

	/**
	 * Returns the {@link #dbProductName}.
	 * 
	 * @return the dbProductName.
	 */
	public String getDbProductName() {
		return dbProductName;
	}

	/**
	 * Returns the {@link #jdbcDriverVersion}.
	 * 
	 * @return the jdbcDriverVersion.
	 */
	public String getJdbcDriverVersion() {
		return jdbcDriverVersion;
	}

	/**
	 * Returns the {@link #jdbcVersion}.
	 * 
	 * @return the jdbcVersion.
	 */
	public String getJdbcVersion() {
		return jdbcVersion;
	}

	/**
	 * Returns the {@link #jdbcDriverClassName}.
	 * 
	 * @return the jdbcDriverClassName.
	 */
	public String getJdbcDriverClassName() {
		return jdbcDriverClassName;
	}

	/**
	 * Returns the {@link #jdbcDriverName}.
	 * 
	 * @return the jdbcDriverName.
	 */
	public String getJdbcDriverName() {
		return jdbcDriverName;
	}

	/**
	 * Returns the {@link #jdbcDriverVersionDetail}.
	 * 
	 * @return the jdbcDriverVersionDetail.
	 */
	public String getJdbcDriverVersionDetail() {
		return jdbcDriverVersionDetail;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(256);
		sb.append(super.toString());
		sb.append("[dbProductName]").append(dbProductName);
		sb.append("[dbProductVersion]").append(dbProductVersion);
		sb.append("[jdbcDriverClassName]").append(jdbcDriverClassName);
		sb.append("[jdbcDriverVersion]").append(jdbcDriverVersion);
		sb.append("[jdbcDriverVersionDetail]").append(jdbcDriverVersionDetail);
		sb.append("[jdbcVersion]").append(jdbcVersion);
		return sb.toString();
	}

	/**
	 * Returns the {@link #jdbcUrl}.
	 * 
	 * @return the jdbcURL.
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	/**
	 * Returns the {@link #jdbcUser}.
	 * 
	 * @return the jdbcUser.
	 */
	public String getJdbcUser() {
		return jdbcUser;
	}

	/**
	 * Returns the {@link #dialect}.
	 * 
	 * @return the dialect.
	 */
	public String getDialect() {
		return dialect;
	}

	/**
	 * Set {@link #dialect}.
	 * 
	 * @param dialect
	 *            The dialect to set.
	 */
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	/**
	 * Returns the {@link #jndiName}.
	 * 
	 * @return the jndiName.
	 */
	public String getJndiName() {
		return jndiName;
	}
}