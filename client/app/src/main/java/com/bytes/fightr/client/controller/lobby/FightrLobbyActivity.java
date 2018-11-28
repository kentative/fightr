package com.bytes.fightr.client.controller.lobby;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bytes.fightr.R;
import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.client.controller.lobby.processor.ChatProcessor;
import com.bytes.fightr.client.controller.lobby.processor.LobbyProcessor;
import com.bytes.fightr.client.controller.lobby.processor.MatchProcessor;
import com.bytes.fightr.client.controller.lobby.processor.PartyProcessor;
import com.bytes.fightr.client.logic.GameLogic;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.logic.processor.FighterPayloadProcessorRegistry;
import com.bytes.fightr.client.service.comm.WebSocketService;
import com.bytes.fightr.client.widget.Adapter.UserListRecyclerAdapter;
import com.bytes.fightr.client.widget.Adapter.UserMsgRecyclerAdapter;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.Message;
import com.bytes.fmk.model.User;
import com.bytes.fmk.payload.Payload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kent on 5/20/2017.
 */
public class FightrLobbyActivity extends Activity {

    // User Views
    private RecyclerView userListView;
    private UserListRecyclerAdapter userListAdapter;
    private LobbyProcessor lobbyProcessor;

    // Chat Message Views
    private RecyclerView messagesListview;
    private ImageView sendButton;
    private ImageView emojiButton;
    private EditText userText;
    private UserMsgRecyclerAdapter userMessageAdapter;
    private ChatProcessor chatProcessor;
    private PartyProcessor partyProcessor;
    private MatchProcessor matchProcessor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove notification bar
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fightr_activity_lobby);
        initView();
        initListener();
        initProcessor();
    }

    private void initListener() {

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FightrApplication.getInstance().getMsgService().send(
                        PayloadUtil.toJson(PayloadBuilder.createMatchPayload(GameState.Instance.getMatch())));
            }

        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Message message = ChatProcessor.createMessage(userText.getText().toString());
                message.setType(Message.SELF_MSG);
                userMessageAdapter.addMessage(message);
                userText.getText().clear();
                FightrApplication.getInstance().getMsgService().send(
                        PayloadUtil.toJson(PayloadBuilder.createMessagePayload(message)));

                chatProcessor.scrollToLast();
            }
        });

    }

    private void initProcessor() {

        lobbyProcessor = new LobbyProcessor(this);
        chatProcessor = new ChatProcessor(this);
        partyProcessor = new PartyProcessor(this);
        matchProcessor = new MatchProcessor(this);

        FighterPayloadProcessorRegistry registry = ((FightrApplication) getApplication()).getProcessorRegistry();
        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.UserEvent),
                lobbyProcessor);

        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.User),
                lobbyProcessor);

        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.ChatEvent),
                chatProcessor);

        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.Fighter),
                partyProcessor);

        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.MatchEvent),
                matchProcessor);
    }

    private void initView() {

        emojiButton = (ImageView) findViewById(R.id.emojiButton);
        sendButton = (ImageView) findViewById(R.id.lobby_msg_send);
        userText = (EditText) findViewById(R.id.lobby_msg_user_msg);

        // User List
        GameState.Instance.clearActiveCaches();
        List<User> users = GameState.Instance.getActiveUsers();
        userListAdapter = new UserListRecyclerAdapter(this, users);
        userListView = (RecyclerView) findViewById(R.id.fightr_lobby_user_list);
        userListView.setLayoutManager(new LinearLayoutManager(this));
        userListView.setAdapter(userListAdapter);

        // User Message List
        List<Message> messages = new ArrayList<>();
        userMessageAdapter = new UserMsgRecyclerAdapter(this, messages);
        messagesListview = (RecyclerView) findViewById(R.id.fightr_lobby_user_messages);
        messagesListview.setLayoutManager(new LinearLayoutManager(this));
        messagesListview.setAdapter(userMessageAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WebSocketService msgService = FightrApplication.getInstance().getMsgService();
        if (msgService.isActive()) {
            debugAsChat("onResume", "Requesting User List");
            msgService.send(PayloadUtil.toJson(PayloadBuilder.createGetAllUserPayload()));
        } else {
            debugAsChat("onResume", "Connection is not active. Reconnecting...");
            msgService.start();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        GameState.Instance.saveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
    }

    public UserListRecyclerAdapter getUserListAdapter() {
        return userListAdapter;
    }

    public UserMsgRecyclerAdapter getUserMessageAdapter() {
        return userMessageAdapter;
    }

    public RecyclerView getUserListView() {
        return userListView;
    }

    public RecyclerView getMessagesListview() {
        return messagesListview;
    }

    public LobbyProcessor getLobbyProcessor() {
        return lobbyProcessor;
    }

    public ChatProcessor getChatProcessor() {
        return chatProcessor;
    }

    public PartyProcessor getPartyProcessor() {
        return partyProcessor;
    }

    public void debugAsChat(String context, String text) {

        final Message message = ChatProcessor.createMessage(text);
        message.setType(Message.SYSTEM_MSG);
        message.setUsername(context);
        userMessageAdapter.addMessage(message);
    }
}
