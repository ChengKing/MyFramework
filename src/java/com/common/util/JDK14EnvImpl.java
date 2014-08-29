package com.common.util;

/**
 * @author ChengKing
 */
class JDK14EnvImpl extends JDKEnv {

	String getVMMemeoryInfo() {
		Runtime runtime = Runtime.getRuntime();
		StringBuffer sb = new StringBuffer(64);
		sb.append("user.dir=").append(System.getProperty("user.dir")).append(';');
		sb.append("java.home=").append(System.getProperty("java.home")).append(';');
		sb.append("java.version=").append(System.getProperty("java.version")).append(';');
		sb.append("java.vendor=").append(System.getProperty("java.vendor")).append(';');
		sb.append("java.vm.name=").append(System.getProperty("java.vm.name")).append(';');
		sb.append("[Memory]: free=");
		sb.append(runtime.freeMemory() / 1048576).append("MB, total=");
		sb.append(runtime.totalMemory() / 1048576).append("MB, max=");
		sb.append(runtime.maxMemory() / 1048576).append("MB");
		return sb.toString();
	}
}