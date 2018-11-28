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
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.event.UserEvent;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import mockit.Mock;
import mockit.MockUp;

public class MatchProcessorTest extends AbstractPayloadProcessorTest<Match> {
	

	@Override
	public AbstractPayloadProcessor getProcessor() {
		return new MatchProcessor();
	}
	
	@Override
	public DataType getResponeDataType() {
		return DataType.MatchEvent;
	}

	@Override
	public int getPayloadType() {
		return Payload.POST;
	}

	@Override
	public DataType getDataType() {
		return DataType.Match;
	}

	@Override
	public Match getData() {
		
		service.getUserService().registerUser(u1, session1.getId());
		service.getUserService().registerUser(u2, session2.getId());
		
		service.getFighterService().registerFighter(f1);
		service.getFighterService().registerFighter(f2);
		
		MatchBuilder builder = new MatchBuilder();
		return builder.addToTeam1(u1.getAvatarId()).addToTeam2(u2.getAvatarId()).create(u1.getId());
	}
	
	
	/**
	 * Verify the following condition:
	 *  - The Match should be set to prepared and registered to the server.
	 *  - The Fighters are no long in the match-making pool
	 *  - User state changed to READY for host
	 *  - User state changed to SEARCHING for target
	 *  - MatchEvent sent to host and target
	 * 
	 * Sample Match (note that ids are randomly generated)
	 * Match ID: 8dcac372-41c3-4a04-b429-359c4623f429 
     * State: Valid 
     * Max round: 1 
     * Team Information:
     *   - Team01 = {4980d568-2d10-46fd-a131-9647b938bd94} // u1.avatarId
     *   - Team02 = {1649e3ab-dc74-439a-9e9e-3fd5887d5164} // u2.avatarId
	 */
	@Test
	public void processMatch() {
		
		MatchBuilder builder = new MatchBuilder();
		Match match = builder.addToTeam1(u1.getAvatarId()).addToTeam2(u2.getAvatarId()).create(u1.getId());
		FighterPayload payload = new FighterPayload(Payload.POST, DataType.Match, PayloadUtil.getGson().toJson(match));
		payload.setSourceId(session1.getId());
		
		// Assert notifications,
		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				
				DataType dataType = Enum.valueOf(DataType.class, payload.getDataType());
				switch (dataType) {
					case UserEvent:
						assertUserStateChangeNotification(payload, null, UserEvent.Type.StateUpdate);
						break;
						
					case MatchEvent:
						assertMatchEventNotification(payload);
						break;
					
					default:
						Assert.fail(); // Expect nothing else
				}
				
			}
		};
		
		Payload<String> responsePayload = processor.process(payload);
		Assert.assertEquals(Payload.NOTIFY, responsePayload.getType());
		MatchEvent matchEvent = PayloadUtil.getData(responsePayload, DataType.MatchEvent);

		// Verify
		Assert.assertEquals(Match.State.Searching, matchEvent.getState());
		Assert.assertEquals(u1.getId(), matchEvent.getMatch().getHost());

		// Verify Fighters are no long in the search pool
		Match registeredMatch = matchEvent.getMatch(); //GameService.getInstance().getMatchService().getRegisteredMatch(u1.getAvatarId());
		List<String> fighterIds = registeredMatch.getFighters();
		for (String fighterId : fighterIds) {
			Fighter registeredFighter = service.getFighterService().getRegisteredFighter(fighterId);
			User registeredUser = service.getUserService().getRegisteredUser(registeredFighter);
			Assert.assertEquals(User.Status.Searching, registeredUser.getStatus());
		}
		
		// Verify the match is registered to both fighters
		Assert.assertEquals(match.getId(), service.getMatchService().getRegisteredMatch(f1.getId()).getId());
		Assert.assertEquals(match.getId(), service.getMatchService().getRegisteredMatch(f2.getId()).getId());
		
		// Verify the payload has source and destination session ids
		// source = session1, destination={session1, session2}
		Assert.assertEquals(session1.getId(), responsePayload.getSourceId());
		Assert.assertTrue(responsePayload.getDestinationIds().contains(session1.getId()));
	}

	private void assertMatchEventNotification(Payload<String> payload) {
		
		Assert.assertEquals("1 destination", 1, payload.getDestinationIds().size());
		Assert.assertTrue(payload.getDestinationIds().contains(session2.getId()));
		Assert.assertFalse(payload.getDestinationIds().contains(session1.getId()));
		
		// Verify Match content
		Assert.assertEquals(Payload.NOTIFY, payload.getType());
		MatchEvent matchEvent = PayloadUtil.getData(payload, DataType.MatchEvent);
		Assert.assertEquals(Match.State.Searching, matchEvent.getMatch().getState());
		Assert.assertEquals(u1.getId(), matchEvent.getMatch().getHost());
	}

}
