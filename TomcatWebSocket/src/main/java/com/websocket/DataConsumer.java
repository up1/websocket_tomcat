package com.websocket;

import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class DataConsumer {

	private Connection connection = null;
	private Session session = null;
	private MessageConsumer consumer = null;
	private static DataConsumer dataConsumer = null;
	private Set<MyWebSocketServlet.TheWebSocket> connections = new CopyOnWriteArraySet<MyWebSocketServlet.TheWebSocket>();

	private DataConsumer() {
		init();
	}

	private void init() {
		String providerURL = "tcp://natty:61616";

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		env.put(Context.PROVIDER_URL, providerURL);
		env.put("topic.somkiat", "somkiat");

		try {
			InitialContext initialContext = new InitialContext(env);
			ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			Connection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination destination = (Destination) initialContext.lookup("somkiat");
			MessageConsumer messageConsumer = session.createConsumer(destination);
			messageConsumer.setMessageListener(new MessageListener() {
				public void onMessage(Message message) {
					try {
						TextMessage textMessage = ((TextMessage) message);
						try {
							for (MyWebSocketServlet.TheWebSocket connection : connections) {
								connection.broadcast(textMessage.getText());
							}
						} catch (JMSException e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});

			connection.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static DataConsumer getInstance() throws Exception {
		try {
			if (dataConsumer == null) {
				dataConsumer = new DataConsumer();
			}
			return dataConsumer;
		} catch (Exception e) {
			dataConsumer.shutdown();
			throw new Exception("Unable to create JMSMessageConsumer : " + e.getMessage());
		}
	}

	public void addConnection(MyWebSocketServlet.TheWebSocket connection) {
		connections.add(connection);
	}

	public void removeConnection(MyWebSocketServlet.TheWebSocket connection) {
		connections.remove(connection);
	}

	private void shutdown() {
		try {
			if (consumer != null)
				consumer.close();
		} catch (Exception e) {
			System.out.println("Could not close producer " + e.getMessage());
		}
		try {
			if (session != null)
				session.close();
		} catch (Exception e) {
			System.out.println("Could not close session " + e.getMessage());
		}
		try {
			if (connection != null)
				connection.close();
		} catch (Exception e) {
			System.out.println("Could not close connection " + e.getMessage());
		}
	}

}
