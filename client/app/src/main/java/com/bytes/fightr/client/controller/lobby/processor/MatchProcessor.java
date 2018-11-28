package com.bytes.fightr.client.controller.lobby.processor;

import android.app.AlertDialog;
import android.widget.Button;

import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.client.controller.lobby.FightrLobbyActivity;
import com.bytes.fightr.client.controller.lobby.MatchDialog;
import com.bytes.fightr.client.logic.GameLogic;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.logic.MatchLogic;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.client.widget.Adapter.FighterListRecyclerAdapter;
import com.bytes.fightr.client.widget.Adapter.UserListRecyclerAdapter;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.Team;
import com.bytes.fightr.common.model.event.MatchEvent;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.User;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import java.util.List;

/**
 * Created by Kent on 4/14/2017.
 * This processor is intended for processing team formation.
 * It handles the following Payload
 * <p>
 * NOTIFY:Fighter
 * 1. Add enemy target to team
 * 2. Add ally target to team
 */
public class MatchProcessor extends AbstractPayloadProcessor {

    private UserListRecyclerAdapter userListAdapter;
    private FightrLobbyActivity controller;
    private MatchDialog dialog;

    public MatchProcessor(FightrLobbyActivity controller) {
        this.controller = controller;
        this.userListAdapter = controller.getUserListAdapter();
        GameLogic.Instance.getMatchLogic().initMatchAsHost();
    }


    @Override
    public Payload<String> process(Payload<String> payload) {

        MatchEvent matchEvent = PayloadUtil.getData(payload, FighterPayload.DataType.MatchEvent);
        if (matchEvent.getMatch().getState() == Match.State.Invalid) {
            controller.debugAsChat("Match Processor", "Invalid match");
            return null;
        }

        switch (matchEvent.getState()) {
            case New:
                break;

            case Searching:
                displayMatchInvite(matchEvent.getMatch());
                break;

            case Declined:
                controller.debugAsChat("Match Processor", "Resetting match");
                GameLogic.Instance.getMatchLogic().initMatchAsHost();
                break;

            case Ready:
                // Enabled the start match button
                controller.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Button button = dialog.getDialog().getButton(AlertDialog.BUTTON_POSITIVE);
                        if (button != null) {
                            button.setEnabled(true);
                        }
                    }
                });
                break;


            case Active:
                break;
            case Completed:
                break;
            case Invalid:
                break;
        }

        return null;
    }

    private void displayMatchInvite(final Match match) {

        // Invalid match
        if (match == null) {
            return;
        }

        dialog = new MatchDialog(controller);
        controller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                controller.debugAsChat("MatchProcessor", "Display Match Invite");
                updateFighters(dialog, match, Team.Id.Team01);
                updateFighters(dialog, match, Team.Id.Team02);
                dialog.promptMatchConfirmation(controller);
            }
        });

    }

    private void updateFighters(MatchDialog dialog, Match match, Team.Id team) {

        FighterListRecyclerAdapter fighterListAdapter;
        switch (team) {
            case Team01:
                fighterListAdapter = dialog.getLeftFighterListAdapter();
                break;
            case Team02:
                fighterListAdapter = dialog.getRightFighterListAdapter();
                break;
            default:
                throw new IllegalArgumentException("Only supports Team01 and Team02");
        }

        List<Fighter> fighters = fighterListAdapter.getDataItems();
        fighters.clear();
        List<String> fighterIds = match.getTeam(team).getFighters();
        for (String id : fighterIds) {
            fighters.add(GameState.Instance.getFighter(id));
        }
        fighterListAdapter.notifyDataSetChanged();
    }


    public void prepareMatch() {

        DisplayUtil.toast(controller, "preparing match...");
        Fighter fighter = GameState.Instance.getUserFighter();
//        Fighter target = GameState.Instance.getTargetFighter();
//        Payload<String> payload = createMatchPayload(fighter.getId(), target.getId());
//        FightrApplication.getInstance().getMsgService().send(PayloadUtil.toJson(payload));
    }

    public void readyMatch() {
        User user = GameState.Instance.getUser();
        user.setStatus(User.Status.Ready);
        FighterPayload payload = PayloadBuilder.createReadyPayload(user);
        FightrApplication.getInstance().getMsgService().send(PayloadUtil.toJson(payload));
    }
}
