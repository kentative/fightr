package com.bytes.fightr.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.server.logic.observer.UserObserver;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.data.model.User.Status;

public class UserService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	/**
	 * Map of registered users
	 * key = userId:String
	 * value = {@code User}
	 */
	private HashMap<String, User> registeredUsers;
	
	private static UserObserver userObserver;
	
	static{
		userObserver = new UserObserver();
	}
	
	UserService() {
		this.registeredUsers = new HashMap<>();
	}
	

	/**
	 * Register a new User to the server
	 * @param user - the user to be registered
	 * @param sessionId - the sessionId associated with the user
	 * @return the registered user
	 */
	public User registerUser(User user, String sessionId) {
		
		String id = user.getId();
		if (id == null) {
			throw new IllegalArgumentException("Unable to register user without an ID");
		}
		
		if (sessionId == null) {
			throw new IllegalArgumentException("Unable to register user without a session ID");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"Registering user (id:name:avartarId:session)\n - %1$s:%2$s:%3$s:%4$s", 
					user.getId(), 
					user.getDisplayName(),
					user.getAvatarId(), 
					sessionId));
		}
		
		// Register the userId with the specified sessionId
		FightrServer.getInstance().getSessionRegistry().registerUser(id, sessionId);
		if (!registeredUsers.containsKey(id)) {
			registeredUsers.put(id, user);
			user.setObserver(userObserver);
			user.setStatus(Status.Available);
			logger.debug("User registered.");
			
		} else {
			// TODO This is set to an error unless password matches. 
			logger.info(String.format("User %1$s already register.", id));
			return null;
		}
		return user;
	}
	
	/**
	 * Unregister the user from the server. Also unregister the associated fighter
	 * @param user - the user to be unregistered
	 */
	public void unregisterUser(User user) {
		
		String id = user.getId();
		if (id == null) {
			throw new IllegalArgumentException("Unable to unregister user without an ID");
		}
		
		if (registeredUsers.containsKey(id)) {
			registeredUsers.remove(id);
			user.setObserver(null);
			logger.debug(String.format("Successfully unregistered user: %1$s", id));
		}
		
		GameService.getInstance().getFighterService().unregisterFighter(user.getAvatarId());
	}
	

	/**
	 * Retrieve the User corresponding to the specified userId
	 * @param userId - the userId
	 * @return the user corresponding to the id
	 */
	public User getRegisteredUser(String userId) {
		return registeredUsers.get(userId);
	}
	
	/**
	 * Retrieve the User corresponding to the fighter
	 * @param fighter - the fighter
	 * @return the user corresponding to the fighter
	 */
	public User getRegisteredUser(Fighter fighter) {
		return registeredUsers.get(fighter.getUserId());
	}
	
	/**
	 * Indicate if the user is registered
	 * @param userId - the user id
	 * @return true if the user is registered, false otherwise
	 */
	public boolean isRegistered(String userId) {
		if (userId != null) {
			return registeredUsers.containsKey(userId);
		}
		return false;
	}
	
	/**
	 * Request to retrieve all registered users
	 * @return
	 */
	public List<User> getAllRegisteredUsers() {
		return new ArrayList<>(registeredUsers.values());
	}

	/**
	 * @return the userObserver
	 */
	public static UserObserver getUserObserver() {
		return userObserver;
	}
	
	/**
	 * Request to debug all registered users and their sessions information
	 */
	public void debugAllRegisteredUsers() {
		
		if (!logger.isDebugEnabled()) { return; }
		
		StringBuilder sb = new StringBuilder();
		sb.append("Server User Registry: \n");
		for (User u : registeredUsers.values()) {
			sb.append(String.format(
				"(name:id:session) - %1$s:%2$s:%3$s\n", 
				u.getDisplayName(), u.getId(), 
				FightrServer.getInstance().getSessionRegistry().getSessionByUserId(u.getId())));
		}
		logger.debug(sb.toString());
	}
}
