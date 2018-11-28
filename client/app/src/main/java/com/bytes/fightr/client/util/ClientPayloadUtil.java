package com.bytes.fightr.client.util;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.MatchBuilder;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.debug.LatencyCmd;
import com.bytes.fightr.common.model.debug.ResetCmd;
import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.util.TimeUtil;
import com.bytes.fmk.model.User;
import com.bytes.fmk.payload.Payload;
import com.google.gson.Gson;

/**
 * Created by Kent on 4/14/2017.
 */

public class ClientPayloadUtil {


    private static Gson gson = new Gson();

    /**
     * @param fighter
     * @param target
     * @param skill
     * @return
     */
    public static FighterPayload createActionPayload(String fighter, String target, FighterSkill.Id skill) {

        FightAction action = new FightAction(skill);
        action.setSource(fighter);
        action.setTargets(target);

        // Create the request payload
        FighterPayload payload = new FighterPayload(
                Payload.POST,
                FighterPayload.DataType.FightAction,
                gson.toJson(action));

        return payload;
    }

    public static FighterPayload createFighterPayload(Fighter fighter) {

        // Create the request payload
        FighterPayload payload = new FighterPayload(
                Payload.POST,
                FighterPayload.DataType.Fighter,
                gson.toJson(fighter));
        return payload;
    }

    /**
     * Request to create a debug payload to reset the server
     * @return the payload
     */
    public static Payload<String> createResetPayload() {
        return new FighterPayload(Payload.POST, FighterPayload.DataType.ResetCmd, gson.toJson(new ResetCmd()));
    }

    /**
     * Request to create a debug payload to detect latency
     * @return the payload
     */
    public static Payload<String> createLatencyPayload() {

        LatencyCmd command = new LatencyCmd();
        command.setClientTime(TimeUtil.now());
        return new FighterPayload(Payload.POST, FighterPayload.DataType.ResetCmd, gson.toJson(command));
    }
}
