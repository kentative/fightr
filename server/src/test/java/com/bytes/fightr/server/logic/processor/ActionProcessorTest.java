package com.bytes.fightr.server.logic.processor;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.MatchBuilder;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.action.FightAction.Sequence;
import com.bytes.fightr.common.model.action.FightResult;
import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.fightr.common.model.skill.SkillRegistry;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.bytes.gamr.model.avatar.AvatarAttribute;

public class ActionProcessorTest extends AbstractPayloadProcessorTest<FightAction> {

	private Match match;

	@Before
	public void setup() {
		match = new MatchBuilder().addToTeam1(f1.getId()).addToTeam2(f2.getId()).create(u1.getId());
		service.getMatchService().registerMatch(match, session1.getId());
	}

	/**
	 * Verify the result contains the correct action performed
	 */
	@Test
	public void resultPayloadTest() {
		FighterSkill.Id skill1 = FighterSkill.Id.Attack;
		FighterSkill.Id skill2 = FighterSkill.Id.Block;
		AbstractPayload<String> p1 = getPayload(f1, f2, skill1);
		AbstractPayload<String> p2 = getPayload(f2, f1, skill2);
		
		processor.process(p1); // won't get a response
		Payload<String> responsePayload = processor.process(p2);

		FightResult fightResult = PayloadUtil.getData(responsePayload, DataType.FightResult);
		List<FightAction> actions = fightResult.getActions();
		
		for (FightAction action : actions) {
			if (action.getSource().equals(f1.getId())) {
				// player action
				Assert.assertEquals(skill1, action.getSkill());
			} else {
				
				// Opponent's action
				Assert.assertEquals(skill2, action.getSkill());
			}
		}
	}

	@Test
	public void attackTest() {
		
		AbstractPayload<String> p1 = getPayload(f1, f2, FighterSkill.Id.Attack);
		AbstractPayload<String> p2 = getPayload(f2, f1, FighterSkill.Id.Attack);
		
		processor.process(p1); // won't get a response
		Payload<String> responsePayload = processor.process(p2);

		FightResult fightResult = PayloadUtil.getData(responsePayload, DataType.FightResult);
		
		for (Fighter f : fightResult.getFighters()) {
			if (f.getId().equals(f1.getId())) {
				Assert.assertEquals(4, f.getAttributes(AvatarAttribute.Type.HEALTH).getValue());
			} else {
				Assert.assertEquals(4, f.getAttributes(AvatarAttribute.Type.HEALTH).getValue());
			}
		}
		
	}
	
	@Test
	public void initiateActionTest() throws InterruptedException {
		
		AbstractPayload<String> p1 = getPayload(f1, f2, FighterSkill.Id.ChargedAttack, Sequence.Initiate);
		
		Fighter fighter  = service.getFighterService().getRegisteredFighter(f1.getId());
		fighter.setState(State.None);
		
		Payload<String> responsePayload = processor.process(p1);
		PayloadUtil.getData(responsePayload, DataType.FightResult);
		
		
		// Assert that f1 state is set to Attacking 
		Assert.assertEquals(State.Attacking, fighter.getState());
		FighterSkill skill = SkillRegistry.getInstance().getSkill(FighterSkill.Id.ChargedAttack);
		final long activationTime = skill.getTurnCost();
		Thread.sleep(activationTime+100);
		
		// Assert f1 state is reset to None when skill duration elapsed --might not need to do this
		Assert.assertEquals(State.None, fighter.getState());
		
		// Assert payload is sent to f2 to indicate that f1 is charging an attack
		Assert.assertTrue(responsePayload.getDestinationIds().contains(session2.getId()));
	}
	
	@Test
	public void interruptActionTest() throws InterruptedException {

		// Perform charged attack
		Payload<String> responsePayload1 = processor.process(getPayload(f1, f2, FighterSkill.Id.ChargedAttack, Sequence.Initiate));
		FightResult result1 = PayloadUtil.getData(responsePayload1, DataType.FightResult);
		Fighter fighterR1 = findById(f1.getId(), result1.getFighters());
		
		TimeUnit.SECONDS.sleep(1);
		// Assert that f1 state is set to Attacking 
		Fighter fighter1  = service.getFighterService().getRegisteredFighter(f1.getId());
		Assert.assertEquals(State.Attacking, fighter1.getState());  // fighter from server
		Assert.assertEquals(State.Attacking, fighterR1.getState()); // fighter from result
		
		TimeUnit.SECONDS.sleep(1);
		// Interrupt f1's action
		processor.process(getPayload(f2, f1, FighterSkill.Id.Disrupt));
		
		Fighter fighter2  = service.getFighterService().getRegisteredFighter(f2.getId());
		Assert.assertEquals(State.Interrupted, fighter1.getState());
		// Assert f2's health
		Assert.assertEquals(5, fighter2.getAttributes(AvatarAttribute.Type.HEALTH).getValue());
	}
	
	
	@Override
	public AbstractPayloadProcessor getProcessor() {
		return new ActionProcessor();
	}

	@Override
	public int getPayloadType() {
		return Payload.POST;
	}
	
	private FighterPayload getPayload(Fighter source, Fighter target, FighterSkill.Id skill) {
		return getPayload(source, target, skill, Sequence.Activate);
	}
	
	private FighterPayload getPayload(Fighter source, Fighter target, FighterSkill.Id skill, FightAction.Sequence sequence) {

		FightAction fightAction = new FightAction();
		fightAction.setSequence(sequence);
		fightAction.setSkill(skill);
		fightAction.setSource(source.getId());
		fightAction.setTargets(target.getId());
		
		// Create the request payload
		FighterPayload p = new FighterPayload(Payload.POST, 
				DataType.FightAction,
				gson.toJson(fightAction));
		p.setSourceId(session1.getId());
		return p;
	}
	
	private Fighter findById(String id, List<Fighter> fighters) {
		for (Fighter f : fighters) {
			if (f.getId().equals(id)) {
				return f;
			}
		}
		return null;
	}

	@Override
	public DataType getResponeDataType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataType getDataType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FightAction getData() {
		// TODO Auto-generated method stub
		return null;
	}
}
