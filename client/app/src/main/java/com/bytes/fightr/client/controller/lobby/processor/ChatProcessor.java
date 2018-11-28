package com.bytes.fightr.client.controller.lobby.processor;

import com.bytes.fightr.client.controller.lobby.FightrLobbyActivity;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.widget.Adapter.UserMsgRecyclerAdapter;
import com.bytes.fightr.common.model.event.ChatEvent;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.Message;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Kent on 4/14/2017.
 */

public class ChatProcessor extends AbstractPayloadProcessor {

    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("hh:mm");
    private FightrLobbyActivity controller;
    private UserMsgRecyclerAdapter userMsgRecyclerAdapter;


    public ChatProcessor(FightrLobbyActivity controller) {

        this.controller = controller;
        this.userMsgRecyclerAdapter = controller.getUserMessageAdapter();

    }

    @Override
    public Payload<String> process(Payload<String> payload) {

        ChatEvent event = PayloadUtil.getData(payload, FighterPayload.DataType.ChatEvent);
        Message message = event.getMessage();
        message.setType(Message.OTHER_MSG);
        userMsgRecyclerAdapter.addMessage(message);
        scrollToLast();
        return null;
    }


    /**
     * Scrolls the user message to the last item
     */
    public void scrollToLast() {
        controller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                controller.getMessagesListview().smoothScrollToPosition(
                        userMsgRecyclerAdapter.getItemCount() - 1
                );
            }
        });
    }

    public static Message createMessage(String text) {

        Message message = new Message();
        message.setUsername(GameState.Instance.getUser().getDisplayName());
        message.setMessage(text);
        message.setTime(DATE_FORMATTER.format(Calendar.getInstance().getTime()));

        return message;
    }


    /**
     * Test method - create a sample message
     *
     * @param msg      the message
     * @param username the user name
     * @return
     */
    static Message createTestMessage(String msg, String username) {
        Message message = new Message();
        message.setMessage(msg);
        message.setUsername(username);
        SimpleDateFormat df = new SimpleDateFormat("hh:mm");
        message.setTime(df.format(Calendar.getInstance().getTime()));
        return message;
    }

}
