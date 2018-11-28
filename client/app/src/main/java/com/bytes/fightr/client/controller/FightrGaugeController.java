package com.bytes.fightr.client.controller;

import android.widget.ProgressBar;

import com.bytes.fightr.client.controller.battle.FightrMainActivity;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.service.comm.WebSocketService;
import com.bytes.fightr.client.util.ClientPayloadUtil;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.google.gson.Gson;


/**
 * Created by Kent on 4/1/2017.
 */

public class FightrGaugeController {

    private ProgressBar actionGauge;

    private int max;
    private int status;

    /**
     * The Action gauge speed, in milliseconds.
     * Smaller value is faster. The total gauge duration is 100 times this value.
     * 10 = 10 * 100 = 1 second,
     * 20 = 20 * 100 = 2 seconds
     */
    private int speed;

    private FightrMainActivity mainController;
    private FightrSkillBarController actionController;
    private WebSocketService msgService;
    private Gson gson;

    public FightrGaugeController(final FightrMainActivity activity) {

        this.mainController = activity;
        this.actionGauge = mainController.getActionGauge();

        this.speed = 40;
        this.max = 100;
        this.gson = new Gson();
        actionGauge.setMax(max);

    }

    /**
     * Starts the action gauge.
     * The specified action will be performed, sent to the server, when the gauge is filled.
     */
    public void start() {

        // Loop the action gauge
        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    while (status < 100) {

                        try {
                            Thread.sleep(speed);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Update the progress bar
                        actionGauge.post(new Runnable() {
                            public void run() {
                                status = (status % max) + 1;
                                actionGauge.setProgress(status);
                            }
                        });

                        if (actionGauge.getProgress() >= actionGauge.getMax()) {
                            performAction();
                        }

                    }
                }
            }
        }).start();
    }

    /**
     *
     */
    private void performAction() {

        // If action is not ready, or connection is not active
        // then do nothing
        if (actionController == null || actionController.getAction() == null) {

            DisplayUtil.toast(mainController, "Action is not ready");
            return;
        }


        if (!msgService.isActive()) {

            DisplayUtil.toast(mainController, "Connection is not ready");
            return;
        }


        final FightAction action = actionController.getAction();

        if (action == null) {
            return;
        }

        Fighter fighter = GameState.Instance.getUserFighter();
        Fighter target = null; // todo GameState.Instance.getTargetFighter();
        Payload<String> payload = ClientPayloadUtil.createActionPayload(
                fighter.getId(), target.getId(), action.getSkill());
        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));

//        mainController.getFightrSoundPool().play(R.raw.beretta, 1f, 1f, 0, 0, 1f);
        // Clear action
        actionController.setAction(null);
    }

    public void setMsgService(WebSocketService msgService) {
        this.msgService = msgService;
    }

    public void setActionController(FightrSkillBarController actionController) {
        this.actionController = actionController;
    }
}
