package com.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 * 网络处理相关的工具类. <BR>
 */
public class NetUtil {
    
    private static final Logger LOG = Logger.getLogger(NetUtil.class);

    /**
     * 测试是否能与指定主机名或IP, 指定端口建立TCP连接.
     * @return 如果能成功建立TCP连接, 返回true; 否则返回false
     * @deprecated ChengKing
     *  use {@link #assertConnect(String, int)} instead!
     */
    public static boolean canConnect(String host, int port) {
        if (host == null || host.trim().length() == 0) {
            return false;
        }
        if (port < 0 || port > 65535) {
            return false;
        }

        Socket socket = null;
        try {
            socket = new Socket(host, port);
            return true;
        } catch (UnknownHostException e) {
            LOG.error("UnknownHost!", e);
        } catch (IOException e) {
            LOG.error("IOException!", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.error("fail on close!" + e);
                }
            }
        }

        return false;
    }
    
    /**
     * 测试是否能与指定主机名或IP, 指定端口建立TCP连接.
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static void assertConnect(String host, int port) throws IOException {
        if (host == null || host.trim().length() == 0) {
        	throw new IllegalArgumentException("host not specified!");
        }
        if (port < 0 || port > 65535) {
        	throw new IllegalArgumentException("invalid port: " + port);
        }

        Socket socket = null;
        try {
            socket = new Socket(host, port);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.error("fail on close!" + e);
                }
            }
        }

    }

    /**
     * @deprecated ChengKing use {@link #getNonLoopbackIPV4Address()} instead!
     */
    public static String getIPAddress() {
    	return getNonLoopbackIPV4Address();
    }

    /**
     * 是否为IP v4地址.
     */
    public static boolean isIPv4Address(InetAddress address) {
        if (address == null) {
            return false;
        }
        return address.getAddress().length == 4;
    }
    
    /**
     * 获取本机的一个IPV4地址(非本地回环IP地址).
     * @return 本机的一个IPV4地址(排除本地回环IP地址), 如出现异常返回null.
     * @since ChengKing
     */
    @SuppressWarnings("rawtypes")
	public static String getNonLoopbackIPV4Address() {
        Enumeration nets = null;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
        	LOG.error("fail to getNetworkInterfaces!", e);
        } catch (RuntimeException e) {
        	LOG.error("fail to getNetworkInterfaces!", e);
        }
        if (nets == null) {
            return null;
        }
        
        NetworkInterface netInterface;
        for ( ; nets.hasMoreElements(); ) {
            netInterface = (NetworkInterface) nets.nextElement();
            Enumeration ips = netInterface.getInetAddresses();
            if (ips == null) {
                continue;
            }
            
            for ( ; ips.hasMoreElements(); ) {
                InetAddress address = (InetAddress) ips.nextElement();
                if (address.isLoopbackAddress()) {
                    continue;
                }
                if (false == isIPv4Address(address)) {
                    continue;
                }
                return address.getHostAddress();
            }
        }
        return null;
    }
    
}