package com.bytes.fightr.server.logic.processor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fightr.server.service.MatchService;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

public class UserProcessor extends AbstractPayloadProcessor {

	private static Logger logger = LoggerFactory.getLogger(UserProcessor.class);
	
	/**
	 * Register user
	 * @param payload - the payload containing the user to be registered
	 */
	@Override
	public AbstractPayload<String> process(Payload<String> payload) {
		
		switch (payload.getType()) {
		case Payload.GET:
			return handleGet(payload);
			
		case Payload.UPDATE:
			return handleUpdate(payload);
			
		default:
			// No action, validation should have prevented this path
			return null;
		}
	
	}
	
	
	private AbstractPayload<String> handleUpdate(Payload<String> payload) {
		logger.info("Handling post user payload");
		User user = PayloadUtil.getData(payload, DataType.User);
		
		MatchService matchService = GameService.getInstance().getMatchService();
		String fighterId = user.getAvatarId();
		User registerUser = GameService.getInstance().getUserByFighterId(fighterId);
		switch (user.getStatus()) {
			case Ready:
			case Searching:
				registerUser.setStatus(user.getStatus());
				break;
				
			default:
				logger.info("Not yet handling this state: " + user.getStatus());
				return null;
			
			}
		
		matchService.updateUserStatus(fighterId, user.getStatus());
		return null;
	}

	private AbstractPayload<String> handleGet(Payload<String> payload) {

		logger.info("Get users");
		User user = PayloadUtil.getData(payload, DataType.User);
		
		GameService gameService = GameService.getInstance();
		List<User> users = new ArrayList<>();
		if (user != null) {
			users.add(gameService.getUserService().getRegisteredUser(user.getId()));
		} else {
			users = gameService.getUserService().getAllRegisteredUsers();
		}
		
		FighterPayload response = new FighterPayload(
				Payload.NOTIFY, 
				DataType.User, 
				gson.toJson(users));
		
		response.setSourceId(payload.getSourceId());
		response.addDestination(payload.getSourceId());
		logger.info("User list size: " + users.size());
		return response;

	}
}
