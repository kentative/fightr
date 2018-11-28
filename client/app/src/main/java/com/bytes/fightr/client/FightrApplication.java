package com.bytes.fightr.client;

import android.app.Application;
import android.os.Handler;

import com.bytes.fightr.client.controller.FightrWebSocketListener;
import com.bytes.fightr.client.logic.GameLogic;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.logic.processor.FighterPayloadProcessorRegistry;
import com.bytes.fightr.client.service.comm.WebSocketService;

/**
 * Created by Kent on 4/25/2017.
 * This class holds shared services for the Fightr Game ...
 */
public class FightrApplication extends Application {

    private static FightrApplication instance;

    public static volatile Handler applicationHandler = null;

    // Services
    private WebSocketService msgService;

    // Controller
    private FightrWebSocketListener webSocketListener;

    // Processor Registry
    private FighterPayloadProcessorRegistry processorRegistry;

    // Game State
    public static FightrApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        applicationHandler = new Handler(getInstance().getMainLooper());

        initService();
        initGame();
    }

    /**
     * Initialize game states
     */
    private void initGame() {

        GameState game = GameState.Instance;
        game.loadInstanceState();
        if (game.getUser() == null) {
            GameLogic.Instance.build(game, false);
        }

    }

    /**
     * Initialize services
     */
    private void initService() {

        processorRegistry = new FighterPayloadProcessorRegistry();
        webSocketListener = new FightrWebSocketListener(processorRegistry);
        msgService = new WebSocketService(webSocketListener);
        webSocketListener.setMsgService(msgService);

    }

    /**
     * For debug and development only.
     * Request to reset the client game state (i.e., erase savedBundle states)
     * and reinitialize as if you are installing the game for the first time.
     */
    public void reset() {
        GameLogic.Instance.build(GameState.Instance, true);
    }

    public WebSocketService getMsgService() {
        return msgService;
    }

    public FightrWebSocketListener getWebSocketListener() {
        return webSocketListener;
    }

    public FighterPayloadProcessorRegistry getProcessorRegistry() {
        return processorRegistry;
    }

}
