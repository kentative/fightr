package com.bytes.fightr.common.payload;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fmk.data.model.Message;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.payload.Payload;

public class PayloadBuilder {
	
	
	/**
     * Creates the login payload. User must be registered before attempting to login.
     *
     * @param user the user to login
     * @return the payload to register a user
     */
    public static FighterPayload createRegisterUserPayload(User user) {

        // Create the user request payload
        user.setStatus(User.Status.New);
        return new FighterPayload(
                Payload.POST,
                FighterPayload.DataType.User,
                PayloadUtil.getGson().toJson(user));
    }

    /**
     * Creates the login payload.
     *
     * @param user the user to login
     * @return the payload to register a user
     */
    public static FighterPayload createLoginUserPayload(User user) {

        // Create the user request payload
        user.setStatus(User.Status.Available);
        return new FighterPayload(
                Payload.POST,
                FighterPayload.DataType.User,
                PayloadUtil.getGson().toJson(user));
    }

    /**
     * Create the fighter registration payload.
     * @param fighter - the fighter
     * @return the payload to register a fighter
     */
    public static FighterPayload createRegisterFighterPayload(Fighter fighter) {

        // Create the user request payload
        return new FighterPayload(
                Payload.POST,
                FighterPayload.DataType.Fighter,
                PayloadUtil.getGson().toJson(fighter));
    }
    
    /**
     * Create the message payload
     * @param message - the text message
     * @return the payload to send the message to all active users
     */
    public static FighterPayload createMessagePayload(Message message) {

        // Create the user request payload
        FighterPayload payload = new FighterPayload(
                Payload.POST,
                FighterPayload.DataType.Message,
                PayloadUtil.getGson().toJson(message));
        return payload;
    }
    
    /**
     * Create the user request payload
     * @param user - the user id
     * @return the payload to request user data
     */
    public static FighterPayload createGetUserPayload(String userId) {

    	User user = new User(userId);
        String data = (user != null) ? PayloadUtil.getGson().toJson(user) : null;
        // Create the user request payload
        return new FighterPayload(
                Payload.GET,
                FighterPayload.DataType.User,
                data);
    }
    
    /**
     * Create the payload to request all users in the server
     * @return the payload to request all users
     */
    public static FighterPayload createGetAllUserPayload() {

        // Create the user request payload
        return new FighterPayload(
                Payload.GET,
                FighterPayload.DataType.User,
                null);
    }


    /**
     * Create the fighter request payload
     * @param fighterId - the fighter id
     * @return the payload to request fighter data
     */
    public static FighterPayload createGetFighterPayload(String fighterId) {

        // Create the user request payload
        return new FighterPayload(
                Payload.GET,
                FighterPayload.DataType.Fighter,
                PayloadUtil.getGson().toJson(new Fighter(null).setId(fighterId)));
    }
    
    /**
     * Create the payload to register a match
     * @param match - the match to be register
     * @return the payload to register a match
     */
    public static FighterPayload createMatchPayload(Match match) {

        FighterPayload payload = new FighterPayload(Payload.POST, FighterPayload.DataType.Match);
        payload.setData(PayloadUtil.getGson().toJson(match));
        return payload;
    }

    /**
     * Create the payload to indicate the user is ready
     * @param user - the user
     * @return the payload to indicate the user is ready
     */
    public static FighterPayload createReadyPayload(User user) {

        FighterPayload payload = new FighterPayload(
                Payload.UPDATE, FighterPayload.DataType.User, PayloadUtil.getGson().toJson(user));
        return payload;
    }
}
