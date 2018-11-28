package com.bytes.fightr.client.controller;

import android.app.ListActivity;

/**
 * Created by Kent on 4/2/2017.
 */

public class FightrSelectionController extends ListActivity{

    /*
    private FightrMainActivity mainController;

    public FightrSelectionController(FightrMainActivity mainController) {
        this.mainController = mainController;
    }

    public void promptSelectFighter(final Context context) {

        // Create the view and loadInstanceState the data
        List<Fighter> fighters = new ArrayList<>();
        LayoutInflater li = LayoutInflater.from(context);
        final View selectFighterListView = li.inflate(R.layout.ui_row_select_fighter, null);
        FighterListAdapter adapter = new FighterListAdapter(mainController, fighters);

        setAdapter(adapter);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alert dialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView fighterName = (TextView) promptsView.findViewById(R.id.select_fighter_checkbox);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                // Send Fighter Registration
                                Fighter fighter = GameState.Instance.getFighter();
                                fighter.setName(userInput.getText().toString());

                                Payload<String> payload = ClientPayloadUtil.createFighterPayload(fighter);
                                mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));

                                // get user input and set it to result edit text
                                Toast.makeText(context, userInput.getText(), Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void registerFighter() {
        DisplayUtil.toast(mainController, "Register fighter" + GameState.Instance.getFighter().getName());
        promptInput(mainController);
    }

    public void prepareMatch() {
        DisplayUtil.toast(mainController, "preparing match...");
        Fighter fighter = GameState.Instance.getFighter();
        Fighter target = GameState.Instance.getTarget();
        Payload<String> payload = ClientPayloadUtil.createMatchPayload(fighter.getId(), target.getId());
        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));
    }

    public void readyMatch() {

        Fighter fighter = GameState.Instance.getFighter();
        fighter.setStatus(Avatar.Status.Ready);
        FighterPayload payload = ClientPayloadUtil.createReadyPayload(fighter);
        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));
    }

    public void reset() {
        FighterPayload payload = new FighterPayload(FighterPayload.Type.Debug, FighterPayload.DataType.String, "reset");
        mainController.getFightrMsgService().send(PayloadUtil.toJson(payload));
    }
    */
}
