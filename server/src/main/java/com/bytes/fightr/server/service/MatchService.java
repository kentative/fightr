package com.bytes.fightr.server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.Match.State;
import com.bytes.fightr.common.model.MatchBuilder;
import com.bytes.fightr.common.model.Team;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.logic.observer.MatchObserver;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fightr.server.service.comm.SessionRegistry;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.data.model.User.Status;
import com.bytes.fmk.observer.Context;
import com.bytes.fmk.observer.DestinationType;
import com.bytes.gamr.model.avatar.Avatar.AvatarType;

public class MatchService {

	private static Logger logger = LoggerFactory.getLogger(MatchService.class);

	/**
	 * Map of registered fighters to their corresponding match
	 * key = FighterId:String
	 * value = {@code Match}
	 */
	private HashMap<String, Match> registeredMatches;
	
	MatchService() {
		this.registeredMatches = new HashMap<>();
	}

	/**
	 * Prepare the match to be started. See {@code Match} for details
	 * 
	 * @param match - the match to registered
	 * @param sessionId - the source session id 
	 * @return the registered match
	 */
	public Match registerMatch(Match match, String sessionId) {
		
		logger.debug("Registering match: " + match.getId());
		
		// Create the notification context
		Context context = new Context();
		context.setDestinationType(DestinationType.Custom);
		context.setSource(sessionId);
		
		// Verify that all fighters in the match are registered
		if (!validateFighters(match))  {
			match.setState(State.Invalid, context);
			return match;
			
		} else {
			
			// Add observer to trigger notifications 
			SessionRegistry sessionRegistry = FightrServer.getInstance().getSessionRegistry();
			Set<String> sessionIds = sessionRegistry.getSessionIds(match.getFighters());
			MatchObserver observer = new MatchObserver();
			observer.addSubscribers(sessionIds);
			match.setObserver(observer);
			match.setState(Match.State.Searching, context);
		}
		
		// register match for all fighters and update user status
		GameService gameService = GameService.getInstance();
		List<String> fighterIds = match.getFighters();
		for (String id : fighterIds) {

			// ignore AI
			Fighter fighter = GameService.getInstance().getFighterService().getRegisteredFighter(id);
			if (fighter.getType() == AvatarType.AI) continue;
			
			registeredMatches.put(id, match);
			gameService.updateUserStatus(id, User.Status.Searching);
		}
		
		return match;
	}
	
	
	/**
	 * Support the following format
	 * team1 versus team2, where team1 and team2 = {1..n}
	 *  - Must have at least 1 fighter on each team
	 *  - All fighters must be registered
	 *  - All user corresponding to the fighters must be registered
	 *  - 
	 * @param match - the match to be verified
	 * @return true if the match is valid
	 */
	private boolean validateFighters(Match match) {
		
		List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
		List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();
		if (team1Fighters.isEmpty() || team2Fighters.isEmpty()) {
			return false;
		}
		
		// Fighters and Users must be registered and available
		List<String> matchFighters = match.getFighters();
		GameService gameService = GameService.getInstance();
		for(String id : matchFighters) {
			Fighter fighter = gameService.getFighterService().getRegisteredFighter(id);
			if (fighter == null) {
				return false;
			} else {
				User user = gameService.getUserService().getRegisteredUser(fighter.getUserId());
				if (user == null || user.getStatus() != Status.Available) {
					logger.warn("Request to register match, but user is not available: " + user.getDisplayName());
					if (logger.isDebugEnabled()) {
						logger.debug("Match : " + PayloadUtil.getGson().toJson(match));
						logger.debug(String.format("user - %1$s fighter - %2$s",
								PayloadUtil.getGson().toJson(user),
								PayloadUtil.getGson().toJson(fighter)));
					}
					return false; 
				}
			}
		}
		return true;
	}
	
	/**
	 * Unregister the association between the fighter and the match. 
	 * If all fighter unregistered, the match is effectively removed from the server.
	 * @param fighterId - the fighterId 
	 * @return the removed match
	 */
	public Match unregister(String fighterId) {
		
		if (fighterId == null) {
			throw new IllegalArgumentException("Unable to unregister match with null figherId");
		}
		
		if (registeredMatches.containsKey(fighterId)) {
			Match removed = registeredMatches.remove(fighterId);
			
			User user = GameService.getInstance().getUserByFighterId(fighterId);
			user.setStatus(Status.Available);
			logger.debug(String.format("Successfully unregistered match for fighter: %1$s", fighterId));
			return removed;
		}
		return null;
	}

	/**
	 * Request to get the match registered to the fighter.
	 * @param fighterId - the fighter id
	 * @return the match registered to the fighter, null if not found
	 */
	public Match getRegisteredMatch(String fighterId) {
		if (fighterId == null) {
			throw new IllegalArgumentException("FighterId cannot be null");
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
				"Found match for Fighter %1$s: %2$s", fighterId, registeredMatches.get(fighterId)));
		}
		
		if (registeredMatches.containsKey(fighterId)) {
			return registeredMatches.get(fighterId);
		}
		return null;
	}
	

	/**
	 * Request to join a match. Fighter must already be registered.
	 * 
	 * @param fighterId 
	 * @return the joined match 
	 */
	public Match joinMatch(String fighterId) {

		FighterService fighterService = GameService.getInstance().getFighterService();
		if (!fighterService.isRegistered(fighterId)) {
			throw new IllegalStateException("Fighter is not registered");
		}
		
		Match match = null; 
		boolean joined = false;
		Fighter fighter = fighterService.getRegisteredFighter(fighterId);
		while (!joined) {
			
			match = getAvailableMatch(fighter.getUserId());
			Team team = match.getJoinableTeam();
			joined = match.join(fighterId, team);
			
			if (!joined) {
				logger.debug("Unable to join available match, trying again");
			}
		}
		
		GameService.getInstance().updateUserStatus(fighterId, User.Status.Searching);
		registeredMatches.put(fighterId, match);
		return match;
	}
	
	/**
	 * Request to leave a match. The fighter will not be unregistered to be eligible for 
	 * opponent searches.
	 * @param fighterId - the fighter id
	 * @return
	 */
	public Match leaveMatch(String fighterId) {
		
		MatchService matchService = GameService.getInstance().getMatchService();
		GameService.getInstance().updateUserStatus(fighterId, User.Status.Available);
		if (!matchService.isRegistered(fighterId)) {
			logger.warn("Match is not found for fighter: " + fighterId);
			return null;
		}
		return unregister(fighterId);
	}
	
	/**
	 * Get a joinable match from the server. If not available, a new Match will be created
	 * @param userId - the user id
	 * @return a joinable match
	 */
	private Match getAvailableMatch(String userId) {
		
		for (Match match : registeredMatches.values()) {
			if (match.isJoinable()) {
				return match;
			}
		}

		// If no available match, create a new one
		return new MatchBuilder().create(userId);
	}
	
	/**
	 * Returns true if this match can be started
	 * @param match - the match to check
	 * @return true if all fighters are ready
	 */
	public boolean isStartable(Match match) {
		// TODO
//		for (Team t : match.getTeams()) {
//			for (String f : t.getFighters()) {
//				Fighter fighter = getRegisteredFighter(f);
//				
//				if (getRegisteredUser(fighter).getStatus() != User.Status.Ready && fighter.getType() != AvatarType.AI) {
//					logger.info(String.format(
//							"Fighter: %1$s:%2$s is NOT ready", fighter.getName(), fighter.getId()));
//					return false;
//				}
//			}
//		}
		return false;
	}
	
	/**
	 * Indicate if the match is registered
	 * @param fighterId - the fighter id
	 * @return true if the match is registered, false otherwise
	 */
	public boolean isRegistered(String fighterId) {
		if (fighterId != null) {
			return registeredMatches.containsKey(fighterId);
		}
		return false;
	}
	

	/**
	 * Get the list of session ids from all fighters in the specified match
	 * @param match - the match containing fighters
	 * @return - the list of sessions id corresponding to the fighters in the match
	 */
	public List<String> getSessions(Match match) {
		List<String> fighterIds = match.getFighters();
		List<Fighter> fighters = GameService.getInstance().getFighterService().getRegisteredFighter(fighterIds);
		List<String> sessionIds = new ArrayList<String>();
		for (Fighter fighter : fighters) {
			sessionIds.add(FightrServer.getInstance().getSessionRegistry().getSessionByUserId(fighter.getUserId()));
		}
		return sessionIds;
	}

	/**
	 * 
	 * @param fighterId
	 */
	public void updateUserStatus(String fighterId, User.Status status) {
		
		Match match = getRegisteredMatch(fighterId);
		if (match == null) {
			throw new IllegalStateException(fighterId + " is in match, but the match is not registered.");
		}

		switch (status) {
		case Available:
			break;
			
		case Battle:
			break;
			
		case New:
			break;
			
		case Ready:
			setUserStatusToReady(match, fighterId);
			break;
			
		case Searching:
			setUserStatusToSearching(match, fighterId);
			break;
			
		default:
			break;
		
		}
	}
	
	private void setUserStatusToReady(Match match, String fighterId) {
		boolean isMatchReady = true;
		List<String> fighters = match.getFighters();
		for (String id : fighters) {
			User userInMatch = GameService.getInstance().getUserByFighterId(id);
			Fighter fighterInMatch = GameService.getInstance().getFighterService().getRegisteredFighter(id);
			if (userInMatch.getStatus() != Status.Ready && fighterInMatch.getType() != AvatarType.AI) {
				isMatchReady = false;
			}
		}
		
		if (isMatchReady) {
			match.setState(Match.State.Ready, createNotificationContext(fighterId));
		}
	}
	
	private void setUserStatusToSearching(Match match, String fighterId) {
		
		if (match.getState() == Match.State.Ready) {
			match.setState(Match.State.Searching, createNotificationContext(fighterId));
		}
	}
	
	/**
	 * Request to create the notification context for Match notifications.
	 * @param fighterId - the fighter id
	 * @return the notification context to be used with {@link MatchObserver}
	 */
	private Context createNotificationContext(String fighterId) {
		// Create the notification context
		Context context = new Context();
		context.setDestinationType(DestinationType.Custom);
		context.setSource(GameService.getInstance().getSessionIdByFighterId(fighterId));
		return context;
	}
}
