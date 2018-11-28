package com.bytes.fightr.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.model.User;

public class FighterService {

	private static Logger logger = LoggerFactory.getLogger(FighterService.class);
	
	/**
	 * key = fighterId:String
	 * value = {@code Fighter}
	 */
	private HashMap<String, Fighter> registeredFighters;
	
	
	FighterService() {
		this.registeredFighters = new HashMap<>();
	}
	
	

	/**
	 * Register a new fighter to the server
	 * @param fighter - the fighter to be registered
	 * @param sessionId - the session id
	 * @return the registered fighter
	 */
	public Fighter registerFighter(Fighter fighter) {
		
		String id = fighter.getId();
		String userId = fighter.getUserId();
		if (id == null || userId == null) {
			throw new IllegalArgumentException(
					"Unable to register fighter without a fighter ID and a user ID");
		}
		
		UserService userService = GameService.getInstance().getUserService();
		if (!userService.isRegistered(userId)) {
			throw new IllegalArgumentException(
					"Invalid user ID referenced: " + userId);
		}
		
		User user = userService.getRegisteredUser(userId);
		if (!user.getAvatarId().equals(fighter.getId())) {
			throw new IllegalArgumentException(
					"IDs don't match (fighter:user) " + id + ": " + user.getAvatarId());
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"Registering fighter (type:name:id)\n - %1$s:%2$s:%3$s", 
					fighter.getType().name(),
					fighter.getName(), 
					fighter.getId()
					));
		}
		
		if (!registeredFighters.containsKey(id)) {
			registeredFighters.put(fighter.getId(), fighter);
			logger.debug("Fighter registered.");
			
		} else {
			fighter.setState(State.None);
			logger.info(String.format(
				"Fighter %1$s already register. Updated status to %2$s", 
				id, State.None.name()));;
		}
		return fighter;
	}
	
	
	/**
	 * Unregister the fighter from the server
	 * @param fighterId - the fighterId to be unregistered
	 */
	public void unregisterFighter(String fighterId) {
		
		if (fighterId == null) {
			throw new IllegalArgumentException("Unable to unregister fighter without an ID");
		}
		
		if (registeredFighters.containsKey(fighterId)) {
			registeredFighters.remove(fighterId);
			logger.debug(String.format("Successfully unregistered fighter: %1$s", fighterId));
		}
	}
	

	/**
	 * Returns a modifiable the list of registered fighters
	 * @return the fighters
	 */
	public List<Fighter> getRegisteredFighters() {
		return new ArrayList<>(registeredFighters.values());
	}
	
	/**
	 * Get the registered fighter specified by the id
	 * @param fighterId - the fighter id
	 * @return the registered fighter
	 */
	public Fighter getRegisteredFighter(String fighterId) {

		if (fighterId != null && registeredFighters.containsKey(fighterId)) {
			return registeredFighters.get(fighterId);
		} else {
			return null;
		}
	}
	
	/**
	 * Get the list of {@code Fighter}s matching the specified ids
	 * @param fighterIds - the fighter ids
	 * @return the list of registered fighters
	 */
	public List<Fighter> getRegisteredFighter(List<String> fighterIds) {
		List<Fighter> fighters = new ArrayList<Fighter>(fighterIds.size());
		for (String fighterId : fighterIds) {
			fighters.add(getRegisteredFighter(fighterId));
		}
		return fighters;
	}
	
	/**
	 * Indicate if the fighter is registered
	 * @param fighterId - the fighter id
	 * @return true if the fighter is registered, false otherwise
	 */
	public boolean isRegistered(String fighterId) {
		if (fighterId != null) {
			return registeredFighters.containsKey(fighterId);
		}
		return false;
	}
	

	/**
	 * Request to debug all registered fighters and their sessions information
	 */
	public void debugAllRegisteredFighters() {
		
		if (!logger.isDebugEnabled()) { return; }
		
		StringBuilder sb = new StringBuilder();
		sb.append("Server Fighter Registry: \n");
		for (Fighter f : registeredFighters.values()) {
			sb.append(String.format(
				"(name:id:session) - %1$s:%2$s:%3$s\n", 
				f.getName(), f.getId(), 
				FightrServer.getInstance().getSessionRegistry().getSessionByUserId(f.getUserId())));
		}
		logger.debug(sb.toString());
	}
}
