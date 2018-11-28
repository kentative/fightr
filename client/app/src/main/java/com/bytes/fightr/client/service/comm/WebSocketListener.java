package com.bytes.fightr.client.service.comm;


import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * Created by Kent on 10/14/2016.
 */
public interface WebSocketListener {

    void onOpen(Session session, EndpointConfig config);

    void onMessage(String message, Session session);

    void onClose(String message);

    void onError(String error);
}
