package com.bytes.fightr.client.controller.lobby.processor;

import android.widget.Toast;

import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.client.controller.lobby.FightrLobbyActivity;
import com.bytes.fightr.client.logic.GameLogic;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.client.widget.Adapter.UserListRecyclerAdapter;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.Team;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.User;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

/**
 * Created by Kent on 4/14/2017.
 * This processor is intended for processing team formation.
 * It handles the following Payload
 * <p>
 * NOTIFY:Fighter
 * 1. Add enemy target to team
 * 2. Add ally target to team
 */
public class PartyProcessor extends AbstractPayloadProcessor {

    private UserListRecyclerAdapter userListAdapter;
    private FightrLobbyActivity controller;

    public PartyProcessor(FightrLobbyActivity controller) {
        this.controller = controller;
        this.userListAdapter = controller.getUserListAdapter();
    }


    @Override
    public Payload<String> process(Payload<String> payload) {

        String dataType = payload.getDataType();

        // Fighter
        if (FighterPayload.DataType.Fighter.toString().equals(dataType)) {
            handleFighterNotification(payload);
        }

        return null;
    }


    private void handleFighterNotification(Payload<String> payload) {

        GameState gameState = GameState.Instance;
        if (payload.getStatus() == Payload.STATUS_OK) {
            Fighter fighter = PayloadUtil.getData(payload, FighterPayload.DataType.Fighter);
            gameState.addFighter(fighter);
            controller.debugAsChat("PartyProcessor", "Fighter data received: " + fighter.toString());

        } else {
            DisplayUtil.toast(controller, "Target has invalid fighter", Toast.LENGTH_SHORT);
        }
    }


    /**
     * Request for fighter information
     *
     * @param fighterId - the fighter id
     */
    private void requestFighterInfo(String fighterId) {
        FightrApplication.getInstance().getMsgService().send(
                PayloadUtil.getGson().toJson(PayloadBuilder.createGetFighterPayload(fighterId)));
    }

    /**
     * Request to set the selected user as an enemy target.
     *
     * @param userId - the user id
     */
    public void selectEnemyTarget(String userId) {

        controller.debugAsChat(GameState.Instance.getUser().getId(), "Requesting to select enemy " + userId);
        GameState gameState = GameState.Instance;
        GameLogic gameLogic = GameLogic.Instance;

        User targetUser = getValidTarget(userId);
        if (targetUser == null) {
            DisplayUtil.toast(controller, "Invalid target");
            return;
        }

        // Selected target is already on team
        Match match = gameState.getMatch();
        if (match.getTeam(Team.Id.Team02).getFighters().contains(targetUser.getAvatarId())) {
            controller.debugAsChat("Party Processor", "Target is already an enemy");
            return;
        }

        String fighterId = targetUser.getAvatarId();
        gameLogic.getMatchLogic().addEnemyToMatch(gameState.getUser().getId(), targetUser.getId());
        requestFighterInfo(fighterId);
    }

    /**
     * Request to set the selected user as an ally target.
     *
     * @param userId - the user id
     */
    public void selectAllyTarget(String userId) {

        controller.debugAsChat(GameState.Instance.getUser().getId(), "Requesting to select ally " + userId);
        GameState gameState = GameState.Instance;
        GameLogic gameLogic = GameLogic.Instance;

        User targetUser = getValidTarget(userId);
        if (targetUser == null) {
            DisplayUtil.toast(controller, "Invalid target");
            return;
        }

        // Selected target is already on team
        Match match = gameState.getMatch();
        if (match.getTeam(Team.Id.Team01).getFighters().contains(targetUser.getAvatarId())) {
            controller.debugAsChat("Party Processor", "Target is already an ally");
            return;
        }

        String fighterId = targetUser.getAvatarId();
        gameLogic.getMatchLogic().addAllyToMatch(gameState.getUser().getId(), targetUser.getId());
        requestFighterInfo(fighterId);
    }

    /**
     * Request to validate the selected target. If the target is valid, the user is returned.
     *
     * @param userId - the selected target id
     * @return the user if the target is valid, null otherwise
     */
    private User getValidTarget(String userId) {

        GameState game = GameState.Instance;
        if (userId.equals(game.getUser().getId())) {
            DisplayUtil.toast(controller, "Invalid target");
            return null;
        }

        User user = game.getUser(userId);
        if (user == null) {
            DisplayUtil.toast(controller, "Invalid user: " + userId, Toast.LENGTH_SHORT);
            return null;
        }

        if (user.getStatus() != User.Status.Available) {
            DisplayUtil.toast(controller, userId + " is not available", Toast.LENGTH_SHORT);
            return null;
        }

        return user;
    }
}
