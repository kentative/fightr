package com.bytes.fightr.client.service.comm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.bytes.fightr.client.service.Persistable;
import com.bytes.fightr.client.service.logger.Logger;
import com.bytes.fightr.client.service.logger.LoggerFactory;

import org.glassfish.tyrus.client.ClientManager;

import java.net.URI;

import javax.websocket.ClientEndpointConfig;

/**
 * Created by Kent on 10/14/2016.
 */

public class WebSocketService implements Persistable {

    Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    private WebSocketClient client;

    private String serverUrl;

    private WebSocketListener listener;

    /**
     * Constructor
     */
    public WebSocketService(@NonNull WebSocketListener listener) {
        this.listener = listener;
    }


    @Override
    public void start() {

        if (client != null) {
            if (client.isOpen()) {
                logger.debug("web socket is already opened.");
                client.close();
                return;
            }
        }

        if (serverUrl == null) {
            logger.debug("server url is not defined");
            return;
        }

        final URI uri;
        try {
            uri = new URI(serverUrl);
        } catch (Exception e) {
            logger.error("URI is invalid: " + serverUrl, e);
            return;
        }

        client = new WebSocketClient(listener);
        // TODO make this retryable
        final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    // Create the connection
                    ClientManager client = ClientManager.createClient();
                    client.connectToServer(WebSocketService.this.client,
                            ClientEndpointConfig.Builder.create().build(), uri);

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                return null;
            }
        };

        asyncTask.execute();
    }


    public boolean isActive() {
        return (client != null && client.isOpen());
    }

    /**
     * Sends a message to the server.
     * @param message
     */
    public void send(String message) {
        if (client == null) {
            logger.debug("Request to send but connection is inactive.");
            return;
        }

        if (client.isOpen()) {
            client.send(message);
        } else {
            listener.onError("Connection is inactive.");
        }
    }

    public void setServerUrl(@NonNull String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public void stop() {
        if (client != null) {
            if (!client.isOpen()) {
                client.send("Connection is not active");
            } else {
                client.close();
            }
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

    }
}
