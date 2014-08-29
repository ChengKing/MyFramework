/*
 * Title: TRS 身份服务器 Copyright: Copyright (c) 2004-2007, TRS信息技术有限公司. All rights reserved. License: see the license file.
 * Company: TRS信息技术有限公司(www.trs.com.cn)
 * 
 * Class: com.trs.idm.util.JDK13EnvImpl Created: liushen@2007-6-23 下午10:33:07
 */
package com.common.util;

/**
 * 
 * @author TRS信息技术有限公司
 */
class JDK13EnvImpl extends JDKEnv {

	String getVMMemeoryInfo() {
		Runtime runtime = Runtime.getRuntime();
		StringBuffer sb = new StringBuffer(64);
		sb.append("user.dir=").append(System.getProperty("user.dir")).append(';');
		sb.append("java.home=").append(System.getProperty("java.home")).append(';');
		sb.append("java.version=").append(System.getProperty("java.version")).append(';');
		sb.append("java.vendor=").append(System.getProperty("java.vendor")).append(';');
		sb.append("java.vm.name=").append(System.getProperty("java.vm.name")).append(';');
		sb.append("[Memory]: free=");
		sb.append(runtime.freeMemory() >> 20).append("MB, total=");
		sb.append(runtime.totalMemory() >> 20).append("MB");
		return sb.toString();
	}
}