package com.bytes.fightr.client.controller.listener;

import android.widget.TextView;

import com.bytes.fightr.client.controller.FightrWebSocketListener;
import com.bytes.fightr.client.controller.battle.FightrMainActivity;
import com.bytes.fightr.client.controller.battle.FightrProcessorListener;
import com.bytes.fightr.client.controller.lobby.processor.ChatProcessor;
import com.bytes.fightr.client.controller.lobby.FightrLobbyActivity;
import com.bytes.fightr.client.controller.lobby.processor.LobbyProcessor;
import com.bytes.fightr.client.controller.lobby.processor.MatchProcessor;
import com.bytes.fightr.client.controller.login.processor.LoginProcessor;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.logic.processor.FighterPayloadProcessorRegistry;
import com.bytes.fightr.client.service.comm.WebSocketClient;
import com.bytes.fightr.client.service.logger.Logger;
import com.bytes.fightr.client.util.ClientPayloadUtil;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.model.User;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.ProcessorListener;
import com.google.gson.Gson;

import junit.framework.Assert;

import org.glassfish.tyrus.client.ClientManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;

import mockit.Expectations;
import mockit.Mocked;

/**
 * Created by Kent on 4/14/2017.
 */
public class PayloadControllerLiveTest {

    private static FightrWebSocketListener listener;

    private static WebSocketClient wsClient;

    @Mocked
    private static Logger logger;

    @Mocked
    private FightrMainActivity fightrMainActivity;

    @Mocked
    private TextView textView;

    private FighterPayloadProcessorRegistry registry;


    private static ProcessorListener processorListener;
    private Gson gson = new Gson();

    /**
     * Establish connection to server
     */
    @Before
    public void init() throws URISyntaxException, IOException, DeploymentException {

        new Expectations() {{
            fightrMainActivity.getDebugText();
            result = textView;
        }};

        processorListener = new FightrProcessorListener(fightrMainActivity);

        // Create the connection
//        String location = "ws://localhost:8080/server/fightr";
        String location = "ws://thinkr.azurewebsites.net/server/fightr";

        registry = new FighterPayloadProcessorRegistry();
        listener = new FightrWebSocketListener(registry);
        wsClient = new WebSocketClient(listener);
        ClientManager client = ClientManager.createClient();
        client.connectToServer(wsClient, ClientEndpointConfig.Builder.create().build(), new URI(location));

        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.UserEvent),
                new LobbyProcessor(new FightrLobbyActivity()));

        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.ChatEvent),
                new ChatProcessor(new FightrLobbyActivity()));

    }

    /**
     * Check the connection to the server
     *
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void connectTest() throws Exception {


        int retries = 5;
        int attempts = 0;
        while (!listener.getSession().isOpen() && attempts <= retries) {
            Thread.sleep(500);
            attempts++;
        }

        Assert.assertTrue(listener.getSession().isOpen());
//        Thread.sleep(1000);

        send(
                PayloadUtil.toJson(

                        PayloadBuilder.createRegisterUserPayload(GameState.Instance.getUser())));
//        Thread.sleep(1000);
        send(
                PayloadUtil.toJson(
                        PayloadBuilder.createRegisterFighterPayload(GameState.Instance.getUserFighter())));
//        Thread.sleep(1000);
        send(
                PayloadUtil.toJson(
                        PayloadBuilder.createGetUserPayload(GameState.Instance.getUser().getId())));

// TODO Phase 2a
//        send(gson.toJson(createFighterPayload()));
//        Thread.sleep(2000);
//        send(gson.toJson(createMatchPayload()));
//        Thread.sleep(2000);
//        send(gson.toJson(createReadyPayload()));
//        Thread.sleep(1000);
//
//        send(gson.toJson(createActionPayload(FighterSkill.Id.Attack)));
//        Thread.sleep(1000);
//        send(gson.toJson(createActionPayload(FighterSkill.Id.Attack)));
//        Thread.sleep(1000);
//        send(gson.toJson(createActionPayload(FighterSkill.Id.Attack)));
//        Thread.sleep(1000);

    }

    private FighterPayload createFighterPayload() throws Exception {
        return ClientPayloadUtil.createFighterPayload(GameState.Instance.getUserFighter());
    }

    private FighterPayload createMatchPayload() {
        Fighter fighter = GameState.Instance.getUserFighter();
        Fighter target = GameState.Instance.getTargetFighter();
        return null; //MatchProcessor.createMatchPayload(fighter.getId(), target.getId());
    }

    /**
     * @return
     */
    private FighterPayload createReadyPayload() {
        User user = GameState.Instance.getUser();
        user.setStatus(User.Status.Ready);
        return PayloadBuilder.createReadyPayload(user);
    }

    private FighterPayload createActionPayload(FighterSkill.Id skill) {
        Fighter fighter = GameState.Instance.getUserFighter();
        Fighter target = GameState.Instance.getTargetFighter();
        return ClientPayloadUtil.createActionPayload(fighter.getId(), target.getId(), skill);
    }

    /**
     * @param message
     */
    private void send(final String message) throws IOException {
        listener.getSession().getBasicRemote().sendText(message);
    }
}