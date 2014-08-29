package com.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

public class SocketUtil {

	private static final Logger LOG = Logger.getLogger(SocketUtil.class);

	private static final SocketUtilInternal suImpl;

	public static void setTimeout(Socket socket, int timeout) {
		if (socket != null) {
			try {
				socket.setSoTimeout(timeout);
			} catch (SocketException e) {
				LOG.warn("cannot set socket timeout! timeout(ms)=" + timeout, e);
			}
		}
	}

	public static void setTcpNoDelay(Socket socket, boolean noDelay) {
		if (socket != null) {
			try {
				socket.setTcpNoDelay(noDelay);
			} catch (SocketException e) {
				LOG.error("cannot set socket tcpNoDelay to " + noDelay, e);
			}
		}
	}

	/**
	 * 输出给定Socket的详细信息 用于调试等用
	 * 
	 * @param socket
	 *            给定Socket
	 * @return Socket详细信息组成的字符串.
	 */
	public static String getSocketDetail(Socket socket) {
		return suImpl.getSocketDetail(socket);
	}

	/**
	 * 输出给定ServerSocket的详细信息 用于调试等用途
	 * 
	 * @param ss
	 *            给定ServerSocket
	 * @return 详细信息组成的字符串
	 */
	public static String getSocketDetail(ServerSocket ss) {
		return suImpl.getSocketDetail(ss);
	}

	static {
		if (EnvUtil.isJDK14OrHigher()) {
			suImpl = new JDK14SocketUtilImpl();
		} else {
			suImpl = null;
		}
	}

	/**
	 * 
	 * @param serverIP
	 * @param serverPort
	 * @param message
	 * @return
	 * @throws Exception
	 * @creator ChengKing @ Aug 15, 2009
	 */
	public static String sendMessage(String serverIP, int serverPort, String message) throws Exception {
		Socket socket = null;
		try {
			socket = new Socket(serverIP, serverPort);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			//
			byte[] inbytes = new byte[200];
			in.read(inbytes);
			byte[] outbytes = message.getBytes();
			out.write(outbytes);
			out.flush();
			in.read(inbytes);
			return new String(inbytes);
		} catch (Exception ex) {
			LOG.error(
					"Send message(" + message + ") to (" + serverIP + ":" + serverPort + ") error:" + ex.getMessage(),
					ex);
			throw ex;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static int getTimeout(Socket socket) {
		if (socket == null) {
			return -1;
		}

		try {
			return socket.getSoTimeout();
		} catch (SocketException e) {
			return -2;
		}
	}
}

abstract class SocketUtilInternal {

	abstract String getSocketDetail(Socket socket);

	abstract String getSocketDetail(ServerSocket ss);
}