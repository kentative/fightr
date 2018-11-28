package com.bytes.fightr.client.controller.lobby;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bytes.fightr.R;
import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.client.widget.Adapter.FighterListRecyclerAdapter;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.User;

import java.util.ArrayList;

/**
 * Created by Kent on 6/8/2017.
 */

public class MatchDialog {

    private FightrLobbyActivity controller;
    private AlertDialog dialog;
    private View mainView;
    private CheckBox readyCheckBox;

    // team 01
    private RecyclerView leftFighterListView;

    private FighterListRecyclerAdapter leftFighterListAdapter;

    // team 02
    private RecyclerView rightFighterListView;
    private FighterListRecyclerAdapter rightFighterListAdapter;

    /**
     * Default constructor
     *
     * @param controller
     */
    public MatchDialog(final FightrLobbyActivity controller) {
        this.controller = controller;

        // Get view from xml layout
        LayoutInflater li = LayoutInflater.from(controller);
        mainView = li.inflate(R.layout.fightr_match_dialog, null);

        readyCheckBox = (CheckBox) mainView.findViewById(R.id.match_fighter_ready);
        readyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyCheckBox.isChecked()) {

                    // Send user ready
                    User user = GameState.Instance.getUser();
                    user.setStatus(User.Status.Ready);
                } else {
                    // Send user ready
                    User user = GameState.Instance.getUser();
                    user.setStatus(User.Status.Searching);
                }

                DisplayUtil.toast(controller, "value " + readyCheckBox.isChecked());
                FighterPayload readyPayload = PayloadBuilder.createReadyPayload(GameState.Instance.getUser());
                FightrApplication.getInstance().getMsgService().send(PayloadUtil.toJson(readyPayload));
            }
        });

        // Left fighters (team 1)
        leftFighterListView = (RecyclerView) mainView.findViewById(R.id.match_fighter_left);
        leftFighterListAdapter = new FighterListRecyclerAdapter(controller, new ArrayList<Fighter>());
        leftFighterListView.setLayoutManager(new LinearLayoutManager(controller));
        leftFighterListView.setAdapter(leftFighterListAdapter);

        // Right fighters (team 2)
        rightFighterListView = (RecyclerView) mainView.findViewById(R.id.match_fighter_right);
        rightFighterListAdapter = new FighterListRecyclerAdapter(controller, new ArrayList<Fighter>());
        rightFighterListView.setLayoutManager(new LinearLayoutManager(controller));
        rightFighterListView.setAdapter(rightFighterListAdapter);
    }

    public void promptMatchConfirmation(final Context context) {

        // Build view
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(mainView);

        // set dialog message
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Match match = GameState.Instance.getMatch();

                        match.setState(Match.State.Ready, null);
                        FightrApplication.getInstance().getMsgService()
                                .send(PayloadUtil.toJson(PayloadBuilder.createMatchPayload(match)));

                        Toast.makeText(context, "Start", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Match match = GameState.Instance.getMatch();
                                match.setState(Match.State.Declined, null);
                                FightrApplication.getInstance().getMsgService()
                                        .send(PayloadUtil.toJson(PayloadBuilder.createMatchPayload(match)));
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        dialog = alertDialogBuilder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {                    //
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        dialog.setTitle("Match Confirmation");
        dialog.show();
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public RecyclerView getLeftFighterListView() {
        return leftFighterListView;
    }

    public FighterListRecyclerAdapter getLeftFighterListAdapter() {
        return leftFighterListAdapter;
    }

    public RecyclerView getRightFighterListView() {
        return rightFighterListView;
    }

    public FighterListRecyclerAdapter getRightFighterListAdapter() {
        return rightFighterListAdapter;
    }
}
