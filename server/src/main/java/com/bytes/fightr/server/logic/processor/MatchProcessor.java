package com.bytes.fightr.server.logic.processor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.Match.State;
import com.bytes.fightr.common.model.event.MatchEvent;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fightr.server.service.MatchService;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

public class MatchProcessor extends AbstractPayloadProcessor {

	private static Logger logger = LoggerFactory.getLogger(MatchProcessor.class);
	
	/**
	 * Check for valid Match:
	 */
	@Override
	public boolean validate(Payload<String> payload) {
		
		boolean valid = super.validate(payload);
		valid = valid && (PayloadUtil.getData(payload, DataType.Match) != null);
		if (!valid) {
			logger.error("validation error - Missing expected data: Match");
		}
		return valid;
	}

	
	@Override
	public AbstractPayload<String> process(Payload<String> payload) {
		
		switch (payload.getType()) {
		case Payload.POST:
			return handlePost(payload);
			
		default:
			// No action, validation should have prevented this path
			return null;
		}
	}

	private AbstractPayload<String> handlePost(Payload<String> payload) {
		
		logger.info("Match: " + payload.getData());
		Match match = PayloadUtil.getData(payload, DataType.Match);
		switch (match.getState()) {
		
		case New:
			return registerMatch(payload, match);
			
		case Declined:
			return cancelMatch(payload, match);
			
		default:
			break;
		
		}
		return null;
	}

	private AbstractPayload<String> registerMatch(Payload<String> payload, Match match) {
		
		logger.info("Registering match");
		Match registerMatch = GameService.getInstance().getMatchService().registerMatch(match, payload.getSourceId());

		// Response, don't send the match back here. It will be notified
		MatchEvent matchEvent = new MatchEvent(State.Searching, registerMatch);
		FighterPayload responsePayload = new FighterPayload(
				Payload.NOTIFY, 
				DataType.MatchEvent, 
				gson.toJson(matchEvent));
		responsePayload.setSourceId(payload.getSourceId());
		responsePayload.addDestination(payload.getSourceId());
		return responsePayload;
	}

	private AbstractPayload<String> cancelMatch(Payload<String> payload, Match match) {

		logger.info("Cancelling match");
		MatchService matchService = GameService.getInstance().getMatchService();
		List<String> fighterIds = match.getFighters();
		for (String id : fighterIds) {
			matchService.unregister(id);
		}

		MatchEvent matchEvent = new MatchEvent(State.Declined, match);
		FighterPayload responsePayload = new FighterPayload(
				Payload.NOTIFY, 
				DataType.MatchEvent, 
				gson.toJson(matchEvent));
		responsePayload.setSourceId(payload.getSourceId());
		responsePayload.addDestination(payload.getSourceId());
		return responsePayload;
		
	}
}
