package com.bytes.fightr.client.controller.listener;

import android.app.Activity;
import android.widget.TextView;

import com.bytes.fightr.client.controller.FightrWebSocketListener;
import com.bytes.fightr.client.logic.processor.FighterPayloadProcessorRegistry;
import com.bytes.fightr.client.service.logger.Logger;
import com.bytes.fightr.client.util.ClientPayloadUtil;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.ProcessorEvent;
import com.bytes.fmk.payload.processor.ProcessorListener;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

import mockit.Expectations;
import mockit.Mocked;

/**
 * Created by Kent on 4/14/2017.
 */
public class PayloadControllerTest {

    private static FightrWebSocketListener listener;

    @Mocked
    private Activity mockedActivity;

    @Mocked
    private TextView mockedview;

    @Mocked
    private static Logger logger;

    private FighterPayloadProcessorRegistry registry;
    private ProcessorListener processorListener;

    private Gson gson = new Gson();


    private Fighter f1;
    private Fighter f2;

    private String sessionId1;
    private String sessionId2;

    @Before
    public void init() {

        this.processorListener = new ProcessorListener() {
            @Override
            public void onEvent(String message) {
                System.out.print(message);
            }

            @Override
            public <T> void onEvent(ProcessorEvent<T> event) {
                System.out.print(event.getData().toString());
            }
        };

        registry = new FighterPayloadProcessorRegistry();
        sessionId1 = "session1";
        sessionId2 = "session2";
        f1 = new Fighter("f1");
        f2 = new Fighter("f2");

    }


    @Before
    public void setup() {

    }


    @Test
    public void onOpen(@Mocked final Session session) throws Exception {

        new Expectations() {{
            session.getId();
            result = sessionId1;
        }};

        listener = new FightrWebSocketListener(registry);
        listener.onMessage(gson.toJson(createWelcomMessage()), session);

    }


    @Test
    public void onRegisterFighterMessage(@Mocked final Session session) throws Exception {
        new Expectations() {{
            session.getId();
            result = sessionId1;
        }};

        listener = new FightrWebSocketListener(registry);
        listener.onMessage(gson.toJson(createFighterPayload()), session);
    }

    @Test
    public void onClose() throws Exception {

    }

    @Test
    public void onError() throws Exception {

    }

    @Test
    public void send() throws Exception {

    }

    @Test
    public void setMsgService() throws Exception {

    }

    /**
     * @return
     */
    private Payload<String> createFighterPayload() {

        List<Fighter> fighters = new ArrayList<>();
        fighters.add(f1);
        fighters.add(f2);

        // Create the request payload
        ClientPayloadUtil.createFighterPayload(f1);
        Payload<String> payload = new FighterPayload(
                Payload.POST,
                FighterPayload.DataType.Fighter,
                gson.toJson(fighters));
        return payload;
    }

    /**
     * Creates the server welcome message
     * @return
     */
    private Payload<String> createWelcomMessage() {

        Payload<String> payload = new FighterPayload(Payload.NOTIFY, FighterPayload.DataType.String);
        payload.setData("Welcome to android test server");
        return payload;

    }

}