package demo.server.monitor;

import java.util.Hashtable;

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

public class MessageMonitor {
	public static void main(String[] args) {
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
						System.out.println(((TextMessage) message).getText());
					} catch (JMSException e) {
						throw new RuntimeException(e);
					}
				}
			});

			connection.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
