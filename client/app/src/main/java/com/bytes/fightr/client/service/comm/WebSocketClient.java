package com.bytes.fightr.client.service.comm;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.bytes.fightr.client.service.logger.Logger;
import com.bytes.fightr.client.service.logger.LoggerFactory;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 * Created by Kent on 10/11/2016.
 */
public class WebSocketClient extends Endpoint {

    private Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private Session session;

    private WebSocketListener listener;

    public WebSocketClient(@NonNull WebSocketListener listener) {
        this.listener = listener;
    }

    @Override
    public void onOpen(final Session session, EndpointConfig conf) {

        this.session = session;
        logger.debug("onOpen");
        listener.onOpen(session, conf);
        session.addMessageHandler(new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String message) {
                logger.debug("onMessage");
                listener.onMessage(message, session);
            }

        });
    }

    /**
     * Request to send a message to the server endpoint
     *
     * @param message the message to send
     */
    void send(final String message) {
        final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {

                    if (listener != null) {
                        listener.onError("Unable to send message. Connection is closed.");
                    }
                    logger.error("Unable to send message.", e);
                }
                return null;
            }
        };
        asyncTask.execute();
    }

    @Override
    public void onClose(Session session, CloseReason reason) {
        listener.onClose("Connection closed");
    }

    @Override
    public void onError(Session session, Throwable t) {
        logger.error("Connection error on session " + session.getId(), t);
    }


    /**
     * Indicates if the connection is open
     *
     * @return true if open
     */
    public boolean isOpen() {
        return session != null && session.isOpen();
    }

    /**
     * Request to close the connection.
     */
    public void close() {
        if (isOpen()) {
            try {
                if (listener != null) {
                    listener.onClose("Connection close requested from client.");
                }
                session.close();
            } catch (IOException e) {
                logger.error("Connection closed" + session.getId(), e);
            }
        }
    }
}
