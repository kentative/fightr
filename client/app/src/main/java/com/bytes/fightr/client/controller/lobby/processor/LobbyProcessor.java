package com.bytes.fightr.client.controller.lobby.processor;

import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.client.controller.lobby.FightrLobbyActivity;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.client.widget.Adapter.UserListRecyclerAdapter;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.User;
import com.bytes.fmk.model.UserEvent;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import java.util.List;

/**
 * Created by Kent on 4/14/2017.
 * This processor handles the following PayloadType:DataType
 * <p>
 * NOTIFY:User
 * 1. Add or remove users from the list
 * 2. Update user status
 * <p>
 * NOTIFY:UserEvent
 * 1. Add or remove users from the list
 * 2. Update user status
 */
public class LobbyProcessor extends AbstractPayloadProcessor {

    private UserListRecyclerAdapter userListAdapter;
    private FightrLobbyActivity controller;

    public LobbyProcessor(FightrLobbyActivity controller) {
        this.controller = controller;
        this.userListAdapter = controller.getUserListAdapter();
    }

    /**
     * @param payload - PayloadType:DataType - NOTIFY:UserEvent
     *                - PayloadType:DataType - NOTIFY:List<User>
     * @return null, no response
     */
    @Override
    public Payload<String> process(Payload<String> payload) {

        String dataType = payload.getDataType();

        // User
        if (FighterPayload.DataType.User.toString().equals(dataType)) {
            List<User> users = PayloadUtil.getDataList(payload, FighterPayload.DataType.User);
            updateUserList(users);

            // User Event
        } else if (FighterPayload.DataType.UserEvent.toString().equals(dataType)) {
            handleUserEventNotification(payload);
        }

        return null;
    }

    private void handleUserEventNotification(Payload<String> payload) {

        DisplayUtil.toast(controller, "User State Notification");
        UserEvent userEvent = PayloadUtil.getData(payload, FighterPayload.DataType.UserEvent);
        UserEvent.Type stateType = userEvent.getType();
        switch (stateType) {
            case StateUpdate:
                updateUserState(userEvent);
                break;

            case UserAdded:
                getNewUser(userEvent);
                break;
        }
    }

    /**
     * Request to get new user specified by user Id in the user event
     *
     * @param userEvent - the user event
     */
    private void getNewUser(UserEvent userEvent) {
        Payload<String> payload = PayloadBuilder.createGetUserPayload(userEvent.getUserId());
        FightrApplication.getInstance().getMsgService().send(PayloadUtil.getGson().toJson(payload));
    }

    /**
     * Update the user list in the lobby activity to match the user list from the server
     *
     * @param users - the user data
     */
    private void updateUserList(final List<User> users) {

        controller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isDirty = false;
                List<User> dataItems = userListAdapter.getDataItems();
//                dataItems.clear();
                for (User u : users) {
                    if (!dataItems.contains(u)) {
                        dataItems.add(u);
                        isDirty = true;
                    }
                }
                if (isDirty) userListAdapter.notifyDataSetChanged();
            }
        });

        GameState game = GameState.Instance;
        for (User user : users) {
            game.addUser(user);
            controller.debugAsChat("LobbyProcessor.updateUserList", user.getId() + ":" + user.getStatus().name());
        }
    }

    /**
     * addUser the user state in the user list of the lobby activity
     *
     * @param userEvent - the user event data
     */
    private void updateUserState(final UserEvent userEvent) {

        final User user = GameState.Instance.getUser(userEvent.getUserId());
        if (user != null) {
            user.setStatus(userEvent.getStatus());
        }

        controller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<User> existingUsers = userListAdapter.getDataItems();
                for (User u : existingUsers) {
                    if (u.getDisplayName().equals(user.getDisplayName())) {
                        u.setStatus(userEvent.getStatus());
                    }
                }
                userListAdapter.notifyDataSetChanged();
            }
        });
    }
}
