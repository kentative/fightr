package com.bytes.fightr.client.controller.login.processor;

import android.content.Intent;
import android.widget.TextView;

import com.bytes.fightr.client.controller.lobby.FightrLobbyActivity;
import com.bytes.fightr.client.controller.login.FightrLoginActivity;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.LoginEvent;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

/**
 * Created by Kent on 4/14/2017.
 */
public class LoginProcessor extends AbstractPayloadProcessor {

    private FightrLoginActivity controller;
    private TextView textView;


    public LoginProcessor(FightrLoginActivity controller) {

        this.controller = controller;
        this.textView = controller.getStatusTextView();

    }

    @Override
    public Payload<String> process(Payload<String> payload) {

        if (payload.getDataType().equals(FighterPayload.DataType.String.toString())) {
            String data = PayloadUtil.getData(payload, FighterPayload.DataType.String);
            DisplayUtil.setText(controller, textView, data);

        } else if (payload.getDataType().equals(FighterPayload.DataType.LoginEvent.toString())) {
            handleLoginResult(payload);
        }

        return null;
    }

    private void handleLoginResult(Payload<String> payload) {

        LoginEvent data = PayloadUtil.getData(payload, FighterPayload.DataType.LoginEvent);
        String information = data.getInformation();

        switch (data.getType()) {
            case RegisterOK:
                GameState.Instance.setUserRegistered(true);
                DisplayUtil.toast(controller, "User Registered");
                controller.startActivity(new Intent(controller, FightrLobbyActivity.class));
                break;

            case RegisterFailed:
                DisplayUtil.toast(controller, information);
                break;

            case LoginOK:
                DisplayUtil.toast(controller, "User Logged in");
                controller.startActivity(new Intent(controller, FightrLobbyActivity.class));
                break;

            case LoginFailed:
                DisplayUtil.toast(controller, information);
                break;

            default:
                DisplayUtil.toast(controller, "Invalid response type: " + data.getType().name());
                break;
        }

    }
}
