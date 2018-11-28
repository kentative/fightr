package com.bytes.fightr.client.controller.battle;

import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.client.controller.battle.FightrMainActivity;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.service.comm.WebSocketService;
import com.bytes.fightr.client.util.ClientPayloadUtil;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.client.widget.image.FighterActionView;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.skill.SkillRegistry;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;

import java.util.Locale;

/**
 * Created by Kent on 4/29/2017.
 *
 */
public class FightrSkillTouchEventListener implements View.OnTouchListener {

    private ProgressBar skillGauge;
    private AsyncTask<Integer, Integer, Boolean> skillActivation;
    private FightrMainActivity mainController;
    private FightAction action;

    public FightrSkillTouchEventListener(FightrMainActivity mainController) {
        this.mainController = mainController;
        this.skillGauge = mainController.getSourceActionGauge();
    }

    private void startGauge() {

        if (action == null) {
            // potential race condition can result in the action not yet selected.
            return;
        }

        int activationTime = SkillRegistry.getInstance().getSkill(action.getSkill()).getTurnCost();
        DisplayUtil.setText(mainController, mainController.getDebugText(), String.format(Locale.getDefault(),
                "%1$s activation time: %2$d", action.getSkill(), activationTime));

        skillActivation = new ActivateSkillTask().execute(activationTime);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (!(v instanceof FighterActionView) || !v.isEnabled()) {
            return false;
        }

        // Determine the action based on the selected view
        action = mainController.getSkillbarController().getAction((FighterActionView) v);
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                debug("action down");
                startGauge();
                break;

            case MotionEvent.ACTION_UP:
                debug("action up");
                skillActivation.cancel(true);
                break;

            default:
                break;
        }

        return true;
    }

    private void debug(String text) {
        Log.i("FighterActionView", text);
    }

    private class ActivateSkillTask extends AsyncTask<Integer, Integer, Boolean> {

        protected Boolean doInBackground(Integer... activationTimes) {
            int elapsedTime = 0;
            while (elapsedTime < activationTimes[0]) {
                try {
                    Thread.sleep(10);
                    elapsedTime += 10;
                    publishProgress((int) ((elapsedTime / (float) activationTimes[0] * 100)));
                    if (isCancelled()) {
                        debug("Skill Cancelled!");
                        return false;
                    }
                } catch (InterruptedException e) {
                    cancel(true);
                }
            }

            return true;
        }

        protected void onProgressUpdate(Integer... progress) {
            skillGauge.setProgress(progress[0]);
        }

        protected void onPostExecute(Boolean complete) {

            if (complete) {
                executeAction();
            }
        }

        /**
         *
         */
        private void executeAction() {

            // If action is not ready, or connection is not active
            // then do nothing

            WebSocketService msgService = ((FightrApplication) mainController.getApplication()).getMsgService();
            if (!msgService.isActive()) {

                DisplayUtil.toast(mainController, "Connection is not ready");
                return;
            }

            Fighter fighter = GameState.Instance.getUserFighter();
            Fighter target = GameState.Instance.getTargetFighter();
            if (action == null || fighter == null || target == null) {
                return;
            }

            Payload<String> payload = ClientPayloadUtil.createActionPayload(fighter.getId(), target.getId(), action.getSkill());
            DisplayUtil.toast(mainController, "Sending action to server: " + payload.getId());
            mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));

//        mainController.getFightrSoundPool().play(R.raw.beretta, 1f, 1f, 0, 0, 1f);
            // Clear action
        }
    }
}
