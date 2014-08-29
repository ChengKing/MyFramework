package com.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * 通过ssh方式连接远程服务器并执行命令的工具类<BR>
 * 
 * @since ChengKing
 */
public class SSHUtil {

	private static final Logger logger = Logger.getLogger(SSHUtil.class);
	/**
	 * 执行命令的超时时间：五分钟太长，改为30s，目前行为分析其实都是发命令给服务器，服务器后台执行的，所以超时时间不需要太长
	 */
	private static final int TIME_OUT = 1000 * 30;

	/**
	 * 上传分析器的介质文件到指定的机器里
	 * 
	 * @param connectionIp
	 *            连接使用的ip
	 * @param connectionUserName
	 *            连接使用的用户名
	 * @param connectionPassword
	 *            连接使用的密码
	 * @param resourceDir
	 *            上传的源目录
	 * @param desDir
	 *            上传的目标目录
	 * @return 是否上传成功的结果
	 * @since v1.0
	 * @creator ChengKing
	 */
	@SuppressWarnings("resource")
	public static boolean upload(String connectionIp, String connectionUserName, String connectionPassword,
			String resourceDir, String desDir) {
		Connection con = getConnection(connectionIp, connectionUserName, connectionPassword, 0);
		try {
			SCPClient scpClient = con.createSCPClient();
			scpClient.put(resourceDir, desDir); // 从本地复制文件到远程目录
			Session session = con.openSession();
			session.execCommand("uname -a && date && uptime && who"); // 远程执行命令

			// 显示执行命令后的信息
			InputStream stdout = new StreamGobbler(session.getStdout());

			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			while (true) {
				String line = br.readLine();
				if (line == null)
					break;

				if (logger.isDebugEnabled())
					logger.debug("Here is some information about the remote host:" + line);
			}
			if (logger.isDebugEnabled())
				logger.debug("ExitCode: " + session.getExitStatus());
			closeConn(con);
		} catch (Throwable e) {
			logger.error("failed to upload [" + resourceDir + "] to [" + desDir + "].", e);
			return false;
		}
		return true;
	}

	/**
	 * 向指定地址发送命令，默认用系统约束的30S超时时间
	 * 
	 * @param connectionIp
	 *            连接使用的ip
	 * @param connectionUserName
	 *            连接使用的用户名
	 * @param connectionPassword
	 *            连接使用的密码
	 * @param cmd
	 *            发送的命令
	 * @param charset
	 *            远程服务器的字符集
	 * @return 运行命令的结果
	 * @since v1.0
	 * @creator ChengKing @ 2013-7-5
	 */
	public static SSHResponse executeCmd(String connectionIp, String connectionUserName, String connectionPassword,
			String cmd, String charset) {
		return executeCmd(connectionIp, connectionUserName, connectionPassword, cmd, charset, TIME_OUT);
	}

	/**
	 * 向指定地址发送命令
	 * 
	 * @param connectionIp
	 *            连接使用的ip
	 * @param connectionUserName
	 *            连接使用的用户名
	 * @param connectionPassword
	 *            连接使用的密码
	 * @param cmd
	 *            发送的命令
	 * @param charset
	 *            远程服务器的字符集
	 * @param timeoutSecond
	 *            超时时间,单位为秒 如果为0，则不作超时时间约束
	 * @return 运行命令的结果
	 * @since v1.0
	 * @creator ChengKing @ 2013-9-22
	 */
	@SuppressWarnings("resource")
	public static SSHResponse executeCmd(String connectionIp, String connectionUserName, String connectionPassword,
			String cmd, String charset, int timeoutSecond) {
		if (logger.isDebugEnabled()) {
			logger.debug("command :" + cmd);
		}
		String localErrorMessage;
		Connection connection = getConnection(connectionIp, connectionUserName, connectionPassword, timeoutSecond);
		Session session = getSession(connection);
		if (session == null || connection == null) {
			localErrorMessage = "failed to get connection [" + connection + "], session [" + session
					+ "] by connectionIp [" + connectionIp + "], connectionUserName [" + connectionUserName
					+ "], cmd [" + cmd + "]";
			logger.error(localErrorMessage);
			return new SSHResponse(false, "", localErrorMessage);
		}
		try {
			session.execCommand(cmd);
		} catch (Throwable e) {
			localErrorMessage = "failed to execute command [" + cmd + "] .";
			logger.error(localErrorMessage, e);
			return new SSHResponse(false, "", localErrorMessage);
		}

		// 显示执行命令后的信息

		// ret = session.getExitStatus();

		InputStream stdout = new StreamGobbler(session.getStdout());
		List<String> result = new ArrayList<String>();

		InputStream stdErr = new StreamGobbler(session.getStderr());
		// 当设置了超时时间时，才对session做超时的设置，否则不需要
		if (timeoutSecond > 0)
			session.waitForCondition(ChannelCondition.EXIT_STATUS, timeoutSecond * 1000);
		List<String> errResult = new ArrayList<String>();

		try {
			result = loadText(stdout, charset);
		} catch (Throwable e) {
			localErrorMessage = "failed to loadText from inputStream [" + stdout + "] .";
			logger.error(localErrorMessage, e);
			return new SSHResponse(false, "", localErrorMessage);
		}
		String returnStr = "";
		for (String str : result) {
			returnStr = returnStr + "\n" + str;
		}

		try {
			errResult = loadText(stdErr, charset);
		} catch (Throwable e) {
			localErrorMessage = "failed to loadText from inputStream [" + stdErr + "] .";
			logger.error(localErrorMessage, e);
			return new SSHResponse(false, returnStr, localErrorMessage);
		}
		String errReturnStr = "";
		for (String str : errResult) {
			errReturnStr = errReturnStr + "\n" + str;
		}

		Integer code = session.getExitStatus();

		// 获得退出状态
		if (logger.isDebugEnabled()) {
			logger.debug("ExitCode: " + code);
			logger.debug("returnStr: " + returnStr);
		}
		if (!StringHelper.isEmpty(errReturnStr)) {
			logger.warn("errReturnStr: " + errReturnStr + " by connectionIp [" + connectionIp
					+ "], connectionUserName [" + connectionUserName + "], cmd [" + cmd + "].");
		}

		close(session);
		closeConn(connection);
		return new SSHResponse(code != null && code.equals(0), returnStr, errReturnStr);
	}

	/**
	 * 向指定地址发送命令
	 * 
	 * @param connectionIp
	 *            连接使用的ip
	 * @param connectionUserName
	 *            连接使用的用户名
	 * @param connectionPassword
	 *            连接使用的密码
	 * @return
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static SSHResponse executeCmd(String connectionIp, String connectionUserName, String connectionPassword,
			String cmd) {
		return executeCmd(connectionIp, connectionUserName, connectionPassword, cmd, "", TIME_OUT);
	}

	/**
	 * 关闭会话
	 * 
	 * @param session
	 *            SSH会话
	 * @since v1.0
	 * @creator ChengKing @ 2013-5-22
	 */
	private static void close(Session session) {
		try {
			session.close();
		} catch (Throwable e) {
			logger.error("failed to close session [" + session + "].", e);
		}
	}

	/**
	 * 关闭连接
	 * 
	 * @param connection
	 *            SSH连接
	 * @since v1.0
	 * @creator ChengKing @ 2013-5-22
	 */
	private static void closeConn(Connection connection) {
		try {
			connection.close();
		} catch (Throwable e) {
			logger.error("failed to close connection [" + connection + "].", e);
		}
	}

	/**
	 * 从连接中取到会话
	 * 
	 * @param connection
	 *            SSH连接
	 * @return SSH会话
	 * @since v1.0
	 * @creator ChengKing @ 2013-5-22
	 */
	private static Session getSession(Connection connection) {
		try {
			return connection.openSession();
		} catch (Throwable e) {
			logger.error("failed to get session from connection [" + connection + "]", e);
		}
		return null;
	}

	/**
	 * 根据ip地址等取到一个连接
	 * 
	 * @param connectionIp
	 *            远程服务器ip
	 * @param connectionUserName
	 *            连接找好
	 * @param connectionPassword
	 *            连接密码
	 * @param timeout
	 *            超时时间，以秒记，为0则不做超时控制
	 * @return SSH连接
	 * @since v1.0
	 * @creator ChengKing @ 2013-5-22
	 */
	private static Connection getConnection(String connectionIp, String connectionUserName, String connectionPassword,
			int timeout) {
		// 远程服务器的用户名密码
		Connection con = new Connection(connectionIp, 22);
		try {
			con.connect(null, timeout * 1000, timeout * 1000);
			boolean isAuthed = con.authenticateWithPassword(connectionUserName, connectionPassword);
			if (!isAuthed) {
				return null;
			}
		} catch (Throwable e) {
			logger.error("failed to connect by connectionIp [" + connectionIp + "], connectionUserName ["
					+ connectionUserName + "] , connectionPassword [" + connectionPassword + "].", e);
			return null;
		}

		// 连接

		return con;
	}

	/**
	 * 判断一个指定远程服务器上的指定路径是否可用（即是否存在），先取得一个连接然后判断文件夹是否存在，之后会自行释放连接。
	 * 
	 * @param connectionIp
	 *            远程服务器ip
	 * @param connectionUserName
	 *            连接账号
	 * @param connectionPassword
	 *            连接密码
	 * @param filepath
	 *            指定路径
	 * @return 如果存在，则返回true，否则返回false，设定超时时间为5s
	 * @since v1.0
	 * @creator ChengKing @ 2013-9-22
	 */
	public static boolean isAvailable(String connectionIp, String connectionUserName, String connectionPassword,
			String filepath) {
		SSHResponse response = executeCmd(connectionIp, connectionUserName, connectionPassword, "ls " + filepath, "", 5);
		return response.isSuccess();

	}

	/**
	 * 
	 * 运行SSH命令返回的结果，包含结果状态、标准输出、错误输出 <BR>
	 * 
	 * 
	 * @since ChengKing@2013-7-3
	 */
	public static class SSHResponse {

		/**
		 * 命令状态
		 */
		private boolean success;

		/**
		 * 标准输出
		 */
		private String stdOutputString;

		/**
		 * 错误输出
		 */
		private String errOutputString;

		/**
		 * @param success
		 * @param stdOutputString
		 * @param errOutputString
		 */
		public SSHResponse(boolean success, String stdOutputString, String errOutputString) {
			this.success = success;
			this.stdOutputString = stdOutputString;
			this.errOutputString = errOutputString;
		}

		/**
		 * Returns the {@link #success}.
		 * 
		 * @return the success.
		 */
		public boolean isSuccess() {
			return success;
		}

		/**
		 * Set {@link #success}.
		 * 
		 * @param success
		 *            The success to set.
		 */
		public void setSuccess(boolean success) {
			this.success = success;
		}

		/**
		 * Returns the {@link #stdOutputString}.
		 * 
		 * @return the stdOutputString.
		 */
		public String getStdOutputString() {
			return stdOutputString;
		}

		/**
		 * Set {@link #stdOutputString}.
		 * 
		 * @param stdOutputString
		 *            The stdOutputString to set.
		 */
		public void setStdOutputString(String stdOutputString) {
			this.stdOutputString = stdOutputString;
		}

		/**
		 * Returns the {@link #errOutputString}.
		 * 
		 * @return the errOutputString.
		 */
		public String getErrOutputString() {
			return errOutputString;
		}

		/**
		 * Set {@link #errOutputString}.
		 * 
		 * @param errOutputString
		 *            The errOutputString to set.
		 */
		public void setErrOutputString(String errOutputString) {
			this.errOutputString = errOutputString;
		}

	}

	/**
	 * 读取输入流，得到字符串构成的List. 由于输入流为传入对象, 因此本方法不关闭它.
	 * 
	 * @param is
	 * @param encoding
	 * @return
	 * @throws IOException
	 * @since ChengKing
	 */
	public static List<String> loadText(InputStream is, String encoding)
			throws IOException {
		List<String> result = new ArrayList<String>();
		InputStreamReader isr = new InputStreamReader(is,
				StringHelper.avoidEmpty(encoding, "UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			result.add(line);
		}
		return result;
	}

	
}
