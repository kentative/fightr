package com.bytes.fightr.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.MatchBuilder;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.payload.AbstractPayload;
import com.google.gson.Gson;

public class FighterClientTest extends AbstractConnectionTest {

	protected int stayAlive; 			 // The delay after all messages are sent	
	protected int sendInterval;   		 // The timeout between each message (millis)
	protected Gson gson = new Gson();
	
	@Before
	public void initialize() {

		// Context root in Tomcat is the folder or war file name!
		// In this case, it's server.war
		location = "ws://localhost:8080/server/fightr";
//		location = "ws://thinkr.azurewebsites.net/server/fightr";
		
		handshakeWait = 2000;
		sendInterval = 2000;
		stayAlive = 5000;
		
		final List<String> messages = new ArrayList<>();
		testFlow(messages);
		testComputeLatency(messages);
		

		/** Sends n messages to the server. */
		sendTask = new Runnable() {

			@Override
			public void run() {
				
				for (String msg : messages) {
					
					client.send(msg);
					wait(sendInterval);
					
				}
				
				wait(stayAlive);
				client.close();
			}
			
			public void wait(int duration) {
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	private void testFlow(List<String> messages) {
		
		// Register fighter 1&2 
		// override dynamic id so server won't register as new 
		// fighter every time this test is restarted.
		User u1 = new User("u1");
		User u2 = new User("u2");
		Fighter f1 = new Fighter("Kent").setId("FighterClientTestF1");
		Fighter f2 = new Fighter("Sarius").setId("FighterClientTestF2");
		
		AbstractPayload<String> fighter1Payload = PayloadBuilder.createRegisterFighterPayload(f1);
		AbstractPayload<String> fighter2Payload = PayloadBuilder.createRegisterFighterPayload(f2);
		
		// Register Fighter
		messages.add(gson.toJson(fighter1Payload));
		messages.add(gson.toJson(fighter2Payload));
		
		// Register match
		AbstractPayload<String> matchPayload = createMatchPayload(u1.getId(), f1.getId(), f2.getId());
		messages.add(gson.toJson(matchPayload));
		
		// Request to start match for fighter 1&2
//		messages.add(gson.toJson(createReadyPayload(u1)));
//		messages.add(gson.toJson(createReadyPayload(u2)));
		
		// Attack!
		messages.add(gson.toJson(createActionPayload(f1, f2, FighterSkill.Id.Attack)));
		messages.add(gson.toJson(createActionPayload(f2, f1, FighterSkill.Id.Attack)));

		// Interruptible Attack
		messages.add(gson.toJson(createActionPayload(f1, f2, FighterSkill.Id.ChargedAttack)));
		messages.add(gson.toJson(createActionPayload(f1, f2, FighterSkill.Id.Disrupt)));
		
		messages.add(gson.toJson(createActionPayload(f1, f2, FighterSkill.Id.Attack)));
		messages.add(gson.toJson(createActionPayload(f2, f1, FighterSkill.Id.Block)));
		
		messages.add(gson.toJson(createActionPayload(f1, f2, FighterSkill.Id.Reload)));
		messages.add(gson.toJson(createActionPayload(f2, f1, FighterSkill.Id.Attack)));
		// TODO messages.add(gson.toJson(new FighterPayload(FighterPayload.Type.Debug, DataType.String, "reset")));
		
		// TODO		
//		FighterPayload actionPayload = new FighterPayload(FighterPayload.Type.Action, "Test message");
//		FighterPayload endPayload = new FighterPayload(FighterPayload.Type.End, "Test message");
//		FighterPayload errorPayload = new FighterPayload(FighterPayload.Type.Error, "Test message");
//		FighterPayload messagePayload = new FighterPayload(FighterPayload.Type.Message, "Test message");
		
//		messageList.add(gson.toJson(startPayload));
//		messageList.add(gson.toJson(actionPayload));
//		messageList.add(gson.toJson(endPayload));
//		messageList.add(gson.toJson(errorPayload));
//		messageList.add(gson.toJson(messagePayload));
		
	}
	
	private void testComputeLatency(List<String> messages) {
//		messages.add(gson.toJson(createComputeLatency()));
	}
	
	/**
	 * 
	 * @param fighter
	 * @param target
	 * @param actionType
	 * @return
	 */
	private AbstractPayload<String> createActionPayload(Fighter fighter, Fighter target, FighterSkill.Id skill) {

		FightAction action = new FightAction();
		action.setSkill(skill);
		action.setSource(fighter.getId());
		action.setTargets(target.getId());
	
		// Create the request payload
		AbstractPayload<String> payload = new FighterPayload(
			FighterPayload.POST,
			DataType.FightAction,
			gson.toJson(action));
		
		return payload;
	}

	/**
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	private AbstractPayload<String> createMatchPayload(String host, String f1, String f2) {
		MatchBuilder builder = new MatchBuilder();
		Match match = builder
				.addToTeam1(f1)
				.addToTeam2(f2).create(host);
		return PayloadBuilder.createMatchPayload(match);
	}

//	/**
//	 * 
//	 * @param fighter
//	 * @return
//	 */
//	private AbstractPayload<String> createReadyPayload(User user) {
//		
//		user.setStatus(Status.Ready);
//		AbstractPayload<String> payload = new FighterPayload(
//				FighterPayload.Type.Ready, DataType.String, user.getId());
//		return payload;
//	}
	
//	private DebugPayload createComputeLatency() {
//        DebugPayload payload = new DebugPayload(FighterPayload.Type.Debug, FighterPayload.DataType.String, "latency");
//        payload.setClientTime(TimeUtil.now());
//        return payload;
//    }
}
