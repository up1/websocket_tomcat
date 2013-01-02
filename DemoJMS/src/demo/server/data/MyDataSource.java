package demo.server.data;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MyDataSource {

	public static Session _session;
	public static MessageProducer _messageProducer;

	private Properties _properties;
	private InitialContext _initialContext;
	private ConnectionFactory _connectionFactory;
	private Destination _destination;

	private static final String TEST_TOPIC = "somkiatTopic";

	public MyDataSource(InitialContext initialContext, ConnectionFactory connectionFactory, Properties properties) {
		_connectionFactory = connectionFactory;
		_properties = properties;
		_initialContext = initialContext;
	}

	public void run() {
		try {
			connect();
			while (true) {
				_updateData();
				Thread.sleep(100L);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void _updateData() {
		String body = mockData();
		try {
			TextMessage message = _session.createTextMessage(body);
			_messageProducer.send(message, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, 0L);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private String mockData() {
		String[] dataList = {"AAA", "BBB", "CCC", "DDD"};
		Random random = new Random();
		return random.nextInt(10) + ":" + dataList[random.nextInt(3)]+ ":" + dataList[random.nextInt(3)];
	}

	private void connect() throws InterruptedException {
		try {
			final Connection connection = _connectionFactory.createConnection();
			connection.setExceptionListener(new ExceptionListener() {
				@Override
				public void onException(JMSException exception) {
					try {
						connection.close();
					} catch (JMSException e) {
						e.printStackTrace();
					}

					try {
						Thread.sleep(TimeUnit.SECONDS.toMillis(10));
						connect();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});

			_session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			if (_destination == null) {
				try {
					_destination = (Destination) _initialContext.lookup(_properties.getProperty(TEST_TOPIC));
				} catch (NamingException e) {
					e.printStackTrace();
				}
				if (_destination == null) {
					_destination = _session.createTopic(_properties.getProperty(TEST_TOPIC));
				}
			}
			_messageProducer = _session.createProducer(_destination);
		} catch (JMSException e) {
			Thread.sleep(TimeUnit.SECONDS.toMillis(10));
			connect();
		}
	}
}