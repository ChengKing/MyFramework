package com.common.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * 关系数据库连接的工具类 <BR>
 * 
 */
public class DbUtil {

	private static final Logger LOG = Logger.getLogger(DbUtil.class);

	public static String H2_DRIVER = "org.h2.Driver";

	/**
	 * 检测给定的配置参数的准确性(能否取得一个DB连接).<BR>
	 * 调用方需负责关闭此方法返回的<code>Connection</code>对象.
	 * 
	 * @param driver
	 *            JDBC驱动类名
	 * @param url
	 *            数据库URL
	 * @param user
	 *            连接用户
	 * @param pwd
	 *            连接密码
	 * @return 数据库连接
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 *             取不到数据库连接时.
	 */
	public static Connection tryConnect(String driver, String url, String user, String pwd)
			throws ClassNotFoundException, SQLException {
		if (driver == null || driver.trim().length() == 0) {
			throw new IllegalArgumentException("the jdbc driver class is empty!");
		}
		if (url == null || url.trim().length() == 0) {
			throw new IllegalArgumentException("the jdbc url is empty!");
		}

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			LOG.error("load JDBC Driver fail! driverClass: " + driver, e);
			throw e;
		}

		try {
			Connection conn = DriverManager.getConnection(url, user, pwd);
			return conn;
		} catch (SQLException e) {
			LOG.error("DriverManager.getConnection(" + url + ", " + user + ", " + pwd + ") fail! jdbcDriver=" + driver,
					e);
			throw new SQLException("connect db fail! (url, user, driver)=(" + url + ", " + user + ", " + driver
					+ "), err: " + e);
		}
	}

	/**
	 * 获取数据库驱动和数据库的信息.
	 * 
	 * @param conn
	 *            有效的数据库连接对象. 如果为null, 则无法获取信息.
	 * @param driver
	 *            所用数据库驱动类
	 * @return 包含数据库驱动和数据库的信息的字符串.
	 */
	public static String getDbAndDriverInfoForUI(Connection conn, String driver) {
		try {
			DatabaseMetaData dbMeta = conn.getMetaData();
			StringBuffer sb = new StringBuffer(256);
			sb.append("所用数据库: ").append(dbMeta.getDatabaseProductName()).append('\n');
			sb.append("所用数据库版本: ").append(dbMeta.getDatabaseProductVersion()).append('\n');
			sb.append("所用JDBC驱动名称: ").append(dbMeta.getDriverName()).append('\n');
			sb.append("驱动版本: ").append(dbMeta.getDriverMajorVersion()).append('.')
					.append(dbMeta.getDriverMinorVersion()).append('\n');
			String driverVer;
			try {
				driverVer = dbMeta.getDriverVersion();
			} catch (Exception e) {
				driverVer = "无法取得.";
			}
			sb.append("驱动版本详细信息: ").append(driverVer).append('\n');

			sb.append("所用JDBC驱动类: ").append(driver).append('\n');
			String jdbcVer;
			try {
				jdbcVer = dbMeta.getJDBCMajorVersion() + "." + dbMeta.getJDBCMinorVersion();
			} catch (Throwable t) {
				jdbcVer = "1.x或2.x";
			}
			sb.append("实现的JDBC版本: ").append(jdbcVer).append('\n');
			return sb.toString();
		} catch (Exception e) {
			return "无法取得详细信息." + e.getMessage();
		}

	}
	
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				LOG.error("fail to close db connection!", e);
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				LOG.error("fail to close resultset!", e);
			}
		}
	}

	public static void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				LOG.error("fail to close " + stmt.getClass().getName(), e);
			}
		}
	}

	/**
	 * 获取数据库驱动和数据库的信息.
	 * 
	 * @param conn
	 *            有效的数据库连接对象. 如果为null, 则无法获取信息.
	 * @param driver
	 *            所用数据库驱动类
	 */
	public static DbInfo getDbAndDriverInfo(Connection conn, Configuration cfg) {
		DbInfo info = new DbInfo();
		info.jdbcDriverClassName = cfg.getProperty(Environment.DRIVER);
		try {
			DatabaseMetaData dbMeta = conn.getMetaData();
			info.dbProductName = dbMeta.getDatabaseProductName();
			info.dbProductVersion = dbMeta.getDatabaseProductVersion();
			info.dbProductName = dbMeta.getDatabaseProductName();
			info.jdbcDriverName = dbMeta.getDriverName();
			info.jdbcDriverVersion = dbMeta.getDriverMajorVersion() + "." + dbMeta.getDriverMinorVersion();
			try {
				info.jdbcDriverVersionDetail = dbMeta.getDriverVersion();
			} catch (Exception e) {
				info.jdbcDriverVersionDetail = e.toString();
			}
			try {
				info.jdbcVersion = dbMeta.getJDBCMajorVersion() + "." + dbMeta.getJDBCMinorVersion();
			} catch (Throwable t) {
				info.jdbcVersion = t.toString();
			}
			info.jdbcUrl = dbMeta.getURL();
			info.jdbcUser = dbMeta.getUserName();
			info.jndiName = cfg.getProperty(Environment.DATASOURCE);
			info.setDialect(cfg.getProperty(Environment.DIALECT));
		} catch (Exception e) {
			LOG.error("fail on getDbAndDriverInfo!", e);
		}
		return info;
	}

	/**
	 * 判断当前数据驱动是否为H2类型
	 * 
	 * @param driver
	 * @return
	 */
	public static boolean isH2Driver(String driver) {
		if (H2_DRIVER.equalsIgnoreCase(driver)) {
			return true;
		}
		return false;
	}

	/**
	 * 根据数据库类型，获取驱动连接信息
	 * 
	 * @param rdbType
	 *            数据库类型 该参数的取值是大小写无关
	 * @return 驱动连接信息
	 * @creator chengking
	 */
	public static String generateDBDriver(String dbType) {
		if (dbType == null) {
			LOG.error("get dbType is null!");
			return "";
		}
		dbType = dbType.toLowerCase();
		if ("sqlserver".equals(dbType) || "mssql".equals(dbType)) {
			return "net.sourceforge.jtds.jdbc.Driver";
		}
		if ("oracle".equals(dbType) || "oraclerac".equals(dbType)) {
			return "oracle.jdbc.driver.OracleDriver";
		}
		if ("mysql".equals(dbType)) {
			return "com.mysql.jdbc.Driver";
		}

		if ("h2".equals(dbType)) {
			return "org.h2.Driver";
		}

		return "";
	}

	/**
	 * 获取数据源
	 * 
	 * @param dataSourceName
	 *            数据源名称
	 * @return
	 * @throws Exception
	 * @creator chengking
	 */
	public static DataSource getDataSource(String dataSourceName) throws Exception {
		if (StringHelper.isEmpty(dataSourceName)) {
			return null;
		}
		try {
			Context ic = new InitialContext();
			DataSource source = (DataSource) ic.lookup(dataSourceName);
			return source;
		} catch (NamingException e) {
			LOG.error("try build dataSource failed with:" + e.getMessage(), e);
			throw new Exception("构造统计报表数据源产生异常!", e);
		}
	}

	/**
	 * 根据数据库类型判断是否是oracle
	 * 
	 * @param rdbType
	 *            数据库类型
	 * @return 是则返回true
	 * @creator chengking
	 */
	public static boolean isOracle(String rdbType) {
		if (rdbType == null) {
			return false;
		}
		return "oracle".equals(rdbType);
	}

	/**
	 * 根据数据库类型判断是否是H2
	 * 
	 * @param rdbType
	 *            数据库类型
	 * @return 是则返回true
	 * @creator chengking
	 */
	public static boolean isH2(String rdbType) {
		if (rdbType == null) {
			return false;
		}
		return "h2".equals(rdbType);
	}

	/**
	 * 是否是Oracle RAC集群
	 * 
	 * @param rdbType
	 *            数据库类型
	 * @return 是则返回true
	 * @creator chengking
	 */
	public static boolean isOracleRac(String rdbType) {
		if (rdbType == null) {
			return false;
		}
		return "oraclerac".equals(rdbType);
	}

	/**
	 * 
	 * @param dbType
	 *            数据库类型. 该参数的取值是大小写无关的.
	 *            <ul>
	 *            取值说明:
	 *            <li>"mysql"表示MYSQLServer(Ver: 5.0+)
	 *            <li>"sqlserver或者mssql"表示SQLServer(Ver: 2005+)
	 *            <li>"oracle"表示Oracle(Ver: 10g+)
	 *            </ul>
	 * @param dbHost
	 *            如果为null则取默认值"localhost"
	 * @param dbPort
	 *            如果为null则取各数据库的默认端口, 如sqlserver是1433, oracle是1521.
	 * @param dbName
	 *            数据库名称
	 * @return jdbc的URL
	 */
	public static String generateJdbcUrl(String dbType, String dbHost, String dbPort, String dbName) {
		if (dbType == null) {
			LOG.error("get dbType is null!");
			return "";
		}
		dbType = dbType.toLowerCase();
		if ("sqlserver".equals(dbType) || "mssql".equals(dbType)) {
			return buildSQLServerJdbcUrl(dbHost, dbPort, dbName);
		}
		if ("oracle".equals(dbType)) {
			return buildOracleThinJdbcUrl(dbHost, dbPort, dbName);
		}
		if ("mysql".equals(dbType)) {
			return buildMySQLJdbcUrl(dbHost, dbPort, dbName);
		}
		if ("h2".equals(dbType)) {
			return buildH2JdbcUrl(dbName);
		}

		LOG.warn("dbType[" + dbType + "] not support, so return!");
		return "";
	}

	/**
	 * 构造H2的JDBC URL
	 * 
	 * @param dbName
	 *            数据库名称
	 * @return JDBC连接串
	 * @creator chengking
	 */
	private static String buildH2JdbcUrl(String dbName) {
		// jdbc:h2:mem:idsdb;DB_CLOSE_ON_EXIT=FALSE
		StringBuffer sb = new StringBuffer();
		sb.append("jdbc:");
		sb.append("h2:mem:");
		sb.append(dbName);
		sb.append(";DB_CLOSE_ON_EXIT=FALSE");
		return sb.toString();
	}

	/**
	 * 构造MYSQL的JDBC URL
	 * 
	 * @param dbHost
	 *            数据库IP
	 * @param dbPort
	 *            端口
	 * @param dbName
	 *            数据库名称
	 * @return JDBC连接串
	 * @creator chengking
	 */
	private static String buildMySQLJdbcUrl(String dbHost, String dbPort, String dbName) {
		StringBuffer sb = new StringBuffer();
		sb.append("jdbc:");
		sb.append("mysql://");
		sb.append((dbHost == null) ? "localhost" : dbHost);
		sb.append(':');
		sb.append((dbPort == null) ? "3306" : dbPort);
		sb.append('/').append(dbName);
		sb.append("?characterEncoding=UTF-8");
		return sb.toString();
	}

	/**
	 * 构造Oracle的JDBC URL
	 * 
	 * @param dbHost
	 *            数据库IP
	 * @param dbPort
	 *            端口
	 * @param dbName
	 *            数据库名称
	 * @return JDBC连接串
	 * @creator chengking
	 */
	private static String buildOracleThinJdbcUrl(String dbHost, String dbPort, String dbName) {
		StringBuffer sb = new StringBuffer();
		sb.append("jdbc:");
		sb.append("oracle:thin:@");
		sb.append((dbHost == null) ? "localhost" : dbHost);
		sb.append(':');
		sb.append((dbPort == null) ? "1521" : dbPort);
		sb.append(':').append(dbName);
		return sb.toString();
	}

	/**
	 * 构造SQLServer的JDBC URL
	 * 
	 * @param dbHost
	 *            数据库IP
	 * @param dbPort
	 *            端口
	 * @param dbName
	 *            数据库名称
	 * @return JDBC连接串
	 * @creator chengking
	 */
	private static String buildSQLServerJdbcUrl(String dbHost, String dbPort, String dbName) {
		StringBuffer sb = new StringBuffer();
		sb.append("jdbc:");
		sb.append("jtds:sqlserver://");
		sb.append((dbHost == null) ? "localhost" : dbHost);
		sb.append(':');
		sb.append((dbPort == null) ? "1433" : dbPort);
		sb.append('/').append(dbName);
		return sb.toString();
	}
}