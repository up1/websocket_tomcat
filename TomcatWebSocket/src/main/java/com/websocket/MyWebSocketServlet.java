package com.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.UnavailableException;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class MyWebSocketServlet extends WebSocketServlet {
	private static final Logger logger = LoggerFactory.getLogger(MyWebSocketServlet.class);
	private DataConsumer dataConsumer;
	
	public void init() throws UnavailableException {
		try {
			super.init();
			dataConsumer = DataConsumer.getInstance();
		} catch (Exception e) {
			throw new UnavailableException(e.getMessage());
		}
	}
	
	@Override
	public StreamInbound createWebSocketInbound(String protocol) {
		logger.debug("Call to createWebSocketInbound( \"{} \" )", protocol);
		TheWebSocket object = new TheWebSocket();
		dataConsumer.addConnection(object);
		return object;
	}


	class TheWebSocket extends MessageInbound {
		private final Logger logger = LoggerFactory.getLogger(TheWebSocket.class);
		private WsOutbound outbound;

		public TheWebSocket() {
			super();
		}

		@Override
		public void onOpen(WsOutbound outbound) {
			logger.debug("Received onOpen( )");
			this.outbound = outbound;
		}

		@Override
		protected void onClose(int status) {
			logger.debug("Received onClose( )");
			dataConsumer.removeConnection(this);
		}
		
		String[] output = {"1:xxxx:", "2:xxxx:", "3:xxxx:", "4:xxxx:"};

		@Override
		protected void onTextMessage(CharBuffer buffer) throws IOException {
			String data = buffer.toString();
			logger.debug("Received onTextMessage( \"{}\" )", data);
			String message = new StringBuffer(data).reverse().toString();
			try {
				// outbound.writeTextMessage(CharBuffer.wrap(message.toCharArray()));
				//broadcast( output[ new Random().nextInt(3) ] +  message);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			} 
		}

		@Override
		protected void onBinaryMessage(ByteBuffer buffer) throws IOException {
			logger.debug("Received unsupported onBinaryMessage( )");
		}

		protected void broadcast(String message) {
			CharBuffer buffer = CharBuffer.wrap(message);
			try {
				getWsOutbound().writeTextMessage(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
