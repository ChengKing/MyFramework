package com.common.util;

/**
 * 获取运行环境信息的工具类. <BR>
 * 
 * @author ChengKing
 */
public class EnvUtil {

	private static final boolean isIBMJDK;
	private static final String jdkVer;
	private static final boolean jdk13OrHigher;
	private static final boolean jdk14OrHigher;
	private static boolean jdk15OrHigher;
	private static JDKEnv impl;

	/**
	 * 是否是IBM JDK.
	 * 
	 * @return 是否是IBM JDK
	 */
	public static boolean isIBMJDK() {
		return isIBMJDK;
	}

	/**
	 * 是否是JDK1.4以上版本.
	 * 
	 * @return 是JDK1.4以上版本返回true, 否则返回false.
	 */
	public static boolean isJDK14OrHigher() {
		return jdk14OrHigher;
	}

	/**
	 * 是否是JDK1.3以上版本.
	 * 
	 * @return 是JDK1.3以上版本返回true, 否则返回false.
	 */
	public static boolean isJDK13OrHigher() {
		return jdk13OrHigher;
	}

	public static boolean isJDK15OrHigher() {
		return jdk15OrHigher;
	}

	/**
	 * 返回JDK的版本字符串. 该字符串包括主版本和次版本, 不包括维护版本.
	 * 
	 * @return JDK的版本, 所有可能的取值有: <li>"1.0" <li>"1.1" <li>"1.2" <li>"1.3" <li>"1.4" <li>"1.5"
	 */
	public static String getJDKVersion() {
		return jdkVer;
	}

	/**
	 * 获取该JVM的基本信息, 包括版本, 厂商, 虚拟机最大内存(1.4以上版本才支持), 操作系统等信息.
	 */
	public static String getJavaEnvInfo() {
		return impl.getJavaEnvInfo();
	}
	
	/**
	 * 获取执行该JVM进程的用户。
	 * 
	 * @return 执行该JVM进程的用户
	 * @since liushen @ Mar 16, 2010
	 */
	public static String getProcessUser() {
		return System.getProperty("user.name");
	}

	private EnvUtil() {
	}

	static {
		isIBMJDK = vendorIsIBM();
		jdkVer = detectJDKVersion();

		double dJdkVer = Double.parseDouble(jdkVer);
		jdk13OrHigher = (dJdkVer >= 1.3);
		jdk14OrHigher = (dJdkVer >= 1.4);
		jdk15OrHigher = (dJdkVer >= 1.5);

		if (jdk14OrHigher) {
			impl = new JDK14EnvImpl();
		} else if (jdk13OrHigher) {
			impl = new JDK13EnvImpl();
		}
	}

	private static final boolean vendorIsIBM() {
		return System.getProperty("java.vendor").toUpperCase().indexOf("IBM") >= 0;
	}

	private static final String detectJDKVersion() {
		String jdkVer = "1.0";
		try {
			Class.forName("java.lang.Void");
			jdkVer = "1.1";

			Class.forName("java.lang.ThreadLocal");
			jdkVer = "1.2";

			Class.forName("java.lang.StrictMath");
			jdkVer = "1.3";

			Class.forName("java.lang.StackTraceElement");
			jdkVer = "1.4";

			Class.forName("java.lang.Enum");
			jdkVer = "1.5";

			Class.forName("javax.script.Bindings");
			jdkVer = "1.6";
		} catch (ClassNotFoundException e) {
		} catch (Throwable t) {
		}
		return jdkVer;
	}

	/**
	 * 获取该JVM的Classpath信息.
	 */
	public static String getClasspaths() {
		StringBuffer sb = new StringBuffer(256);
		sb.append("javahome=");
		sb.append(System.getProperty("java.home"));
		sb.append(";classpath=");
		sb.append(System.getProperty("java.class.path"));
		return sb.toString();
	}

	/**
	 * 获取该JVM的内存使用信息.
	 */
	public static String getVMMemeoryInfo() {
		return impl.getVMMemeoryInfo();
	}

	/**
	 * 获取操作系统信息
	 * 
	 * @return
	 * @since v4.0
	 * @creator ChengKing @ Jan 8, 2014
	 */
	public static String getOSInfo() {
		try {
			StringBuffer sb = new StringBuffer(256);
			sb.append("OS:");
			sb.append(System.getProperty("os.arch")).append(' ').append(System.getProperty("os.name")).append(' ');
			sb.append(System.getProperty("os.version"));

			return sb.toString();
		} catch (Throwable t) {
			// such as: java.lang.NoClassDefFoundError: java/lang/Runtime
			// (WebappClassLoader: Lifecycle error : CL stopped)
			return "getEnv fail! err=" + t;
		}
	}

}

abstract class JDKEnv {

	abstract String getVMMemeoryInfo();

	String getJavaEnvInfo() {
		try {
			StringBuffer sb = new StringBuffer(256);
			sb.append("Java:");
			sb.append(System.getProperty("java.version")).append(',').append(System.getProperty("java.vm.name"))
					.append(',');
			sb.append(System.getProperty("java.vendor"));

			return sb.toString();
		} catch (Throwable t) {
			return "getEnv fail! err=" + t;
		}
	}
}