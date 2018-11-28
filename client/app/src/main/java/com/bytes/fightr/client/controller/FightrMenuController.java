package com.bytes.fightr.client.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bytes.fightr.R;
import com.bytes.fightr.client.controller.battle.FightrMainActivity;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.util.ClientPayloadUtil;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.User;
import com.bytes.fmk.payload.Payload;

/**
 * Created by Kent on 4/2/2017.
 */

public class FightrMenuController {

    private FightrMainActivity mainController;

    public FightrMenuController(FightrMainActivity mainController) {
        this.mainController = mainController;
    }

    public void promptInput(final Context context) {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.ui_dialog_text, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alert dialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // Send Fighter Registration
                                Fighter fighter = GameState.Instance.getUserFighter();
                                fighter.setName(userInput.getText().toString());

                                Payload<String> payload = ClientPayloadUtil.createFighterPayload(fighter);
                                mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));

                                // get user input and set it to result edit text
                                Toast.makeText(context, userInput.getText(), Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void registerFighter() {
        DisplayUtil.toast(mainController, "Registering fighter: " + GameState.Instance.getUserFighter().getName());

        // Send Fighter Registration
        Fighter fighter = GameState.Instance.getUserFighter();

        Payload<String> payload = ClientPayloadUtil.createFighterPayload(fighter);
        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));

//        promptInput(mainController);
    }

    public void prepareMatch() {
        DisplayUtil.toast(mainController, "preparing match...");
//        Fighter fighter = GameState.Instance.getUserFighter();
//        Fighter target = GameState.Instance.getTargetFighter();
//        Payload<String> payload = ClientPayloadUtil.createMatchPayload(fighter.getId(), target.getId());
//        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));
    }

    public void readyMatch() {

        User user = GameState.Instance.getUser();
        user.setStatus(User.Status.Ready);
//        FighterPayload payload = ClientPayloadUtil.createReadyPayload(user);
//        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));
    }

    public void reset() {
//        GameState.Instance.reset();
        Payload<String> payload = ClientPayloadUtil.createResetPayload();
        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));
    }

    public void computeLatency() {
        Payload<String> payload = ClientPayloadUtil.createLatencyPayload();
        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));
    }
}
