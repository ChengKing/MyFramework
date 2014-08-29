package com.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

/**
 * Mac信息工具类.<BR>
 */
public class MacAddrUtil {

	private static String osName = System.getProperty("os.name");

	private static double osVersion;

	private static final double VISTA_INNER_VERSION = 6.0;

	private static final Logger logger = Logger.getLogger(MacAddrUtil.class);

	/**
	 * 
	 * @return 所有mac地址的数组. 如果获取不到, 则返回new String[0].
	 */
	public static String[] getMacAddrs() {
		String sOs = osName.toUpperCase();
		if (sOs.indexOf("WINDOWS") >= 0) {
			return getMacOnWindows();
		} else if (sOs.indexOf("LINUX") >= 0) {
			return getMacOnLinux();
		} else if (sOs.indexOf("SOLARIS") >= 0 || sOs.indexOf("SUNOS") >= 0) {
			return getMacOnSolaris();
		} else if (sOs.indexOf("AIX") >= 0) {
			return getMacOnAIX();
		} else if (sOs.indexOf("HP") >= 0) {
			return getMacOnHP();
		} else if (sOs.indexOf("MAC OS X") >= 0) {
			// TODO parse macosx mac address
			String[] tmp = { "ff" };
			return tmp;
		}

		return new String[0];
	}

	private static String[] getMacOnWindows() {
		if (osVersion >= VISTA_INNER_VERSION) {
			return parseGetMAC();
		} else {
			return parseIPConfig();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] parseIPConfig() {
		List macList = new ArrayList();
		try {
			String cmd = "ipconfig /all";
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			for (String line = bufferedreader.readLine(); line != null; line = bufferedreader.readLine()) {
				if (line.indexOf("Physical Address") > 0) {
					int i = line.indexOf("Physical Address") + 36;
					macList.add(line.substring(i).trim());
				}
			}
			bufferedreader.close();

			process.waitFor();
		} catch (Exception e) {
			System.err.println(e + " occured when getMacOnWindows()! os=" + osName);
			e.printStackTrace();
			logger.error(e + " occured when getMacOnWindows()! os=" + osName, e);
		}
		return (String[]) macList.toArray(new String[0]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] parseGetMAC() {
		List macList = new ArrayList();
		int exitValue = 0;
		try {
			String cmd = "getmac /nh";
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			for (String line = bufferedreader.readLine(); line != null; line = bufferedreader.readLine()) {
				// System.out.println(line);
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				StringTokenizer st = new StringTokenizer(line);
				if (st.hasMoreTokens()) {
					macList.add(st.nextToken());
				}
			}
			bufferedreader.close();

			exitValue = process.waitFor();
		} catch (Exception e) {
			System.err.println(e + " occured when exec getmac.exe /nh! os=" + osName);
			e.printStackTrace();
			logger.error(e + " occured when exec getmac.exe /nh! os=" + osName, e);
		}
		if (exitValue != 0) {
			throw new RuntimeException("exitValue: " + " of exec getmac.exe /nh!");
		}

		return (String[]) macList.toArray(new String[0]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] getMacOnLinux() {
		try {
			String cmd = "/sbin/ifconfig -a";
			Process process = Runtime.getRuntime().exec(cmd);
			InputStream is = process.getInputStream();
			List macList = parseMacsFromLinuxIfconfigOutput(is);

			process.waitFor();
			return (String[]) macList.toArray(new String[0]);
		} catch (Exception e) {
			System.err.println(e + " occured when getMacOnLinux()! os=" + osName);
			e.printStackTrace();
			logger.error(e + " occured when getMacOnLinux()! os=" + osName, e);
			return new String[0];
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static List parseMacsFromLinuxIfconfigOutput(InputStream is) throws IOException {
		List macList = new ArrayList();
		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
		for (String line = bufferedreader.readLine(); line != null; line = bufferedreader.readLine()) {
			line = line.toUpperCase();
			if (line.indexOf("HWADDR") > 0) {
				int i = line.indexOf("HWADDR") + 7;
				macList.add(line.substring(i).trim().replace(':', '-'));
			}
		}
		bufferedreader.close();
		return macList;
	}

	private static String[] getMacOnSolaris() {
		return getMacByCmd("/usr/bin/hostid");
	}

	private static String[] getMacOnAIX() {
		return getMacByCmd("/usr/bin/uname -m");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] getMacByCmd(String cmd) {
		List macList = new ArrayList();
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			for (String line = bufferedreader.readLine(); line != null; line = bufferedreader.readLine()) {
				macList.add(line.trim().toUpperCase());
			}
			bufferedreader.close();

			process.waitFor();
		} catch (Exception e) {
			System.err.println(e + " occured when getMacByCmd()! cmd=" + cmd + ", os=" + osName);
			e.printStackTrace();
			logger.error(e + " occured when getMacByCmd()! cmd=" + cmd + ", os=" + osName, e);
		}
		return (String[]) macList.toArray(new String[0]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] getMacOnHP() {
		List macList = new ArrayList();
		try {
			String cmd = "/usr/sbin/lanscan";
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			for (String line = bufferedreader.readLine(); line != null; line = bufferedreader.readLine()) {
				line = line.toUpperCase();
				int nPose = line.indexOf("0X");
				if (nPose > 0) {
					int nStart = nPose + 2;
					int nEnd = line.indexOf(" ", nStart);
					macList.add(line.substring(nStart, nEnd).trim());
				}
			}
			bufferedreader.close();

			process.waitFor();
		} catch (Exception e) {
			System.err.println(e + " occured when getMacOnHP()! os=" + osName);
			e.printStackTrace();
			logger.error(e + " occured when getMacOnHP()! os=" + osName, e);
		}
		return (String[]) macList.toArray(new String[0]);
	}

	static void printLocalMacAddrs() {
		System.out.println("----Begin getMacAddrs----");
		String[] macAddrs = getMacAddrs();
		for (int i = 0; i < macAddrs.length; i++) {
			System.out.println(i + ": " + macAddrs[i]);
		}
		System.out.println("----End getMacAddrs----");
	}

	static {
		String sOSVer = System.getProperty("os.version");
		try {
			osVersion = Double.parseDouble(sOSVer);
		} catch (Exception e) {
			osVersion = 1.0;
		}
	}

	/**
	 * 
	 * @param machineCode
	 * @return
	 */
	public static boolean isValidMachineCode(String machineCode) {
		if (machineCode == null) {
			return false;
		}
		String[] macAddrs = getMacAddrs();
		for (int i = 0; i < macAddrs.length; i++) {
			if (machineCode.equals(DigestUtils.md5Hex(macAddrs[i]))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取主网卡(第一个网络接口)的MAC地址.
	 * 
	 * @return 十六进制表示的MAC地址.
	 * @since v3.5
	 * @creator liushen @ Dec 14, 2009
	 */
	public static String getPrimaryMacInHex() {
		String myMac = null;
		String[] allMacs = MacAddrUtil.getMacAddrs();
		if (allMacs == null || allMacs.length == 0) {
			logger.error("get allMacs[" + allMacs + "] is empty!");
			return "";
		}
		myMac = allMacs[0];
		return (myMac == null) ? "" : DigestUtils.md5Hex(myMac);
	}
}