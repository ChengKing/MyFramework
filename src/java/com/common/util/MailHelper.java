package com.common.util;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

/**
 * 邮件客户端工具类. <BR>
 * 
 */
public class MailHelper {

	private static final Logger LOG = Logger.getLogger(MailHelper.class);

	private String failInfo = "";

	public String getFailInfo() {
		return failInfo;
	}

	/**
	 * 发送简单的邮件(不包含附件的邮件).
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param usr
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @param encoding
	 * @param extConfigs
	 *            包含发件人昵称等的系统配置集合
	 * @return
	 * @creator ChengKing @ 2012-2-10
	 */
	@SuppressWarnings("rawtypes")
	public static boolean sendMail(String smtpServer, String from, String to, String usr, String pwd, String subject,
			String msgBody, String encoding, Map extConfigs) throws Exception {
		if (StringHelper.isEmpty(encoding)) {
			encoding = "GBK";
		}
		return sendMail(smtpServer, from, new String[] { to }, null, usr, pwd, subject, msgBody, encoding, extConfigs);
	}

	/**
	 * 发送简单的邮件(不包含附件的邮件).
	 * 
	 * @deprecated by {@link #sendMail(String, String, String, String, String, String, String, String, Map)}
	 *             此接口不支持系统配置的发件人昵称等配置
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param usr
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @param encoding
	 * @return
	 * @throws IdMException
	 * @since v3.5
	 * @creator shixin @ 2011-7-15
	 */
	// public static boolean sendMail(String smtpServer, String from, String to, String usr, String pwd, String subject,
	// String msgBody, String encoding) throws IdMException {
	// return sendMail(smtpServer, from, to, usr, pwd, subject, msgBody, encoding, new HashMap());
	// }

	/**
	 * @deprecated 废弃 by {@link #sendMail(String, String, String, String, String, String, String, String)}
	 * 
	 *             发送简单的邮件(不包含附件的邮件).<br>
	 *             废弃原因：本方法默认使用GBK编码发送邮件，不能指定其它邮件正文编码格式；<br>
	 * 
	 * @throws IdMException
	 * 
	 * @see #sendMail(String, String, String[], String[], String, String, String, String)
	 */
	// public static boolean sendMail(String smtpServer, String from, String to, String usr, String pwd, String subject,
	// String msgBody) throws IdMException {
	// return sendMail(smtpServer, from, new String[] { to }, null, usr, pwd, subject, msgBody, "GBK");
	// }

	/**
	 * 指定编码格式，发送简单的邮件(不包含附件的邮件).
	 * 
	 * @param smtpServer
	 *            邮件服务器地址，如mail.trs.com.cn
	 * @param from
	 *            发件地址
	 * @param toList
	 *            收件地址集合
	 * @param ccList
	 *            抄送地址集合
	 * @param usr
	 *            邮件验证用户名
	 * @param pwd
	 *            邮件验证用户密码
	 * @param subject
	 *            主题
	 * @param msgBody
	 *            正文
	 * @param charset
	 *            系统编码
	 * @param extConfigs
	 *            包含发件人昵称等的系统配置集合
	 * @return
	 * @throws Exception
	 * @creator ChengKing @ 2012-2-10
	 */
	@SuppressWarnings("rawtypes")
	public static boolean sendMail(String smtpServer, String from, String[] toList, String[] ccList, String usr,
			final String pwd, String subject, String msgBody, String charset, Map extConfigs) throws Exception {
		LOG.debug("begin to send email for user [" + usr + "] by smtpServer [" + smtpServer + "], from [" + from
				+ "], with msgBody [" + msgBody + "] in charset [" + charset + "]");
		if (StringHelper.isEmpty(charset)) {
			charset = "GBK";
		}
		// 获取其它系统配置，如发件人的昵称系统配置
		String fromNickName = getFromNickName(from, extConfigs);

		final boolean missTo = (toList == null) || (toList.length == 0);
		final boolean missCc = (ccList == null) || (ccList.length == 0);
		if (missTo && missCc) {
			throw new Exception("邮件要发送的地址不能为空");
		}
		if (StringHelper.isEmpty(smtpServer)) {
			throw new Exception("邮件服务器SMTP不能为空");
		}
		Properties prop = new Properties();
		prop.put("mail.smtp.host", smtpServer);
		prop.put("mail.smtp.connectiontimeout", 20000);
		prop.put("mail.smtp.timeout", 20000);
		//
		String[] emailAndUser = fetchEmailAndUser(from, usr);
		if (emailAndUser == null) {
			throw new Exception("email_sender.fail.no_smtp_email 没有配置SMTP Email的发送地址");
		}

		Authenticator authenticator = null;
		if ((pwd != null) && (!("").equals(pwd))) {
			// fix TRSIDS-5358:支持多个用户名，以分号分割
			final String userName = emailAndUser[1];
			prop.put("mail.smtp.auth", "true");
			prop.put("mail.smtp.user", userName);
			prop.put("mail.smtp.password", pwd);
			authenticator = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, pwd);
				}
			};
		}
		// mail.smtp.connectiontimeout：Socket connection timeout value in milliseconds. This timeout is implemented by
		// java.net.Socket. Default is infinite timeout.
		// mail.smtp.timeout：Socket read timeout value in milliseconds. This timeout is implemented by java.net.Socket.
		// Default is infinite timeout.
		Session session = Session.getInstance(prop, authenticator);
		MimeMessage message = new MimeMessage(session);
		try {
			message.setHeader("Content-Transfer-Encoding", "base64"); 
			InternetAddress fromAddress = getFromInternetAddress(emailAndUser[0], charset, fromNickName);
			message.setFrom(fromAddress);
			if (false == missTo) {
				for (int i = 0; i < toList.length; i++) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(toList[i]));
				}
			}
			if (false == missCc) {
				for (int i = 0; i < ccList.length; i++) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccList[i]));
				}
			}
			message.setSubject(subject, charset);
			message.setSentDate(new Date());
			String type = "text/html;charset=" + charset;
			message.setContent(msgBody, type);
			// message.setText(msgBody);
			// MimeMultipart mm = new MimeMultipart();
			// MimeBodyPart mbp = new MimeBodyPart();
			// mbp.setContent(msgBody, type);
			// mm.addBodyPart(mbp);
			// message.setContent(mm);
			// if ((pwd != null) && (!("").equals(pwd))) {
			// LOG.debug("pwd in config is not null, so send message [" + message + "] after connect to smtpServer ["
			// + smtpServer + "].");
			// Transport transport = session.getTransport("smtp");
			// transport.connect(smtpServer, StringHelper.isEmpty(usr) ? extractUser(from) : usr, pwd);
			// transport.sendMessage(message, message.getAllRecipients());
			// transport.close();
			// } else {
			// LOG.debug("pwd in config is null, so send message [" + message + "] without connection.");
			// Transport.send(message);
			// }
			Transport.send(message);
		} catch (Exception e) {
			LOG.error("(Fail while sending mail,probably your MailServer config is wrong,props=" + smtpServer + ", "
					+ session.getProperties(), e);
			throw new Exception("邮件发送失败,失败原因:" + e.getMessage());
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("succeed to send email , message [" + message + "] .");
		}
		return true;
	}

	/**
	 * 从smtpEmail和authUser里面随机解析出匹配的发送的email和userName信息。<br>
	 * 如果有多个的话用分号(;)分割。当smtpEmail和authUser个数不一致时，采用最少的个数。当authUser为空时，以smtpEmail为准，authUser为单个smtpEmail的@符号之前的部分。<br>
	 * 比如，smtpEmail为"a@trs.com;b@trs.com;c@trs.com;"。如果authUser为空，则authUser分别为"a、b、c"。如果authUser的个数小于smtpEmail时，
	 * 以authUser随机选取。<br>
	 * 如果authUser的个数大于smtpEmails，则以smtpEmails的个数随机选取；
	 * 
	 * @param smtpEmails
	 *            配置的smtpEmail，多个之间用分号(;)分割。
	 * @param authUsers
	 *            配置的认证用户名集成，多个之间用分号(;)分割
	 * @return 第一个值[0]为smtpEmail，第二个值[1]为authUser
	 * @creator ChengKing
	 */
	static String[] fetchEmailAndUser(String smtpEmails, String authUsers) {
		String[] emailAndUser = new String[] { "", "" };
		if (StringHelper.isEmpty(smtpEmails)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("get smtpEmails[" + smtpEmails + "] is empty!");
			}
			return null;
		}
		String[] smtpEmailArray = StringHelper.split(smtpEmails, ";");
		if (LOG.isDebugEnabled()) {
			LOG.debug("get smtpEmailArray[" + smtpEmailArray + "] by smtpEmails[" + smtpEmails + "], authUsers["
					+ authUsers + "]");
		}
		int randomRange = 1, randomIndex = 0;
		//
		if (StringHelper.isEmpty(authUsers)) { // 如果认证用户为空，则从smtpEmails里面解析出用户
			randomRange = smtpEmailArray.length;
			randomIndex = (new Random()).nextInt(randomRange);
			String smtpEmail = smtpEmailArray[randomIndex];
			emailAndUser[0] = smtpEmail;
			emailAndUser[1] = extractUserNameBySingleEmail(smtpEmail);
		} else {
			String[] authUserArray = StringHelper.split(authUsers, ";");
			randomRange = Math.min(smtpEmailArray.length, authUserArray.length);
			randomIndex = (new Random()).nextInt(randomRange);
			emailAndUser[0] = smtpEmailArray[randomIndex];
			emailAndUser[1] = authUserArray[randomIndex];
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("get emailAndUser[" + StringHelper.toString(emailAndUser) + "] by smtpEmails[" + smtpEmails
					+ "], authUsers[" + authUsers + "]");
		}
		return emailAndUser;
	}

	/**
	 * 从Mail地址中解出用户名.<BR>
	 * 如果当前的email为分号分隔的多个email地址，则返回分号分隔的多个用户名组成的字符串；
	 * 
	 * 用例: <code>
	 *         assertEquals("liu.shen", MailHelper.extractUser("liu.shen@trs.com.cn"));
	 *         assertEquals("bx0702;trs0001;trs0002", MailHelper.extractUser("bx0702@sina.com;trs0001@sina.com;trs0002@sina.com"));
	 * </code>
	 * 
	 * @param email
	 *            email地址
	 * @return 用户名，如果email为空的话，返回空串("")；<br>
	 *         如果当前的email为分号分隔的多个email地址，则返回分号分隔的多个用户名组成的字符串；<br>
	 */
	public static String extractUser(String email) {
		if (StringHelper.isEmpty(email))
			return "";

		// fix TRSIDS-5358，支持多个邮件地址随机选择发送
		String[] smtpEmailArray = StringHelper.split(email, ";");
		StringBuffer smtpUserNameBuffer = new StringBuffer();
		if (smtpEmailArray == null || smtpEmailArray.length == 0) {
			return "";
		}
		for (String fromEmail : smtpEmailArray) {
			if (StringHelper.isEmpty(smtpUserNameBuffer.toString())) {
				smtpUserNameBuffer.append(extractUserNameBySingleEmail(fromEmail));
			} else {
				smtpUserNameBuffer.append(";").append(extractUserNameBySingleEmail(fromEmail));
			}
		}
		return smtpUserNameBuffer.toString();
	}

	/**
	 * 根据单个Email地址获取对应用户名
	 * 
	 * @param email
	 *            发送地址
	 * @return 对应用户名
	 * @creator ChengKing
	 */
	private static String extractUserNameBySingleEmail(String email) {
		if (StringHelper.isEmpty(email))
			return "";
		final int pos = email.indexOf('@');
		return (pos > 0) ? email.substring(0, pos) : email;
	}

	/**
	 * 是否为有效的email地址
	 * 
	 * @param email
	 * @return 如果有效，则返回true，否则返回false
	 */
	public static boolean isValidEMailAddress(String email) {
		if (email == null) {
			return false;
		}

		if (email.indexOf('@') < 1) {
			return false;
		}

		try {
			new InternetAddress(email);

			return true;
		} catch (AddressException e) {
			return false;
		}
	}

	/**
	 * @deprecated 废弃 by {@link #sendMailOfObject(String, String, String, String, String, String, Object, String)} <br>
	 * 
	 *             发送对象类型的邮件<br>
	 *             废弃原因：本方法默认使用GBK编码发送邮件，不能指定其它邮件正文编码格式；<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @return
	 */
	// public static boolean sendMailOfObject(String smtpServer, String from, String to, String pwd, String subject,
	// Object msgBody) {
	// return sendMailOfObject(smtpServer, from, to, extractUser(from), pwd, subject, msgBody, "GBK");
	// }

	/**
	 * 根据指定编码，发送对象类型的邮件<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param usr
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @param encoding
	 * @return
	 * @since v3.5
	 * @creator shixin @ 2011-7-16
	 */
	// public static boolean sendMailOfObject(String smtpServer, String from, String to, String usr, String pwd,
	// String subject, Object msgBody, String encoding) {
	// return sendMailOfObject(smtpServer, from, new String[] { to }, null, usr, pwd, subject, msgBody, encoding);
	// }

	/**
	 * @deprecated 废弃 by
	 *             {@link #sendMailOfObject(String, String, String[], String[], String, String, String, Object, String)}
	 *             发送对象类型的邮件<br>
	 *             废弃原因：本方法默认使用GBK编码发送邮件，不能指定其它邮件正文编码格式；<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param usr
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @return
	 */
	// public static boolean sendMailOfObject(String smtpServer, String from, String to, String usr, String pwd,
	// String subject, Object msgBody) {
	// return sendMailOfObject(smtpServer, from, new String[] { to }, null, usr, pwd, subject, msgBody, "GBK");
	// }

	/**
	 * 根据指定编码，发送对象类型的邮件<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param toList
	 * @param ccList
	 * @param usr
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @param encoding
	 *            邮件正文编码格式
	 * @return
	 * @since v3.5
	 * @creator shixin @ 2011-7-16
	 */
	// public static boolean sendMailOfObject(String smtpServer, String from, String toList[], String ccList[],
	// String usr, String pwd, String subject, Object msgBody, String encoding) {
	// if (StringHelper.isEmpty(encoding)) {
	// encoding = "GBK";
	// }
	// boolean missTo;
	// boolean missCc;
	// Session session;
	// MimeMessage message;
	// missTo = toList == null || toList.length == 0;
	// missCc = ccList == null || ccList.length == 0;
	// if (missTo && missCc)
	// return false;
	// Properties prop = new Properties();
	// prop.put("mail.smtp.host", smtpServer);
	// if (pwd != null)
	// prop.put("mail.smtp.auth", "true");
	// session = Session.getInstance(prop, null);
	// try {
	// message = new MimeMessage(session);
	// message.setHeader("Content-Transfer-Encoding", "base64");
	// message.setFrom(new InternetAddress(from));
	// if (!missTo) {
	// for (int i = 0; i < toList.length; i++)
	// message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toList[i]));
	//
	// }
	// if (!missCc) {
	// for (int i = 0; i < ccList.length; i++)
	// message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(ccList[i]));
	//
	// }
	// message.setSubject(subject, encoding);
	// message.setSentDate(new Date());
	// String type = "text/html;charset=" + encoding;
	// message.setContent(msgBody, type);
	// // message.setText(msgBody);
	// // MimeMultipart mm = new MimeMultipart();
	// // MimeBodyPart mbp = new MimeBodyPart();
	// // mbp.setContent(msgBody, type);
	// // mm.addBodyPart(mbp);
	// // message.setContent(mm);
	// if (pwd != null) {
	// Transport transport = session.getTransport("smtp");
	// transport.connect(smtpServer, StringHelper.isEmpty(usr) ? extractUser(from) : usr, pwd);
	// transport.sendMessage(message, message.getAllRecipients());
	// transport.close();
	// } else {
	// Transport.send(message);
	// }
	// return true;
	// } catch (AddressException e) {
	// LOG.error("(smtpHost, props)=" + smtpServer + ", " + session.getProperties(), e);
	// return false;
	// } catch (MessagingException e) {
	// LOG.error("(smtpHost, props)=" + smtpServer + ", " + session.getProperties(), e);
	// return false;
	// }
	// }

	/**
	 * @deprecated 废弃 by
	 *             {@link #sendMailOfObject(String, String, String[], String[], String, String, String, Object, String)}
	 *             发送对象类型的邮件<br>
	 *             废弃原因：本方法默认使用GBK编码发送邮件，不能指定其它邮件正文编码格式；<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param toList
	 * @param ccList
	 * @param usr
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @return
	 */
	// public static boolean sendMailOfObject(String smtpServer, String from, String toList[], String ccList[],
	// String usr, String pwd, String subject, Object msgBody) {
	// return sendMailOfObject(smtpServer, from, toList, ccList, usr, pwd, subject, msgBody, "GBK");
	// }

	/**
	 * 根据指定邮件正文编码格式，发送测试邮件；<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @param needAuth
	 * @param encoding
	 * @return
	 * @since v3.5
	 * @creator shixin @ 2011-7-16
	 */
	// public boolean sendMailTest(String smtpServer, String from, String to, String pwd, String subject, Object
	// msgBody,
	// boolean needAuth, String encoding) {
	// return sendMailTest(smtpServer, from, to, extractUser(from), pwd, subject, msgBody, needAuth, encoding);
	// }

	/**
	 * @deprecated 废弃掉by{@link #sendMailTest(String, String, String, String, String, Object, boolean, String)} <br>
	 *             发送测试邮件<br>
	 *             废弃原因：本方法默认使用GBK编码发送邮件，不能指定其它邮件正文编码格式；<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @param needAuth
	 *            新增是否认证参数
	 * @return 发送成功返回true，否则返回false
	 */
	// public boolean sendMailTest(String smtpServer, String from, String to, String pwd, String subject, Object
	// msgBody,
	// boolean needAuth) {
	// return sendMailTest(smtpServer, from, to, pwd, subject, msgBody, needAuth, "GBK");
	// }

	/**
	 * @deprecated 废弃by {@link #sendMailTest(String, String, String, String, String, String, Object, boolean, String)}
	 *             发送测试邮件<br>
	 *             废弃原因：本方法默认使用GBK编码发送邮件，不能指定其它邮件正文编码格式；<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param usr
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @param needAuth
	 *            新增是否认证参数
	 * @return 发送成功返回true，否则返回false
	 */
	// public boolean sendMailTest(String smtpServer, String from, String to, String usr, String pwd, String subject,
	// Object msgBody, boolean needAuth) {
	// return sendMailTest(smtpServer, from, new String[] { to }, null, usr, pwd, subject, msgBody, needAuth, "GBK");
	// }

	/**
	 * 指定邮件正文编码，发送测试邮件<br>
	 * 
	 * @param smtpServer
	 * @param from
	 * @param to
	 * @param usr
	 * @param pwd
	 * @param subject
	 * @param msgBody
	 * @param needAuth
	 * @param encoding
	 * @return
	 * @creator ChengKing 
	 */

	@SuppressWarnings("rawtypes")
	public boolean sendMailTest(String smtpServer, String from, String toList[], String ccList[], String usr,
			String pwd, String subject, Object msgBody, boolean needAuth, String charset, Map extConfigs) {
		// 获取其它系统配置，如发件人的昵称系统配置
		String fromNickName = getFromNickName(from, extConfigs);
		if (StringHelper.isEmpty(charset)) {
			charset = "GBK";
		}
		boolean missTo;
		boolean missCc;
		Session session;
		MimeMessage message;
		missTo = toList == null || toList.length == 0;
		missCc = ccList == null || ccList.length == 0;

		Properties prop = new Properties();
		prop.put("mail.smtp.host", smtpServer);
		prop.put("mail.smtp.connectiontimeout", 20000);
		prop.put("mail.smtp.timeout", 20000);
		if (needAuth)
			prop.put("mail.smtp.auth", "true");
		session = Session.getInstance(prop, null);
		try {
			if (missTo && missCc) {
				throw new Exception("邮件要发送到的Email地址不能为空!");
			}
			if (StringHelper.isEmpty(smtpServer)) {
				throw new Exception("邮件服务器SMTP不能为空");
			}
			message = new MimeMessage(session);
			message.setHeader("Content-Transfer-Encoding", "base64");

			InternetAddress fromAddress = getFromInternetAddress(from, charset, fromNickName);
			message.setFrom(fromAddress);
			if (!missTo) {
				for (int i = 0; i < toList.length; i++)
					message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toList[i]));

			}
			if (!missCc) {
				for (int i = 0; i < ccList.length; i++)
					message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(ccList[i]));

			}
			message.setSubject(subject, charset);
			message.setSentDate(new Date());
			String type = "text/html;charset=" + charset;
			message.setContent(msgBody, type);
			// message.setText(msgBody);
			// MimeMultipart mm = new MimeMultipart();
			// MimeBodyPart mbp = new MimeBodyPart();
			// mbp.setContent(msgBody, type);
			// mm.addBodyPart(mbp);
			// message.setContent(mm);
			if (needAuth) {
				Transport transport = session.getTransport("smtp");
				transport.connect(smtpServer, StringHelper.isEmpty(usr) ? extractUser(from) : usr, pwd);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} else {
				Transport.send(message);
			}
			return true;
		} catch (Exception e) {
			LOG.error("(smtpHost, props)=" + smtpServer + ", " + session.getProperties(), e);
			failInfo += "\n" + "失败原因：\n" + (e.toString() == null ? "未知" : e.toString()) + "\n\n" + "详细堆栈信息：" + "\n";
			StackTraceElement[] stackTraceElements = e.getStackTrace();
			for (int i = 0; i < stackTraceElements.length; i++) {
				failInfo += stackTraceElements[i] + "\n";
			}
			return false;
		}
	}

	/**
	 * 初始化获取发件人Address
	 * 
	 * @param from
	 * @param charset
	 * @param fromNickName
	 * @return
	 * @throws AddressException
	 * @creator ChengKing
	 */
	private static InternetAddress getFromInternetAddress(String from, String charset, String fromNickName)
			throws AddressException {
		InternetAddress fromAddress;
		if (StringHelper.isEmpty(fromNickName)) {
			LOG.debug("get fromNickName[" + fromNickName + "] is empty");
			fromAddress = new InternetAddress(from);
			return fromAddress;
		}
		try {
			fromAddress = new InternetAddress(from, MimeUtility.encodeText(fromNickName, charset, "B"));
		} catch (UnsupportedEncodingException e) {
			LOG.error("get error while new InternetAddress by from[" + from + "], fromNickName[" + fromNickName
					+ "], charset[" + charset + "], encoding[B]", e);
			fromAddress = new InternetAddress(from);
		}
		LOG.debug("get from email fromAddress[" + fromAddress + "] by from[" + from + "], fromNickName[" + fromNickName
				+ "], charset[" + charset + "], encoding[B]");
		return fromAddress;
	}

	/**
	 * 根据系统配置获取发件人昵称值
	 * 
	 * @param from
	 * @param extConfigs
	 * @creator ChengKing
	 */
	@SuppressWarnings("rawtypes")
	private static String getFromNickName(String from, Map extConfigs) {
		String fromNickName = from;
		if (extConfigs == null || extConfigs.size() == 0) {
			LOG.debug("get extConfigs[" + extConfigs + "] is empty, so return directly!");
			return fromNickName;
		}
		fromNickName = (String) extConfigs.get("fromNickName");
		LOG.debug("get from email nickName[" + fromNickName + "] by extConfigs[" + extConfigs + "]");
		return fromNickName;
	}
}