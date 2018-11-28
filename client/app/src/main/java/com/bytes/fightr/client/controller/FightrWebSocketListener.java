package com.bytes.fightr.client.controller;

import com.bytes.fightr.client.logic.processor.FighterPayloadProcessorRegistry;
import com.bytes.fightr.client.service.comm.WebSocketListener;
import com.bytes.fightr.client.service.comm.WebSocketService;
import com.bytes.fightr.client.service.logger.Logger;
import com.bytes.fightr.client.service.logger.LoggerFactory;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.event.EventListener;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * Created by Kent on 10/14/2016.
 * Default implementation of the WebSocket listener for the client
 */

public class FightrWebSocketListener implements WebSocketListener {

    private Logger logger = LoggerFactory.getLogger(FightrWebSocketListener.class);

    private Gson gson;

    private Session session;
    private WebSocketService msgService;
    private FighterPayloadProcessorRegistry processorRegistry;
    private Set<EventListener> listeners;

    /**
     * Default constructor.
     */
    public FightrWebSocketListener(FighterPayloadProcessorRegistry processorRegistry) {
        this.gson = new Gson();
        this.processorRegistry = processorRegistry;
        this.listeners = new HashSet<>();
    }


    @Override
    public void onOpen(Session session, EndpointConfig config) {
        setSession(session);
        notify("Connected to Server.\n");
    }


    /**
     * Process the json message from the server.
     *
     * @param message the message from the server
     * @param session the established session
     */
    @Override
    public void onMessage(final String message, Session session) {

        Payload<String> responsePayload = null;
        FighterPayload payload = null;
        try {
            // 1. Parse incoming payload
            payload = gson.fromJson(message, PayloadUtil.getClass(message));
            payload.setSourceId(session.getId());
            payload.addDestination(session.getId());

        } catch (JsonSyntaxException e) {
            notify("Unable to parse Json payload " + e.getMessage());
        }

        if (payload == null) {
            notify("Unable to parse Json payload");
            return;
        }

        String processorName;
        try {
            // 2. Validate payload
            processorName = processorRegistry.getName(payload.getType(), payload.getDataType());
            AbstractPayloadProcessor processor = processorRegistry.getProcessor(processorName);

            if (!processor.validate(payload)) {
                notify(String.format("Unable to validate request: %1$s", payload.getType()));
            } else {

                // 3. Process payload
                responsePayload = processor.process(payload);
            }

            // 4. Send response payload
            if (responsePayload != null) {
                send(responsePayload);
            }

        } catch (Exception e) {
            notify("Unable to process payload:" + message);
        }
    }


    @Override
    public void onClose(String message) {
        notify(message);
    }

    @Override
    public void onError(String message) {
        notify(message);
    }


    /**
     * Request to send the specified {@code Payload} to the lists of specified {@code Session}
     *
     * @param payload - the payload to be sent
     */
    public void send(Payload<String> payload) {

        if (payload == null) {
            logger.warn("request to send payload but payload is null");
            return;
        }

        payload.setSourceId(session.getId());
        msgService.send(gson.toJson(payload));
    }

    /**
     * Notify all listeners
     *
     * @param message the message
     */
    private void notify(String message) {
        for (EventListener listener : listeners) {
            listener.onEvent(message);

        }
    }

    /**
     * Add the specified listener
     *
     * @param listener the listener to be added
     * @return true if the listener was added
     */
    public boolean addEventListener(EventListener listener) {

        return listener != null && listeners.add(listener);
    }

    /**
     * Remove the specified listener
     *
     * @param listener the listener to be removed
     * @return true if the listener was removed
     */
    public boolean removeListener(EventListener listener) {

        return listener != null && listeners.remove(listener);
    }

    public void setMsgService(WebSocketService msgService) {
        this.msgService = msgService;
    }

    private void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

}
