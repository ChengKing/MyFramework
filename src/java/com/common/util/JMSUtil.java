package com.common.util;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * 检测JMS服务的工具类 <BR>
 * 
 * @since ChengKing
 */
public class JMSUtil {

	private static final Logger logger = Logger.getLogger(JMSUtil.class);

	/**
	 * 根据jndi名称获取JNDI连接
	 * 
	 * @param connections
	 * @return
	 * @throws Exception
	 * @creator ChengKing
	 */
	public static Destination getDestination(String factory, String className, String url, String desName)
			throws Exception {
		Destination destination = null;
		try {
			Properties pro = new Properties();
			pro.setProperty("java.naming.factory.initial", className);
			pro.setProperty("java.naming.provider.url", url);
			Context initCtx = new javax.naming.InitialContext(pro);
			destination = (Destination) initCtx.lookup(desName);
		} catch (NamingException e) {
			logger.error("failed to find connectionFactory by factoryName [" + factory + "].", e);
		} catch (Throwable t) {
			logger.error("failed to find destination by desName [" + desName + "].", t);
		}
		return destination;
	}

	/**
	 * 根据jndi名称获取JNDI连接
	 * 
	 * @param connections
	 * @return
	 * @throws Exception
	 * 
	 * @creator ChengKing
	 */
	public static Connection getJNDIConnection(String factory, String className, String url) throws Exception {
		// ConnectionFactory ：连接工厂，JMS 用它创建连接
		ConnectionFactory connectionFactory = null;
		// Connection ：JMS 客户端到JMS Provider 的连接
		Connection connection = null;
		Context initCtx = getJNDIContext(className, url);
		try {
			connectionFactory = (ConnectionFactory) initCtx.lookup(factory);
		} catch (NamingException e) {
			throw new Exception("failed to find connectionFactory by factoryName [" + factory + "].", e);
		}

		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			if (logger.isDebugEnabled()) {
				logger.debug("create new jms connection[" + connection + "] by factory[" + factory + "], className["
						+ className + "], url[" + url + "]");
			}
		} catch (JMSException e) {
			releaseConnection(connection, null);
			throw new Exception("cannot create connection by factoryName [" + factory + "].", e);
		} catch (Throwable t) {
			releaseConnection(connection, null);
			throw new Exception("cannot create connection by factoryName [" + factory + "].", t);
		}
		return connection;
	}

	/**
	 * 释放消息中间件的长连接
	 * 
	 * @since v3.5
	 * @creator ChengKing
	 */
	public static void releaseConnection(Connection connection, Session session) {
		try {
			if (session != null) {
				session.close();
				if (logger.isDebugEnabled()) {
					logger.debug("close jms session with connection[" + connection + "]");
				}
				session = null;
			}
		} catch (Throwable t) {
			logger.error("failed to release session [" + session + "]", t);
		}

		try {
			if (null != connection) {
				// connection.stop();
				connection.close();
				if (logger.isDebugEnabled()) {
					logger.debug("close jms connection[" + connection + "]");
				}
				connection = null;
			}
		} catch (Throwable t) {
			logger.error("failed to release connection [" + connection + "]", t);
		}
	}

	/**
	 * 根据配置初始化上下文
	 * 
	 * @param className
	 * @param jmsUrl
	 * @return
	 * @throws Exception
	 * @creator ChengKing
	 */
	public static Context getJNDIContext(String className, String jmsUrl) throws Exception {
		Properties pro = new Properties();
		pro.setProperty("java.naming.factory.initial", className);
		pro.setProperty("java.naming.provider.url", jmsUrl);

		Context ctx = null;
		try {
			ctx = new javax.naming.InitialContext(pro);
		} catch (NamingException e) {
			logger.error("failed to init jndi context by Properties[" + pro + "].", e);
			throw new Exception("failed to init jndi context by Properties[" + pro + "].", e);
		}
		return ctx;
	}

	/**
	 * 根据上下文及工厂名获取连接
	 * 
	 * @param ctx
	 * @param factoryName
	 * @return
	 * @throws Exception
	 * @creator ChengKing
	 */
	public static QueueConnection getJNDIQueueConnection(Context ctx, String factoryName) throws Exception {
		QueueConnectionFactory connectionFactory = null;
		try {
			connectionFactory = (QueueConnectionFactory) ctx.lookup(factoryName);
		} catch (NamingException e) {
			logger.error("failed to find connectionFactory by factoryName [" + factoryName + "], ctx[" + ctx + "].", e);
			throw new Exception("failed to find connectionFactory by factoryName [" + factoryName + "], ctx[" + ctx
					+ "].", e);
		}
		QueueConnection connection = null;
		try {
			connection = connectionFactory.createQueueConnection();
			if (logger.isDebugEnabled()) {
				logger.debug("create new jms connection[" + connection + "] by factoryName [" + factoryName + "], ctx["
						+ ctx + "]");
			}
		} catch (JMSException e) {
			logger.error("failed to create new jms connection[" + connection + "] by factoryName [" + factoryName
					+ "], ctx[" + ctx + "].", e);
			releaseConnection(connection, null);
			throw new Exception("cannot create connection by factoryName[" + factoryName + "], ctx[" + ctx + "].", e);
		} catch (Throwable t) {
			logger.error("failed to create new jms connection[" + connection + "] by factoryName [" + factoryName
					+ "], ctx[" + ctx + "].", t);
			releaseConnection(connection, null);
			throw new Exception("cannot create connection by factoryName[" + factoryName + "], ctx[" + ctx + "].", t);
		}
		return connection;
	}

	/**
	 * 根据连接获取QueueSession
	 * 
	 * @param queueConnection
	 * @return
	 * @throws Exception
	 * @creator ChengKing
	 */
	public static QueueSession getJNDIQueueSession(QueueConnection queueConnection) throws Exception {
		QueueSession session = null;
		boolean transacted = false;
		try {
			session = queueConnection.createQueueSession(transacted, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e1) {
			logger.error("failed to init jndi QueueSession by queueConnection[" + queueConnection + "].", e1);
			releaseConnection(queueConnection, session);
			throw new Exception("failed to init jndi QueueSession by queueConnection[" + queueConnection + "].", e1);
		}
		return session;
	}

	/**
	 * 根据连接和会话，以及队列名称获取QueueReceiver
	 * 
	 * @param ctx
	 * @param queueConnection
	 * @param session
	 * @param jmsResultName
	 * @return
	 * @throws Exception
	 * @creator ChengKing
	 */
	public static QueueReceiver getJNDIQueueReceiver(Context ctx, QueueConnection queueConnection,
			QueueSession session, String jmsResultName) throws Exception {
		Queue inQueue = null;
		try {
			inQueue = (Queue) ctx.lookup(jmsResultName);
		} catch (Exception e) {
			// zhangshi 这个是先类似jndi的方式查询有没有，如果查不到那么会创建一个队列
			// 对于apache的而言，不用事先创建队列，用的时候直接create就行；但是ibm的得要先在mq那边创建了才能用
			try {
				inQueue = session.createQueue(jmsResultName);
			} catch (JMSException e1) {
				logger.error(
						"failed to createQueue by session[" + session + "], jmsResultName[" + jmsResultName + "].", e1);
				releaseConnection(queueConnection, session);
				throw new Exception("failed to createQueue by session[" + session + "], jmsResultName["
						+ jmsResultName + "].", e1);
			}
		}
		QueueReceiver queueReceiver = null;
		try {
			queueReceiver = session.createReceiver(inQueue);
			if (logger.isDebugEnabled()) {
				logger.debug("get jms queueReceiver[" + queueReceiver + "] by session[" + session + "], ctx[" + ctx
						+ "], jmsResultName[" + jmsResultName + "], queueConnection[" + queueConnection
						+ "], jmsResultName[" + jmsResultName + "]");
			}
		} catch (JMSException e) {
			logger.error("failed to createReceiver by session[" + session + "], jmsResultName[" + jmsResultName
					+ "], inQueue[" + inQueue + "].", e);
			releaseConnection(queueConnection, session);
			throw new Exception("failed to createQueue by session[" + session + "], jmsResultName[" + jmsResultName
					+ "], inQueue[" + inQueue + "].", e);
		}
		return queueReceiver;
	}

	/**
	 * 关闭上下文连接
	 * 
	 * @param ctx
	 * @creator ChengKing
	 */
	public static void realseCtx(Context ctx) {
		try {
			if (ctx != null) {
				ctx.close();
				if (logger.isDebugEnabled()) {
					logger.debug("close jms ctx with ctx[" + ctx + "]");
				}
				ctx = null;
			}
		} catch (Throwable t) {
			logger.error("failed to release ctx [" + ctx + "]", t);
		}
	}

	/**
	 * @param queueConnection
	 * @param session
	 * @param queueReceiver
	 * @since v1.0
	 * @creator ChengKing
	 */
	public static void releaseConnection(QueueConnection queueConnection, QueueSession session,
			QueueReceiver queueReceiver) {
		try {
			if (queueReceiver != null) {
				queueReceiver.close();
				if (logger.isDebugEnabled()) {
					logger.debug("close jms queueReceiver with queueConnection[" + queueConnection + "], session["
							+ session + "]");
				}
				queueReceiver = null;
			}
		} catch (Throwable t) {
			logger.error("failed to release queueReceiver [" + queueReceiver + "]", t);
		}
		releaseConnection(queueConnection, session);
	}
}
