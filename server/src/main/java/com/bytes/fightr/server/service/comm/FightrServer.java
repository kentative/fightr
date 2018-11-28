package com.bytes.fightr.server.service.comm;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.SessionException;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.logic.processor.FighterPayloadProcessorRegistry;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.data.model.User.Status;
import com.bytes.fmk.observer.DestinationType;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.PayloadProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@ServerEndpoint(value = "/fightr", configurator = FighterServerConfigurator.class)
public class FightrServer {

	private static final String VERSION = "L-2017.06.19";
	
	private static FightrServer instance;

	private static Logger logger = LoggerFactory.getLogger(FightrServer.class);

	private SessionRegistry sessionRegistry;
	
	private FighterPayloadProcessorRegistry processorFactory;

	private Gson gson;

	private FightrServer() {
		logger.info("Fightr WebSocketServer created");
		this.sessionRegistry = new SessionRegistry();
		this.gson = new Gson();
		this.processorFactory = new FighterPayloadProcessorRegistry();
	}

	/**
	 * The Singleton instance
	 * 
	 * @return
	 */
	public static FightrServer getInstance() {
		if (instance == null) {
			instance = new FightrServer();
			GameService.getInstance().registerAI();
		}
		return instance;
	}

	
	/**
	 * Process {@code payload} from the client. 
	 * Any exception found will be h
	 * 
	 * @param message
	 * @param session
	 * @throws IOException
	 * @throws InterruptedException
	 * @see https://javaee-spec.java.net/nonav/javadocs/javax/websocket/OnMessage.html
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.info("\n\n----==== Received message from: " + session.getId() + " ====----");
		
		Payload<String> responsePayload;
		FighterPayload payload = null;
		
		try {
			// 1. Parse incoming payload
			payload = gson.fromJson(message, PayloadUtil.getClass(message));
			payload.setSourceId(session.getId());
			payload.addDestination(session.getId());
		} catch (JsonSyntaxException e) {
			responsePayload = new FighterPayload(Payload.NOTIFY, DataType.String, e.getMessage());
			responsePayload.addDestination(session.getId());
			logger.error("Unable to parse Json payload: " + message, e);
		}

		try {
			// 2. Validate payload
			String processorName = processorFactory.getName(payload.getType(), payload.getDataType());
			PayloadProcessor<String> processor = processorFactory.getProcessor(processorName);
					
			if (!processor.validate(payload)) {
				String errorMsg = String.format("Unable to validate request: %1$s", payload.getType());
				responsePayload = new FighterPayload(Payload.NOTIFY, DataType.String, errorMsg);
				logger.error(errorMsg);
			} else {
				
				// 3. Process payload
				responsePayload = processor.process(payload);
			}
			// 4. Send response payload
			if (responsePayload != null) {
				send(responsePayload);
			}
			
		} catch (Exception e) {
			logger.error("Unable to process payload",e);
			send(new FighterPayload(Payload.NOTIFY, DataType.String, e.getMessage()));
		}
	}

	/**
	 * This method level annotation can be used to decorate a Java method that
	 * wishes to be called when a new web socket session is open. The method may
	 * only take the following parameters: optional Session parameter optional
	 * EndpointConfig parameter Zero to n String parameters annotated with the
	 * PathParam annotation.
	 * 
	 * @param session
	 * @param config
	 */
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		logger.info(String.format("\n\n------======== Session %1$s connected ========--------\n", session.getId()));
		
		if (sessionRegistry.contains(session.getId())) {
			logger.info("Removed existing session: " + session.getId());
			sessionRegistry.unregister(session.getId());
		}

		sessionRegistry.register(session);
		send(createWelcomePayload(session));
	}

	/**
	 * Create a welcome message payload
	 * @param session the connecting session
	 * @return the payload with a welcome message
	 */
	private FighterPayload createWelcomePayload(Session session) {
		FighterPayload payload = new FighterPayload(
				Payload.NOTIFY, DataType.String,
				String.format("Welcome to Fightr %1$s.\nYour session id: %2$s", VERSION, session.getId()));
			payload.setSourceId(session.getId());
			payload.addDestination(session.getId());
		return payload;
	}

	/**
	 * This method level annotation can be used to decorate a Java method that
	 * wishes to be called when a web socket session is closing. The method may
	 * only take the following parameters:- optional Session parameter optional
	 * CloseReason parameter Zero to n String parameters annotated with the
	 * PathParam annotation.
	 * 
	 * @see #Endpoint.onClose()
	 * @param session
	 * @param config
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {

		String sessionId = session.getId();
		String userId = sessionRegistry.getUserId(sessionId);
		if (userId != null) {
			logger.info("User offline: " + userId);
			User registeredUser = GameService.getInstance().getUserService().getRegisteredUser(userId);
			registeredUser.setStatus(Status.Offline);
		}
		
		if (sessionRegistry.unregister(sessionId)) {
			logger.info(
					"Connection closed. " + closeReason.getReasonPhrase() + 
					". successfully removed session  " + sessionId);
		} else {
			logger.info("Connection closed: " + closeReason.getReasonPhrase());
		}
	}

	 /**
     * Developers may implement this method when the web socket session
     * creates some kind of error that is not modeled in the web socket protocol. This may for example
     * be a notification that an incoming message is too big to handle, or that the incoming message could not be encoded.
     *
     * <p>There are a number of categories of exception that this method is (currently) defined to handle:
     * <ul>
     * <li>connection problems, for example, a socket failure that occurs before
     * the web socket connection can be formally closed. These are modeled as 
     * {@link SessionException}s</li>
     * <li>runtime errors thrown by developer created message handlers calls.</li>
     * <li>conversion errors encoding incoming messages before any message handler has been called. These
     * are modeled as {@link DecodeException}s</li>
     * </ul>
     *
     * @param session the session in use when the error occurs.
     * @param thr     the throwable representing the problem.
     */
    public void onError(Session session, Throwable thr) {
    	logger.error("Error encounter for session " + session.getId() + " - " + thr.getMessage());
    }
	
	/**
	 * Request to send the {@code Payload} to the destination encapsulated in the destination list
	 * @param payload - the payload to be sent
	 */
	public void send(Payload<String> payload) {

		if (payload == null) {
			logger.warn("request to send payload but payload is null");
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("---=== Source:%1$s Destination:%2$s ", 
					payload.getSourceId(), gson.toJson(payload.getDestinationIds())));
		}
		
		List<Session> sessions = sessionRegistry.getSessions(payload.getDestinationIds());
		for (Session s : sessions) {
			
			String json = null;
			try {
				if (s.isOpen()) {
					json = gson.toJson(payload);
					s.getBasicRemote().sendText(json);
					logger.debug("Payload Id: " + payload.getId());
					// This is verbose
					logger.debug("Sending data: " + json);
					
				} else {
					logger.warn("Session is not open: " + s.getId());
				}
			} catch (Exception e) {
				logger.error("unable to send payload: " + json, e);
			}
		}
	}

	/**
	 * Request to send the specified {@code Payload} the specified destination type
	 * @param payload - the payload to be sent
	 * @param type - the destination type
	 * @see DestinationType
	 */
	public void send(Payload<String> payload, DestinationType type) {
		if (payload == null || payload.getSourceId() == null) {
			throw new IllegalArgumentException("Payload source session id must not be null.");
		}
		
		payload.addDestinations(getDestinationIds(payload.getSourceId(), type));
		send(payload);
	}
	
	/**
	 * Request to generate the destination id list based on {@link DestinationType}
	 * @param sessionId - the payload source session id
	 * @param type - the destination type
	 */
	private Set<String> getDestinationIds(String sessionId, DestinationType type) {
		
		Set<String> destinationIds;
		switch (type) {
		case All:
			// Send notification to all others connected users
			destinationIds = FightrServer.getInstance().getSessionRegistry().getAllSessionIds();
			destinationIds.remove(ServerSession.SESSION_AI1.getId());
			destinationIds.remove(ServerSession.SESSION_AI2.getId());
			break;
			
		case Other:
			// Send notification to all others connected users
			destinationIds = FightrServer.getInstance().getSessionRegistry().getAllSessionIds();
			destinationIds.remove(sessionId);
			destinationIds.remove(ServerSession.SESSION_AI1.getId());
			destinationIds.remove(ServerSession.SESSION_AI2.getId());
			break;
			
		case Source:
			destinationIds = new HashSet<String>();
			destinationIds.add(sessionId);
			break;
			
		default:
			throw new IllegalArgumentException("Destination type is not supported: " + type);
		}
		return destinationIds;
	}

	/**
	 * @return the sessionRegistry
	 */
	public SessionRegistry getSessionRegistry() {
		return sessionRegistry;
	}

	public void reset() {
		sessionRegistry = new SessionRegistry();
	}

}
