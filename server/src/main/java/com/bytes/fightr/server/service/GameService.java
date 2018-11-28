package com.bytes.fightr.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.action.FightResult;
import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.fightr.common.model.skill.SkillRegistry;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fightr.server.service.comm.ServerSession;
import com.bytes.fmk.data.model.User;
import com.bytes.gamr.model.avatar.Avatar.AvatarType;
import com.bytes.gamr.model.avatar.skill.Skill;

public class GameService {

	private static Logger logger = LoggerFactory.getLogger(GameService.class);
	
	private static GameService instance; 
	
	private MatchService matchService;
	private FighterService fighterService;
	private UserService userService;
	
	public static GameService getInstance() {
		if (instance == null) {
			instance = new GameService();
		}
		return instance;
	}
	
	/**
	 * private constructor for singleton instance
	 */
	private GameService() {
		this.matchService = new MatchService();
		this.fighterService = new FighterService();
		this.userService = new UserService();
	}
	
	/**
	 * Register AI Fighters
	 */
	public void registerAI() {
		
		logger.info("Registering AI for first time use");
		
		// Hanzo
		User hanzo = new User("Hanzo").setId("hanzo_@ai.com");
		Fighter hanzoFighter = new Fighter(AvatarType.AI, "Hanzo").setId("hanzo_ai");
		hanzoFighter.linkUser(hanzo);
		
		// Genji
		User genji = new User("Genji").setId("genji@ai.com");
		Fighter genjiFighter = new Fighter(AvatarType.AI, "Genji").setId("genji_ai");
		genjiFighter.linkUser(genji);
		
		userService.registerUser(hanzo, ServerSession.SESSION_AI1.getId());
		fighterService.registerFighter(hanzoFighter);
		userService.registerUser(genji, ServerSession.SESSION_AI2.getId());
		fighterService.registerFighter(genjiFighter);
		
	}
	
	/**
	 * Queue the action to synch action inputs from all fighter in the match
	 * @param action - the action to queue
	 * @return true if the queue is ready
	 */
	public boolean queueAction(FightAction action, Match match) {

		if (!action.isValid()) {
			throw new IllegalArgumentException("Invalid Fighter Action");
		}

		return match.queueAction(action);
	}

	/**
	 * Execute the action based on the specified parameters
	 * @return
	 */
	public FightResult execute(Match match) {

		logger.info("Executing action in match: " + match.getId());

		// Result for this round of actions 
		FightResult result = new FightResult();
		
		FightAction action = match.pollAction();
		while (action != null) {
			result.getActions().add(action);
			perform(action);
			action = match.pollAction();
		}

		result.setFighters(fighterService.getRegisteredFighter(match.getFighters()));
		return result;
	}


	/**
	 * 
	 * @param action
	 */
	private void perform(FightAction action) {

		SkillRegistry skillR =  SkillRegistry.getInstance();
		logger.info("Performing action: " + action.getSkill());
		Skill<Fighter> skill = skillR.getSkill(action.getSkill());
		
		Fighter sourceFighter, targetFighter; 
		sourceFighter = fighterService.getRegisteredFighter(action.getSource());
		for (String target : action.getTargets()) {
			// Damage and effects are calculated and applied here:
			targetFighter = fighterService.getRegisteredFighter(target);
			skill.activate(sourceFighter, targetFighter);
		}
	}

	/**
	 * Request to generate a random AI {@code FightAction}
	 * @param fighter - the AI fighter
	 * @param sourceId - the AI target id
	 * @return
	 */
	public FightAction generateAIAction(String aiId, String sourceId) {

		int random = ThreadLocalRandom.current().nextInt(0, 6);
		FightAction action = new FightAction();
		action.setSkill(FighterSkill.Id.values()[random]);
		action.setSource(aiId);
		action.setTargets(sourceId);
		
		return action;
	}
	
	/**
	 * Request to update the user's status corresponding to the fighter's id
	 * @param fighterId - the fighter id
	 * @param status - the status to set
	 */
	public void updateUserStatus(String fighterId, User.Status status) {
		userService.getRegisteredUser(
				fighterService.getRegisteredFighter(fighterId)).setStatus(status);
	}

	public void reset() {
		instance = new GameService();
	}

	public FightResult initiateAction(Match match, FightAction action) {

		logger.info("Initiating action in match: " + match.getId());

		// Result for this round of actions 
		FightResult result = new FightResult();
		result.getActions().add(action);
		final Fighter fighter = fighterService.getRegisteredFighter(action.getSource());
		fighter.setState(State.Attacking);
		fighter.setActiveSkill(action.getSkill());
		
		FighterSkill skill = SkillRegistry.getInstance().getSkill(action.getSkill());
		final long activationTime = skill.getTurnCost();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(activationTime);
					fighter.setState(State.None);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
			
		}).start();
		
		result.setFighters(fighterService.getRegisteredFighter(match.getFighters()));
		
		return result;
	}
	
	/**
	 * Get the user corresponding to the specified fighter id.
	 * @param fighterId - the fighter id
	 * @return the User
	 */
	public User getUserByFighterId(String fighterId) {
		Fighter registeredFighter = fighterService.getRegisteredFighter(fighterId);
		if (registeredFighter != null) {
			return userService.getRegisteredUser(registeredFighter.getUserId());
			
		}
		return null;
	}
	
	/**
	 * Get the list of Users corresponding to the list of fighter ids. 
	 * Order is not guaranteed.
	 * @param fighterIds - the list of fighter ids
	 */
	public List<User> getUsersByFighterIds(List<String> fighterIds) {
		List<User> users = new ArrayList<User>(fighterIds.size());
		FighterService fighterService = GameService.getInstance().getFighterService();
		for (String fighterId : fighterIds) {
			Fighter registeredFighter = fighterService.getRegisteredFighter(fighterId);
			users.add(userService.getRegisteredUser(registeredFighter.getUserId()));
		}
		return users;
	}

	/**
	 * @return the matchService
	 */
	public MatchService getMatchService() {
		return matchService;
	}

	/**
	 * @return the fighterService
	 */
	public FighterService getFighterService() {
		return fighterService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * Request to retrieve the session id corresponding to the fighter id
	 * @param fighterId - the fighter id
	 * @return the session id if available, null otherwise
	 */
	public String getSessionIdByFighterId(String fighterId) {
		User user = getUserByFighterId(fighterId);
		if (user != null) {
			return FightrServer.getInstance().getSessionRegistry().getSessionByUserId(user.getId());
		}
		return null;
	}
}
