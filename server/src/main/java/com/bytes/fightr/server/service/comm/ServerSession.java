package com.bytes.fightr.server.service.comm;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.MessageHandler.Partial;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * Null-pattern server session.
 */
public class ServerSession implements Session {
	
	public static final ServerSession SESSION_AI1 = new ServerSession("fighter_server_session_id1");
	public static final ServerSession SESSION_AI2 = new ServerSession("fighter_server_session_id2");
	
	private String id;

	public ServerSession(String id) {
		this.id = id;
	}
	
	@Override
	public WebSocketContainer getContainer() {
		return null;
	}

	@Override
	public void addMessageHandler(MessageHandler handler) throws IllegalStateException {
		
	}

	@Override
	public <T> void addMessageHandler(Class<T> clazz, Whole<T> handler) {
		
	}

	@Override
	public <T> void addMessageHandler(Class<T> clazz, Partial<T> handler) {	}

	@Override
	public Set<MessageHandler> getMessageHandlers() {
		return null;
	}

	@Override
	public void removeMessageHandler(MessageHandler handler) {}

	@Override
	public String getProtocolVersion() {
		return null;
	}

	@Override
	public String getNegotiatedSubprotocol() {
		return null;
	}

	@Override
	public List<Extension> getNegotiatedExtensions() {
		return null;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public long getMaxIdleTimeout() {
		return 0;
	}

	@Override
	public void setMaxIdleTimeout(long milliseconds) {
	}

	@Override
	public void setMaxBinaryMessageBufferSize(int length) {
	}

	@Override
	public int getMaxBinaryMessageBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxTextMessageBufferSize(int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxTextMessageBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Async getAsyncRemote() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Basic getBasicRemote() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void close(CloseReason closeReason) throws IOException {
	}

	@Override
	public URI getRequestURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<String>> getRequestParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getPathParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getUserProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Session> getOpenSessions() {
		return null;
	}

}
