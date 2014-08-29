package com.common.util;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.log4j.Logger;

/**
 * 提供基于消息中间件进行消息接收的消息接收器 <BR>
 * 
 * @since ChengKing
 */
public class JMSConsumerProvider {

	private Connection connection;

	private Session session;

	private static final Logger logger = Logger.getLogger(JMSConsumerProvider.class);

	public MessageConsumer createConsumer(String factoryName, String className, String connectionUrl,
			String connectionQueueName) {
		JMSUtil.releaseConnection(connection, session);
		try {
			connection = JMSUtil.getJNDIConnection(factoryName, className, connectionUrl);
			connection.start();
			if (logger.isDebugEnabled()) {
				logger.debug("succeed to get connection from connectionUrl [" + connectionUrl + "] by factoryName ["
						+ factoryName + "] with className [" + className + "]");
			}
		} catch (Exception e) {
			logger.error("failed to get connection from connectionUrl [" + connectionUrl + "] by factoryName ["
					+ factoryName + "] with className [" + className + "]", e);
			return null;
		} catch (Throwable t) {
			logger.error("failed to get connection from connectionUrl [" + connectionUrl + "] by factoryName ["
					+ factoryName + "] with className [" + className + "]", t);
			return null;
		}
		Destination destination = null;
		try {
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			destination = JMSUtil.getDestination(factoryName, className, connectionUrl, connectionQueueName);
			// 通过JNDI取destination时，如果取不到，就默认从session中create一个队列出来
			if (null == destination) {
				logger.info("destination is null by connectionUrl [" + connectionUrl + "] by factoryName ["
						+ factoryName + "] with className [" + className + "], createQueue as default from session ["
						+ session + "].");
				destination = session.createQueue(connectionQueueName);
			}
			// System.out.println("succeed to create session [" + session + "] ,and get destination [" + destination
			// + "] from connection [" + connection + "] with connectionQueueName [" + connectionQueueName + "].");
			if (logger.isDebugEnabled()) {
				logger.debug("succeed to create session [" + session + "] ,and get destination [" + destination
						+ "] from connection [" + connection + "] with connectionQueueName [" + connectionQueueName
						+ "].");
			}
		} catch (JMSException e) {
			logger.error("failed to create session or get destination from connection [" + connection
					+ "],with connectionQueueName [" + connectionQueueName + "]", e);
			return null;
		} catch (Throwable t) {
			logger.error("failed to create session or get destination from connection [" + connection
					+ "],with connectionQueueName [" + connectionQueueName + "]", t);
			return null;
		}

		try {
			return createConsumer(destination);
			// System.out.println("succeed to create consumer from session [" + session + "] by destination ["
			// + destination + "].");
			// logger.debug("succeed to create consumer from session [" + session + "] by destination [" + destination
			// + "].");
		} catch (Throwable t) {
			logger.error("failed to create consumer from session [" + session + "] by destination [" + destination
					+ "]", t);
		}
		return null;
	}

	/**
	 * 根据连接信息构造监听consumer
	 * 
	 * @param destination
	 * @since v1.0
	 * @creator ChengKing
	 */
	private MessageConsumer createConsumer(Destination destination) {
		MessageConsumer consumer = null;
		try {
			consumer = session.createConsumer(destination);
			if (logger.isDebugEnabled()) {
				logger.debug("succeed to create consumer by session [" + session + "], from destination ["
						+ destination + "]");
			}
			// System.out.println("succeed to create consumer by session [" + session + "], from destination ["
			// + destination + "]");
		} catch (JMSException e) {
			logger.error("failed to create consumer by session [" + session + "], from destination [" + destination
					+ "]", e);
		}
		return consumer;
	}
}
