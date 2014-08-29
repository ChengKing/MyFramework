package com.common.util;

import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

/**
 * 
 * @author ChengKing
 */
class JDK14SocketUtilImpl extends SocketUtilInternal {

	private static final Logger LOG = Logger.getLogger(JDK14SocketUtilImpl.class);

	/**
	 * @see com.trs.idm.util.SocketUtilInternal#getSocketDetail(java.net.Socket)
	 */
	String getSocketDetail(Socket socket) {
		if (socket == null) {
			return "The Socket object is null!";
		}
		StringBuffer sb = new StringBuffer(128);
		try {
			sb.append("Socket detail: receiveBuf<").append(socket.getReceiveBufferSize());
			sb.append(">/sendBuf<").append(socket.getSendBufferSize());
			sb.append(">/timeout<").append(socket.getSoTimeout());
			sb.append(">/keepAlive<").append(socket.getKeepAlive());
			sb.append(">/tcpNoDelay<").append(socket.getTcpNoDelay());
			sb.append(">/soLinger<").append(socket.getSoLinger());
			sb.append(">/OOBInline<").append(socket.getOOBInline());
			sb.append(">/reuseAddress<").append(socket.getReuseAddress());
			sb.append(">/trafficClass<").append(socket.getTrafficClass());
			sb.append(">/localSocketAddr<").append(socket.getLocalSocketAddress());
			if (socket instanceof SSLSocket) {
				SSLSocket sslSocket = (SSLSocket) socket;
				sb.append(">/needClientAuth<").append(sslSocket.getNeedClientAuth());
			}
			sb.append(">/class<").append(socket.getClass().getName());
			sb.append(">/toString: ").append(socket.toString());
		} catch (Throwable e) {
			LOG.warn("getDetail fail! socket=" + socket, e);
		}
		return sb.toString();

	}

	/**
	 * @see com.trs.idm.util.SocketUtilInternal#getSocketDetail(java.net.ServerSocket)
	 */
	String getSocketDetail(ServerSocket ss) {
		if (ss == null) {
			return "The ServerSocket object is null!";
		}
		StringBuffer sb = new StringBuffer(128);
		try {
			sb.append("Socket detail: receiveBuf<").append(ss.getReceiveBufferSize());
			sb.append(">/timeout<").append(ss.getSoTimeout());
			if (ss instanceof SSLServerSocket) {
				SSLServerSocket sslServer = (SSLServerSocket) ss;
				sb.append(">/needClientAuth<").append(sslServer.getNeedClientAuth());
			}
			sb.append(">/class<").append(ss.getClass().getName());
			sb.append(">/toString: ").append(ss.toString());
		} catch (Throwable e) {
			LOG.warn("fail to getSocketDetail!", e);
		}
		return sb.toString();
	}

}