package com.bytes.fightr.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is the base class for all connection-type tests.
 * Test pattern is as follow:
 * 	1. define connection parameters
 *  2. define message parameters
 *  3. define the sendTask (Runnable) to send test-specific entity to the server
 * @author Kent
 *
 */
public abstract class AbstractConnectionTest {

	private Logger logger = LoggerFactory.getLogger(AbstractConnectionTest.class);
	
	// Connection Parameters
	protected WebSocketClient client;    // The Web Socket client, no need to override this
	protected String location;           // The Web Socket server address, fully qualified 
	protected int handshakeWait;         // The handshake wait time before sending the first message (millis)
	
	// Message Parameters
	protected String message;     		 // The default message, override as needed
	
	// Send Task Parameters
	protected Runnable sendTask;         // Define the test-specific send task
	
	protected FighterClientSim clientSimulator;
	
	public AbstractConnectionTest() {
		clientSimulator = new FighterClientSim();
	}
	
	/**
	 * Sends n messages to the server and check to see if the server receives all of them.
	 * 	 * 
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 */
	@Test 
	public void ConnectClient() throws URISyntaxException, InterruptedException {
		
		URI address = new URI(location);		
		client = new WebSocketClient(address, new Draft_17()) {		
			
			@Override
			public void onOpen(ServerHandshake handshake) {		
				
			}
			
			@Override
			public void onMessage(String msg) {
				try {
					clientSimulator.process(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}		

			@Override
			public void onError(Exception ex) {
				
				logger.info(ex.getMessage());				
			}
			
			@Override
			public void onClose(int code, String reason, boolean remote) {
				
			}
		};		
		
		client.connect();
		clientSimulator.setClient(client);
		// Allow time to connect
		Thread.sleep(handshakeWait);
		if (client.getConnection().isOpen()) {
			logger.info("Connection is open. Executing sendTask.");
			if (sendTask != null) {
				sendTask.run();
			}		
		} else {
			logger.info("Connection is not open.");
		}
		
	}
	
}
