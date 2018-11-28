package com.bytes.fightr.server.logic.processor;

import java.util.ArrayList;
import java.util.List;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.action.FightResult;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.bytes.gamr.model.avatar.Avatar.AvatarType;

public class ActionProcessor extends AbstractPayloadProcessor {

	@Override
	public boolean validate(Payload<String> payload) {
		boolean valid = super.validate(payload);
		valid = valid && (PayloadUtil.getData(payload, DataType.FightAction) != null);
		if (!valid) {
			logger.error("validation error - Missing expected data: ActionType");
		}
		return valid;	
	}

	
	@Override
	public AbstractPayload<String> process(Payload<String> payload) {

		FightAction action = PayloadUtil.getData(payload, DataType.FightAction);
		String sourceId = action.getSource();
		logger.info("Performing action for fighter: "+ sourceId);

		GameService gameService = GameService.getInstance();
		Match match = gameService.getMatchService().getRegisteredMatch(sourceId);
		
		// If target of the action is an AI, automatically generate AI action
		List<String> targets = action.getTargets();
		for (String t : targets) {
			Fighter fighter = gameService.getFighterService().getRegisteredFighter(t);
			if (fighter.getType() == AvatarType.AI) {
				FightAction aiAction = gameService.generateAIAction(t, sourceId);
				match.queueAction(aiAction);			
			}
		}
		
		
		FightResult result = null;
		switch (action.getSequence()) {
		
		case Initiate:
			result = GameService.getInstance().initiateAction(match, action);
			break;
			
		case Activate:
			result = activateAction(match, action);
			break;
			
		}
		
		List<String> destinations = new ArrayList<String>(action.getTargets());
		destinations.add(action.getSource());
		FighterPayload resultPayload = new FighterPayload(Payload.NOTIFY, DataType.FightResult);
		resultPayload.setData(gson.toJson(result));
		resultPayload.setSourceId(payload.getSourceId());
		resultPayload.addDestinations(
			FightrServer.getInstance().getSessionRegistry().getSessionIds(destinations));
		
		return resultPayload;
	}


	/**
	 * Turn-based implementation: wait until both players issue action
	 * @param match
	 * @param action
	 * @return
	 */
	private FightResult activateAction(Match match, FightAction action) {
		
		FightResult result = null;
		if (match.queueAction(action)) {
			logger.info("Action queue is ready, executing actions.");
			result = GameService.getInstance().execute(match);
		} else {
			logger.info("Waiting for opponent's action.");
		}
		
		if (result == null) {
			// No result needed, still waiting for action
			return null;
		}
		return result;
	}

}
