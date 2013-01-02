package demo.server;

import java.util.Hashtable;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;

import demo.server.data.MyDataSource;

public class ManageData {

	public static void main(String[] args) {
		ManageData p = new ManageData();
		p.execute(p.init());
	}

	private Properties init() {
		Properties properties = new Properties();
		properties.setProperty("providerURL", "tcp://natty:61616");
		properties.setProperty("initialConnectionFactory", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		properties.setProperty("connectionFactory", "ConnectionFactory");
		properties.setProperty("somkiatTopic", "somkiat");
		return properties;
	}

	private void execute(Properties properties) {
		String providerURL = properties.getProperty("providerURL");
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, properties.getProperty("initialConnectionFactory"));
		env.put(Context.PROVIDER_URL, providerURL);

		try {
			InitialContext initialContext = new InitialContext(env);
			ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup(properties.getProperty("connectionFactory"));
			MyDataSource service = new MyDataSource(initialContext, connectionFactory, properties);
			System.out.println("Testing running on :" + providerURL);
			service.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
