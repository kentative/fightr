package com.bytes.fightr.client.controller.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bytes.fightr.R;
import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.client.controller.login.processor.LoginProcessor;
import com.bytes.fightr.client.logic.GameLogic;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.logic.processor.FighterPayloadProcessorRegistry;
import com.bytes.fightr.client.service.comm.WebSocketService;
import com.bytes.fightr.client.settings.SettingsController;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadBuilder;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.event.EventListener;
import com.bytes.fmk.model.User;
import com.bytes.fmk.payload.Payload;

/**
 * Created by Kent on 4/23/2017.
 */
public class FightrLoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private Button resetButton;

    private TextView statusTextView;
    private EditText fighterNameView;

    private LoginProcessor loginProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove notification bar
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fightr_activity_login);

        initView();
        initService();
        initGame();
        initProcessor();
    }

    private void initProcessor() {

        loginProcessor = new LoginProcessor(this);
        FighterPayloadProcessorRegistry registry = ((FightrApplication) getApplication()).getProcessorRegistry();
        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.String),
                loginProcessor);

        registry.registerProcessor(
                registry.getName(Payload.NOTIFY, FighterPayload.DataType.LoginEvent),
                loginProcessor);
    }

    private void initGame() {
        final GameState game = GameState.Instance;
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        fighterNameView.setText(game.getUser().getDisplayName());
                    }
                }
        );
    }

    /**
     * Initialize all views in this activity
     */
    private void initView() {

        fighterNameView = (EditText) findViewById(R.id.fightr_login_text);

        registerButton = (Button) findViewById(R.id.fightr_login_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterClick();
            }
        });

        loginButton = (Button) findViewById(R.id.fightr_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });

        resetButton = (Button) findViewById(R.id.fightr_login_reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResetClick();
            }
        });
        statusTextView = (TextView) findViewById(R.id.fightr_login_status);

        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        loginButton.startAnimation(pulse);

    }

    private void onResetClick() {
        FightrApplication.getInstance().reset();
    }

    private void onRegisterClick() {

        // Retrieve existing user from saved state
        GameState game = GameState.Instance;
        String userId = fighterNameView.getText().toString();
        User existingUser = game.getUser(userId);
        if (existingUser != null && game.isUserRegistered()) {
            DisplayUtil.toast(this, userId + " is already registered");
            return;
        }

        // Update user name
        User user = game.getUser();
        user.setDisplayName(fighterNameView.getText().toString());
        user.setId(user.getDisplayName()); // TODO this is temp - to enforce different names

        // Must use a different avatar
        Fighter newFighter = GameLogic.Instance.createDefaultFighter(user);
        newFighter.linkUser(user);
        game.setUser(user);
        game.addFighter(newFighter);

        // Register User
        Payload<String> userPayload = PayloadBuilder.createRegisterUserPayload(user);
        WebSocketService msgService = FightrApplication.getInstance().getMsgService();
        msgService.send(PayloadUtil.getGson().toJson(userPayload));

        // Register Avatar associated with the user
        Payload<String> avatarPayload = PayloadBuilder.createRegisterFighterPayload(game.getUserFighter());
        msgService.send(PayloadUtil.getGson().toJson(avatarPayload));
    }

    private void onLoginClick() {

        // Retrieve existing user from saved state
        String userId = fighterNameView.getText().toString();
        GameState game = GameState.Instance;
        User existingUser = game.getUser(userId);

        if (existingUser == null) {
            DisplayUtil.toast(this, "User " + userId + " not found.");
            return;
        }

        // Login
        Payload<String> payload = PayloadBuilder.createLoginUserPayload(existingUser);
        FightrApplication.getInstance().getMsgService().send(PayloadUtil.getGson().toJson(payload));
    }

    /**
     * Initialize service dependencies used in this activity
     */
    private void initService() {

        FightrApplication application = (FightrApplication) getApplication();
        application.getWebSocketListener().addEventListener(new EventListener() {

            @Override
            public void onEvent(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

    }

    /**
     * Connects the user to the server.
     * TODO Authentication
     *
     * @param address the server url
     */
    private void reconnect(final String address) {

        WebSocketService fightrMsgService = ((FightrApplication) getApplication()).getMsgService();
        if (fightrMsgService == null) {
            DisplayUtil.setText(this, statusTextView, getString(R.string.login_msg_service_unavailable));
            return;
        }

        if (fightrMsgService.isActive()) {
            DisplayUtil.setText(this, statusTextView, getString(R.string.login_connection_active));
            return;
        }

        if (address == null || address.isEmpty()) {
            DisplayUtil.setText(this, statusTextView, getString(R.string.login_server_url_invalid));
            return;
        }

        fightrMsgService.setServerUrl(address);
        fightrMsgService.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        WebSocketService fightrMsgService = ((FightrApplication) getApplication()).getMsgService();
        boolean isConnected = true; // TODO SettingsController.isConnected(getApplicationContext());
        if (isConnected) {
            DisplayUtil.setText(this, statusTextView, getString(R.string.login_auto_enabled));
            reconnect(SettingsController.getServerUrl(getApplicationContext()));
        } else {
            DisplayUtil.setText(this, statusTextView, getString(R.string.login_auto_disabled));
            fightrMsgService.stop();
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

    public TextView getStatusTextView() {
        return statusTextView;
    }
}
