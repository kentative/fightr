package com.bytes.fightr.server.logic.processor;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.MatchBuilder;
import com.bytes.fightr.common.model.event.MatchEvent;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.event.UserEvent;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.observer.Context;
import com.bytes.fmk.observer.DestinationType;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import mockit.Mock;
import mockit.MockUp;

public class UserProcessorTest extends AbstractPayloadProcessorTest<User> {

	@Override
	public AbstractPayloadProcessor getProcessor() {
		return new UserProcessor();
	}
	
	@Override
	public DataType getResponeDataType() {
		return DataType.User;
	}

	@Override
	public int getPayloadType() {
		return Payload.POST;
	}

	@Override
	public DataType getDataType() {
		return DataType.User;
	}

	@Override
	public User getData() {
		return u1;
	}
	
	/**
	 * input: null
	 * output: all users on the servers
	 * notification: None
	 */
	@Test
	public void getUsersTest() {
		
		service.getUserService().registerUser(u1, session1.getId());
		service.getUserService().registerUser(u2, session2.getId());
		service.getUserService().registerUser(u3, session3.getId());
		
		Payload<String> payload = new FighterPayload(
				Payload.GET, 
				DataType.User, 
				null);
		payload.setSourceId(session1.getId());
		Payload<String> responsePayload = processor.process(payload);
		
		Assert.assertEquals(Payload.NOTIFY, responsePayload.getType());
		List<User> users = PayloadUtil.getDataList(responsePayload, DataType.User);
		Assert.assertEquals(
				"Assert 3 users (2 AI, 3 registered)", 
				5, users.size());
		
		Assert.assertEquals("Only 1 destination, source of the requesting session", 1, responsePayload.getDestinationIds().size());
		Assert.assertTrue(responsePayload.getDestinationIds().contains(session1.getId()));
	}
	
	/**
	 * input: user Id
	 * output: user matching the id
	 * notification: None
	 */
	@Test
	public void getUserByIdTest() {
		
		service.getUserService().registerUser(u1, session1.getId());
		service.getUserService().registerUser(u2, session2.getId());
		service.getUserService().registerUser(u3, session3.getId());
		
		Payload<String> payload = new FighterPayload(
				Payload.GET, 
				DataType.User, 
				PayloadUtil.getGson().toJson(u3));
		payload.setSourceId(session1.getId());
		
		Payload<String> responsePayload = processor.process(payload);
		
		Assert.assertEquals(Payload.NOTIFY, responsePayload.getType());
		List<User> users = PayloadUtil.getDataList(responsePayload, DataType.User);
		Assert.assertEquals("Assert 1 users", 1, users.size());
		Assert.assertEquals(u3.getDisplayName(), users.get(0).getDisplayName());
		
		Assert.assertEquals("Only 1 destination, source of the requesting session", 1, responsePayload.getDestinationIds().size());
		Assert.assertTrue(responsePayload.getDestinationIds().contains(session1.getId()));
	}
	
	/**
	 * Update user status to ready - send notifications to other user(s) in the match
	 * Update match status to ready when all fighters are ready, send only to fighters in the match
	 * input: user status - READY
	 * output: none
	 * notification: all users, if match is ready, match users
	 * 
	 */
	@Test
	public void userMatchReady() {
		
		service.getUserService().registerUser(u1, session1.getId());
		service.getUserService().registerUser(u2, session2.getId());
		service.getFighterService().registerFighter(f1);
		service.getFighterService().registerFighter(f2);
		
		MatchBuilder builder = new MatchBuilder();
		Match match = builder.addToTeam1(f1.getId()).addToTeam2(f2.getId()).create(u1.getId());
		service.getMatchService().registerMatch(match, session1.getId());
		
		User u1Clone = clone(u1);
		Fighter f1Clone = clone(f1);
		f1Clone.linkUser(u1Clone);
		final Payload<String> payload1 = PayloadBuilder.createReadyPayload(u1Clone);
		payload1.setSourceId(session1.getId());

		User u2Clone = clone(u2);
		Fighter f2Clone = clone(f2);
		f2Clone.linkUser(u2Clone);
		final Payload<String> payload2 = PayloadBuilder.createReadyPayload(u2Clone);
		payload2.setSourceId(session2.getId());

		User registeredUser1 = service.getUserService().getRegisteredUser(u1Clone.getId());
		User registeredUser2 = service.getUserService().getRegisteredUser(u2Clone.getId());
		Match registeredMatch = service.getMatchService().getRegisteredMatch(u1Clone.getAvatarId());
		
		// Assert user state notifications,
		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				
				DataType dataType = Enum.valueOf(DataType.class, payload.getDataType());
				switch (dataType) {
					case UserEvent:
						assertUserStateChangeNotification(payload, User.Status.Ready, UserEvent.Type.StateUpdate);
						break;
					
					case MatchEvent:
						Assert.fail(); // Match is not ready, 
						break;
						
					default:
						Assert.fail(); // Expect nothing else
				}
				
			}
		};
				
		// Check status after user 1 is ready
		Payload<String> response1 = processor.process(payload1);
		Assert.assertNull(response1);
		Assert.assertEquals(User.Status.Ready, registeredUser1.getStatus());
		Assert.assertEquals(User.Status.Searching, registeredUser2.getStatus());
		Assert.assertEquals(Match.State.Searching, registeredMatch.getState());
		
		// Assert user state notifications,
		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				
				DataType dataType = Enum.valueOf(DataType.class, payload.getDataType());
				switch (dataType) {
					case UserEvent:
						assertUserStateChangeNotification(payload, User.Status.Ready, UserEvent.Type.StateUpdate);
						break;
					
					case MatchEvent:
						assertMatchReadyEventNotification(payload, payload1.getSourceId());
						break;
						
					default:
						Assert.fail(); // Expect nothing else
				}
				
			}
		};
		// Check status after user 2 (all fighters) are ready		
		Payload<String> response2 = processor.process(payload2);
		Assert.assertNull(response2);
		Assert.assertEquals(User.Status.Ready, registeredUser1.getStatus());
		Assert.assertEquals(User.Status.Ready, registeredUser2.getStatus());
		Assert.assertEquals(Match.State.Ready, registeredMatch.getState());
	}

	
	/**
	 * Update user status to Searching - send all user notifications of this event
	 * Update match status to Searching, send only to fighters in the match
	 * input: user status - SEARCHING
	 * output: none
	 * notification: all users, if match is ready, match users
	 * 
	 */
	@Test
	public void userMatchSearching() {
		
		service.getUserService().registerUser(u1, session1.getId());
		service.getUserService().registerUser(u2, session2.getId());
		service.getFighterService().registerFighter(f1);
		service.getFighterService().registerFighter(f2);
		
		MatchBuilder builder = new MatchBuilder();
		Match match = builder.addToTeam1(f1.getId()).addToTeam2(f2.getId()).create(u1.getId());
		service.getMatchService().registerMatch(match, session1.getId());
		
		match.setState(Match.State.Ready, new Context(DestinationType.Custom));
		u1.setStatus(User.Status.Ready);
		u2.setStatus(User.Status.Ready);
		
		User u1Clone = clone(u1);
		Fighter f1Clone = clone(f1);
		f1Clone.linkUser(u1Clone);
		u1Clone.setStatus(User.Status.Searching);
		Payload<String> payload1 = PayloadBuilder.createReadyPayload(u1Clone);
		payload1.setSourceId(session1.getId());

		User registeredUser1 = service.getUserService().getRegisteredUser(u1Clone.getId());
		Match registeredMatch = service.getMatchService().getRegisteredMatch(u1Clone.getAvatarId());
		
		// Assert user state notifications,
		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				
				DataType dataType = Enum.valueOf(DataType.class, payload.getDataType());
				switch (dataType) {
					case UserEvent:
						assertUserStateChangeNotification(payload, User.Status.Searching, UserEvent.Type.StateUpdate);
						break;
					
					case MatchEvent:
						assertMatchSearchingEventNotification(payload);
						break;
						
					default:
						Assert.fail(); // Expect nothing else
				}
				
			}
		};
				
		// Check status after user 1 is updated to searching
		Payload<String> response1 = processor.process(payload1);
		Assert.assertNull(response1);
		Assert.assertEquals(User.Status.Searching, registeredUser1.getStatus());
		Assert.assertEquals(Match.State.Searching, registeredMatch.getState());
	}

	
	private void assertMatchReadyEventNotification(Payload<String> payload, String sourceSessionId) {
			
			Assert.assertEquals("1 destinations, other fighters", 1, payload.getDestinationIds().size());
			Assert.assertTrue(payload.getDestinationIds().contains(sourceSessionId));
			
			// Verify Match content
			Assert.assertEquals(Payload.NOTIFY, payload.getType());
			MatchEvent matchEvent = PayloadUtil.getData(payload, DataType.MatchEvent);
			Assert.assertEquals(Match.State.Ready, matchEvent.getMatch().getState());
			Assert.assertEquals(u1.getId(), matchEvent.getMatch().getHost());
	}
	
	private void assertMatchSearchingEventNotification(Payload<String> payload) {
		
		Assert.assertEquals("2 destinations, both fighters", 2, payload.getDestinationIds().size());
		Assert.assertTrue(payload.getDestinationIds().contains(session1.getId()));
		Assert.assertTrue(payload.getDestinationIds().contains(session2.getId()));
		
		// Verify Match content
		Assert.assertEquals(Payload.NOTIFY, payload.getType());
		MatchEvent matchEvent = PayloadUtil.getData(payload, DataType.MatchEvent);
		Assert.assertEquals(Match.State.Searching, matchEvent.getMatch().getState());
		Assert.assertEquals(u1.getId(), matchEvent.getMatch().getHost());
	}
	
}
