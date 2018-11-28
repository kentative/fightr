package com.bytes.fightr.server.logic.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fmk.data.event.LoginEvent;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.data.model.User.Status;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

public class LoginProcessor extends AbstractPayloadProcessor {

	private static Logger logger = LoggerFactory.getLogger(LoginProcessor.class);
	
	/**
	 * Register user
	 * @param payload - the payload containing the user to be registered
	 */
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
		User user = PayloadUtil.getData(payload, DataType.User);
		if (user.getStatus() == User.Status.New) {
			return registerUser(payload, user);
		} else {
			return loginUser(payload, user.getId());
		}
	}

	private AbstractPayload<String> registerUser(Payload<String> payload, User user) {

		logger.info("Registering user");
		GameService gameService = GameService.getInstance();
		User registeredUser = gameService.getUserService().registerUser(user, payload.getSourceId());

		LoginEvent result = new LoginEvent();
		if (registeredUser != null) {
			result.setType(LoginEvent.Type.RegisterOK);
		} else {
			result.setType(LoginEvent.Type.RegisterFailed);
			result.setInformation(String.format("User name %1$s is not available.", user.getId()));
		}
		
		// Response
		FighterPayload response = new FighterPayload(
				Payload.NOTIFY, 
				DataType.LoginEvent, 
				gson.toJson(result));

		response.setSourceId(payload.getSourceId());
		response.addDestination(payload.getSourceId());
		return response;
	}
	
	/**
	 * 
	 * @param payload
	 * @param userId - the user id
	 * @return
	 */
	private AbstractPayload<String> loginUser(Payload<String> payload, String userId) {
		
		logger.info("Login user");
		
		GameService gameService = GameService.getInstance();
		User user = gameService.getUserService().getRegisteredUser(userId);
		LoginEvent result = new LoginEvent();
		if (user != null) {
//			TODO result.setInformation("Unable to authenticate user" + userId);
			gameService.getUserService().registerUser(user, payload.getSourceId());
			result.setType(LoginEvent.Type.LoginOK);
			user.setStatus(Status.Available); 
		} else {
			result.setType(LoginEvent.Type.LoginFailed);
			result.setInformation("User " + userId + " is not registered.");
		}
		
		FighterPayload response = new FighterPayload(
				Payload.NOTIFY, 
				DataType.LoginEvent, 
				gson.toJson(result));
		
		response.setSourceId(payload.getSourceId());
		response.addDestination(payload.getSourceId());
		return response;
	}
}
