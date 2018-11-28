package com.bytes.fightr.server.logic.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

public class FighterProcessor extends AbstractPayloadProcessor {

	private static Logger logger = LoggerFactory.getLogger(FighterProcessor.class);
	
	/**
	 * Data must contain a valid Fighter
	 */
	@Override
	public boolean validate(Payload<String> payload) {
		boolean valid = super.validate(payload);
		valid = valid && (PayloadUtil.getData(payload, DataType.Fighter) != null);
		if (!valid) {
			logger.error("validation error - Missing expected data: Fighter");
		}
		return valid;
	}
	
	/**
	 * Request to process a {@code Fighter} payload.
	 * This processor performs the following:
	 * 1. Fighter registration
	 * 2. Fighter retrieval
	 * 
	 * @param payload - the payload containing the fighter to be registered
	 */
	@Override
	public AbstractPayload<String> process(Payload<String> payload) {
		
		switch (payload.getType()) {
		case Payload.GET:
			return getRegisteredFighter(payload);
			
		case Payload.POST:
			return registerFighter(payload);
			
		default:
			// No action, validation should have prevented this path
			return null;
		}
	}

	/**
	 * 
	 * @param payload
	 * @return
	 */
	private AbstractPayload<String> getRegisteredFighter(Payload<String> payload) {
		
		logger.info("Get register fighter");
		Fighter fighter = PayloadUtil.getData(payload, DataType.Fighter);
		
		Fighter registeredFighter = null;
		if (fighter != null && fighter.getId() != null) {
			registeredFighter = GameService.getInstance().getFighterService().getRegisteredFighter(fighter.getId());
		}
		
		FighterPayload response = new FighterPayload(
				Payload.NOTIFY, 
				DataType.Fighter, 
				gson.toJson(registeredFighter));
		if (registeredFighter == null) {
			response.setStatus(Payload.STATUS_ERROR);
		}
		
		response.setSourceId(payload.getSourceId());
		response.addDestination(payload.getSourceId());
		return response;
	}

	private AbstractPayload<String> registerFighter(Payload<String> payload) {

		logger.info("Registering fighter");
		Fighter fighter = PayloadUtil.getData(payload, DataType.Fighter);
		GameService.getInstance().getFighterService().registerFighter(fighter);
		return null;
	}
	
}
