package com.bytes.fightr.server.service.comm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SessionRegistry {

	private static Logger logger = LoggerFactory.getLogger(SessionRegistry.class);
	
	/**
	 * Key  = Session id
	 * Value = Session
	 */
	private HashMap<String, Session> registry;
	
	/**
	 * BiMap of sessionIds to userIds and vice-versa. 
	 * Since both are unique string (UUID) and are mapped 1-1
	 * Key = sessionId:String OR userId:String
	 * Value = userId:String OR sessionId:String
	 * 
	 *  A pair (userId:sessionId, must be added twice)
	 */
	private HashMap<String, String> sessionIdMap;
	
	public SessionRegistry() {
		this.registry = new HashMap<String, Session>();
		this.sessionIdMap = new HashMap<String, String>();
		
		registry.put(ServerSession.SESSION_AI1.getId(), ServerSession.SESSION_AI1);
		registry.put(ServerSession.SESSION_AI2.getId(), ServerSession.SESSION_AI2);
	}
	
	public void register(Session session) {
		
		if (session == null || session.getId() == null) {
			throw new IllegalArgumentException("Invalid session");
		}
		registry.put(session.getId(), session);
	}
	
	/**
	 * Unregister session
	 * @param sessionId - the session id
	 * @return true if successfully unregistered
	 */
	public boolean unregister(String sessionId) {
		if (registry.containsKey(sessionId)) {
			registry.remove(sessionId);

			// Remove both entries from the bi-map
			sessionIdMap.remove(sessionIdMap.remove(sessionId));
			
			logger.debug("Successfully removed: " + sessionId);
			return true;
		} else {
			logger.warn("Session is not registered: " + sessionId);
		}
		return false;
	}
	
	public Session getSession(String key) {
		if (registry.containsKey(key)) {
			return registry.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * Get the list of sessions corresponding to the specified keys.
	 * Order is random.
	 * @param keys - the list of keys
	 * @return the list of sessions corresponding to the keys, order is random
	 */
	public List<Session> getSessions(Set<String> keys) {
		
		if (keys == null) {
			throw new IllegalArgumentException("Expects a non-null key");
		}
		
		List<Session> sessions = new ArrayList<>();
		for (String k : keys) {
			if (registry.containsKey(k)) {
				sessions.add(registry.get(k));
				logger.debug("Session found: " + k);
			} else {
				logger.debug("Session not found: " + k);
			}
		}
		return sessions;
	}

	public Collection<Session> getAllSessions() {
		return registry.values();
	}
	
	public Set<String> getAllSessionIds() {
		Set<String> ids = new HashSet<String>();
		for (Session session : registry.values()) {
			if (session != null) {
				ids.add(session.getId());
			}
		}
		return ids;
	}

	public boolean contains(String key) {
		return registry.containsKey(key);
	}

	/**
	 * Register the userId with the specified sessionId
	 * @param id - the user id
	 * @param sessionId - the session corresponding to the fighter
	 */
	public void registerUser(String id, String sessionId) {
		if (id == null || sessionId == null) {
			throw new IllegalArgumentException("UserId and SessionId cannot be null");
		}
		
		if (!registry.containsKey(sessionId)) {
			throw new IllegalArgumentException("Specified session Id is invalid: " + sessionId);
		}
		
		if (sessionIdMap.containsKey(id)) {
			logger.warn(String.format(
					"Replacing previous sessionId for user (user:new sessionId) - %1$s:%2$s", 
					id, 
					sessionId));
		}
		
		// Add two entries for bi-directional lookup
		// Each element is a unique string
		sessionIdMap.put(sessionId, id);
		sessionIdMap.put(id, sessionId);
	}

	
	/**
	 * Get the list of session ids for the specified fighter Ids
	 * @param fighters - the list of fighter ids
	 * @return the list of session ids
	 */
	public Set<String> getSessionIds(List<String> fighterIds) {
		
		Set<String> sessionIds = new HashSet<String>();
		for (String f : fighterIds) {
			if (sessionIdMap.containsKey(f)) {
				sessionIds.add(getSessionByUserId(f));
				logger.debug(String.format("Session found for (fighterId:sessionId)\n %1$s:%2$s", f, getSessionByUserId(f)));
			} else {
				logger.warn("Session NOT found for fighter " + f);
			}
		}
		return sessionIds;
		
	}
	
	
	/**
	 * Get the session id for the specified user id
	 * @param id - the userId
	 * @return the session id
	 */
	public String getSessionByUserId(String userId) {
		return sessionIdMap.get(userId);
	}

	/**
	 * Retrieve the userId corresponding to the sessionId. Returns null if not found
	 * @param sessionId - the session id
	 * @return the corresponding user id
	 */
	public String getUserId(String sessionId) {
		if (sessionId != null && sessionIdMap.containsKey(sessionId)) { 
			return sessionIdMap.get(sessionId);
		}
		return null;
	}
}
