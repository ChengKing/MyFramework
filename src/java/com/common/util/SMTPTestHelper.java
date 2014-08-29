package com.common.util;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;

import org.apache.log4j.Logger;

/**
 * EMail SMTP连接测试对象. <BR>
 * 
 * @author ChengKing
 */
public class SMTPTestHelper {
    
    private static final Logger LOG = Logger.getLogger(SMTPTestHelper.class);
    
    /**
     * 测试连接结果是否成功.
     */
    private boolean connectSuccess;
    
    /**
     * 失败原因.
     */
    private String failInfo;
    
    /**
     * 所测试的SMTP服务器.
     */
    private String smtpServer;
    
    /**
     * 所测试的EMail地址.
     */
    private String mailAddr;
    
    /**
     * SMTP服务器端口.
     */
    private int port = 25;
    
    /**
     * 是否需要SMTP认证.
     */
    private boolean needAuth;
    
    /**
     * 邮件的SMTP认证用户.
     */
    private String mailUser;
    
    /**
     * 邮件密码.
     */
    private String mailPwd;
    
    public SMTPTestHelper(String smtpServer, String mailAddr) {
        this.smtpServer = smtpServer;
        this.mailAddr = mailAddr;
    }
    
    public SMTPTestHelper(String smtpServer, String mailAddr, String usr, String pwd) {
        this(smtpServer, mailAddr);
        
        if (usr != null && usr.trim().length() > 0) {
            needAuth = true;
            this.mailUser = usr;
            this.mailPwd = pwd;
        }
    }
    
    /**
     * 测试SMTP连接, 并根据连接结果设置相应成员变量的值.
     */
    public void connect() {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", smtpServer);
        if (needAuth) {
            prop.put("mail.smtp.auth", "true");
        }
        Session session = Session.getInstance(prop, null);
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
            if (needAuth) {
                transport.connect(smtpServer, port, mailUser, mailPwd);
            } else {
                transport.connect();
            }
            connectSuccess = true;
            transport.close();
        } catch (Exception e) {
            LOG.error("fail! mail prop=" + prop, e);
            failInfo = e.toString();
        }
    }

    /**
     * Returns the {@link #mailUser}.
     * @return the mailUser.
     */
    public String getMailUser() {
        return mailUser;
    }

    /**
     * Set {@link #mailUser}.
     * @param mailUser The mailUser to set.
     */
    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    /**
     * Returns the {@link #needAuth}.
     * @return the needAuth.
     */
    public boolean isNeedAuth() {
        return needAuth;
    }

    /**
     * Set {@link #needAuth}.
     * @param needAuth The needAuth to set.
     */
    public void setNeedAuth(boolean needAuth) {
        this.needAuth = needAuth;
    }

    /**
     * Returns the {@link #port}.
     * @return the port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Set {@link #port}.
     * @param port The port to set.
     */
    public void setPort(int port) {
        if (port > 0 && port < 65535) {
            this.port = port;
        }
    }

    /**
     * Returns the {@link #connectSuccess}.
     * @return the success.
     */
    public boolean isConnectSuccess() {
        return connectSuccess;
    }

    /**
     * Returns the {@link #failInfo}.
     * @return the failInfo.
     */
    public String getFailInfo() {
        return failInfo;
    }

    /**
     * Returns the {@link #mailAddr}.
     * @return the mailAddr.
     */
    public String getMailAddr() {
        return mailAddr;
    }

    /**
     * Returns the {@link #smtpServer}.
     * @return the smtpServer.
     */
    public String getSmtpServer() {
        return smtpServer;
    }

    /**
     * Set {@link #mailPwd}.
     * @param mailPwd The mailPwd to set.
     */
    public void setMailPwd(String mailPwd) {
        this.mailPwd = mailPwd;
    }

}