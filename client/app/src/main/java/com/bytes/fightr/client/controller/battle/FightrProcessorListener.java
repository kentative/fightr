package com.bytes.fightr.client.controller.battle;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.processor.ProcessorEvent;
import com.bytes.fmk.payload.processor.ProcessorListener;

import java.util.List;

/**
 * Created by Kent on 4/14/2017.
 */

public class FightrProcessorListener implements ProcessorListener {

    private FightrMainActivity mainController;
    private TextView textView;


    public FightrProcessorListener(FightrMainActivity mainController) {

        this.mainController = mainController;
        this.textView = mainController.getDebugText();

    }

    @Override
    public void onEvent(String message) {
        DisplayUtil.appendText(mainController, textView, message);
    }

    @Override
    public <T> void onEvent(ProcessorEvent<T> event) {

        String json = event.getData().toString();
        switch (event.getType()) {

            // This is use for debugging
            case Default:
                DisplayUtil.appendText(mainController, textView, json);
                break;

            case Snackbar:
                Snackbar.make(textView, json, Snackbar.LENGTH_LONG).show();
                break;


            case Fighters:
                List<Fighter> dataList = PayloadUtil.getDataList(json, FighterPayload.DataType.Fighter);
                // TODO GameState.Instance.setFighters(dataList);

//                Intent selectFighterIntent = new Intent(mainController.getApplicationContext(), SelectFighterActivity.class);
//                selectFighterIntent.putExtra(SelectFighterActivity.FIGHTER_LIST_KEY, event.getData().toString());
//                mainController.startActivity(selectFighterIntent);
                break;

            case StartMatch:
//                DisplayUtil.toast(mainController, "Starting action gauge");
//                mainController.getFightrGaugeController().start();
                break;

            case ActionResults:
                FightAction action = (FightAction) event.getData();
                Fighter self = GameState.Instance.getUserFighter();

                DisplayUtil.appendText(mainController, textView, "Action: " + action.getSkill().name());

                if (self.getId().equals(action.getSource())) {
                    mainController.updateSourceFighterAction(action);
                } else {
                    mainController.updateTargetFighterAction(action);
                }
                break;

            case ActionResults_P1:
                DisplayUtil.appendText(mainController, textView, event.getData().toString());
                break;

            case GameResults:
                DisplayUtil.toast(mainController, json);
                DisplayUtil.appendText(mainController, mainController.getDebugText(), "Resetting server.");
                mainController.getFightrMenuController().reset();
                break;
        }
    }
}
