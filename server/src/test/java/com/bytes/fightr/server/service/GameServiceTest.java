package com.bytes.fightr.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.MatchBuilder;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarAttribute.Type;

public class GameServiceTest extends BaseServiceTest {

	@Test
	public void registerFighterTest() {
		
		service.getUserService().registerUser(u1, session1.getId());
		service.getUserService().registerUser(u2, session2.getId());
		service.getUserService().registerUser(u3, session3.getId());
		
		service.getFighterService().registerFighter(f1);
		service.getFighterService().registerFighter(f2);
		service.getFighterService().registerFighter(f3);
		
		List<Fighter> fighters = service.getFighterService().getRegisteredFighters();
		Assert.assertTrue(fighters.contains(f1));
		Assert.assertTrue(fighters.contains(f2));
		Assert.assertTrue(fighters.contains(f3));
	}
	
	@Test
	public void executeTest() {
		
		service.getUserService().registerUser(u1, session1.getId());
		service.getUserService().registerUser(u2, session2.getId());
		
		service.getFighterService().registerFighter(f1);
		service.getFighterService().registerFighter(f2);
		
		MatchBuilder matchBuilder = new MatchBuilder();
		Match m = matchBuilder
				.addToTeam1(f1.getId())
				.addToTeam2(f2.getId())
				.create(u1.getId());
			
		service.getMatchService().registerMatch(m, session1.getId());
		
		FightAction action1 = new FightAction();
		action1.setSkill(FighterSkill.Id.Attack);
		action1.setSource(f1.getId());
		action1.setTargets(f2.getId());
		
		FightAction action2 = new FightAction(FighterSkill.Id.Attack);
		action2.setSource(f2.getId());
		action2.setTargets(f1.getId());
		
		
		m.queueAction(action1);
		m.queueAction(action2);
		service.execute(m);
		AvatarAttribute health1 = f1.getAttributes(Type.HEALTH);
		Assert.assertEquals(4, health1.getValue());
		
		AvatarAttribute health2 = f2.getAttributes(Type.HEALTH);
		Assert.assertEquals(4, health2.getValue());
		
	}
	
	@Test
	public void generateAIActionTest() {
		
		String aiId = "aiId";
		String targetId = "targetId";
		
		Map<FighterSkill.Id, Integer> randomSkillCheck = new HashMap<>();
		for (int i = 0; i < 30; i++) {
			FightAction action = GameService.getInstance().generateAIAction(aiId, targetId);
			randomSkillCheck.put(action.getSkill(), 1);
			Assert.assertEquals(aiId, action.getSource());
			Assert.assertEquals(targetId, action.getTargets().get(0));
		}
		
		Assert.assertEquals(6, randomSkillCheck.keySet().size());
	}
	
	@Test
	public void registerUserTest() {
		
		// Observer is set when user is registered
		Assert.assertNull(u1.getObserver());
		service.getUserService().registerUser(u1, session1.getId());
		Assert.assertNotNull(u1.getObserver());
	}
	
	@Test
	public void unregisterUserTest() {
		
		// Observer is removed when user is unregistered
		service.getUserService().registerUser(u1, session1.getId());
		Assert.assertNotNull(u1.getObserver());
		service.getUserService().unregisterUser(u1);
		Assert.assertNull(u1.getObserver());
		
	}
}
