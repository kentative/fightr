package com.bytes.fightr.server.logic.processor;

import java.util.List;
import java.util.Set;

import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.Match.State;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.data.model.User.Status;
import com.bytes.fmk.observer.Context;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

public class ReadyProcessor extends AbstractPayloadProcessor {

	@Override
	public boolean validate(Payload<String> payload) {
		boolean valid = super.validate(payload);
		valid = valid && (PayloadUtil.getData(payload, DataType.String) != null);
		if (!valid) {
			logger.error("validation error - Missing expected data: Fighter id");
		}
		return valid;	
	}

	
	/**
	 * Obtain the registered fighter via the specified id
	 * Set the status to Ready
	 * Send the update to all other players in the match
	 * If all fighters in the match are ready, send start payload
	 */
	@Override
	public Payload<String> process(Payload<String> payload) {

		String userId = PayloadUtil.getData(payload, DataType.String);
		logger.info("Confirming ready status for user "+ userId);
		
		GameService gameService = GameService.getInstance();
		User user = gameService.getUserService().getRegisteredUser(userId);
		
		user.setStatus(Status.Ready);
		Match match = gameService.getMatchService().getRegisteredMatch(user.getAvatarId());
		List<String> idList = match.getFighters();
		idList.remove(user.getAvatarId()); // remove the source
		
		Set<String> sessionIds = FightrServer.getInstance().getSessionRegistry().getSessionIds(idList);
		// TODO Payload Ready
		FighterPayload responsePayload = new FighterPayload(
				Payload.NOTIFY, DataType.MatchEvent, gson.toJson(user.getAvatarId()));
		responsePayload.setSourceId(payload.getSourceId());
		responsePayload.addDestinations(sessionIds);

		// Send START match payload if ALL fighters are ready
		if (gameService.getMatchService().isStartable(match)) {
			match.setState(State.Ready, new Context());
			logger.debug("Match is startable, sending START payload");
			
			idList.add(user.getAvatarId());
			sessionIds = FightrServer.getInstance().getSessionRegistry().getSessionIds(idList);
			// TODO Payload Start 
			FighterPayload startMatchPayload = new FighterPayload(
					Payload.NOTIFY, 
					DataType.MatchEvent, 
					match.getId());
			// TODO startMatchPayload.setSourceId(SessionRegistry.SERVER_SESSION_ID);
			startMatchPayload.addDestinations(sessionIds);
			FightrServer.getInstance().send(startMatchPayload);
		} else {
			logger.debug("Match is NOT startable.");
		}
		
		logger.info(String.format("Fighter %1$s is: %2$s", user.getAvatarId(), user.getStatus()));
		return responsePayload;
	}

}
