package com.bytes.fightr.client.controller.battle;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bytes.fightr.R;
import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.client.controller.FightrGaugeController;
import com.bytes.fightr.client.controller.FightrMenuController;
import com.bytes.fightr.client.controller.FightrSkillBarController;
import com.bytes.fightr.client.logic.GameLogic;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.service.comm.WebSocketService;
import com.bytes.fightr.client.settings.SettingsActivity;
import com.bytes.fightr.client.settings.SettingsController;
import com.bytes.fightr.client.widget.spin.SpinLayout;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarPower;

public class FightrMainActivity extends AppCompatActivity {


    // Views
    // Source fighter (player)
//    private ImageView sourceImage;
    private TextView sourceFighterName;
    private ProgressBar sourceFighterHp;
    private ProgressBar sourceFighterAp;
    private ProgressBar sourceActionGauge;
    private TextView sourceFighterText;

    // Target fighter (opponent)
//    private ImageView targetImage;
    private TextView targetFighterName;
    private ProgressBar targetFighterHp;
    private ProgressBar targetFighterAp;
    private ProgressBar targetActionGauge;
    private TextView targetFighterText;

    private TextView fightrDebugText;

    private SpinLayout spinLayout;

    // Controller and Listeners
    private FightrGaugeController fightrGaugeController;
    private FightrMenuController fightrMenuController;

    private FightrSkillBarController skillbarListener;
    private FightrSkillTouchEventListener skillListener;
    private FightrProcessorListener fightrProcessorListener;
    private WebSocketService fightrMsgService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fightr_nav_drawer);

        initView();
        initService();

    }

    /**
     * Initialize all views in this activity
     */
    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new FightrNavListener(this));

        fightrDebugText = (TextView) findViewById(R.id.fightr_debug_text);

        // source fighter
        View sourceFighterView = findViewById(R.id.fightr_include_fighter_header_source);
//        sourceImage = (ImageView) sourceFighterView.findViewById(R.id.fightr_header_image);
        sourceFighterName = (TextView) sourceFighterView.findViewById(R.id.fightr_header_name);
        sourceFighterHp = (ProgressBar) sourceFighterView.findViewById(R.id.fightr_lobby_msg_user_msg);
        sourceFighterAp = (ProgressBar) sourceFighterView.findViewById(R.id.fightr_lobby_msg_user_msg_time);
        sourceActionGauge = (ProgressBar) sourceFighterView.findViewById(R.id.fightr_header_action_gauge);
        sourceFighterText = (TextView) sourceFighterView.findViewById(R.id.fightr_header_text_actions);


        // target fighter
        View targetFighterView = findViewById(R.id.fightr_include_fighter_header_target);
//        targetImage = (ImageView) targetFighterView.findViewById(R.id.fightr_header_image);
        targetFighterName = (TextView) targetFighterView.findViewById(R.id.fightr_header_name);
        targetFighterHp = (ProgressBar) targetFighterView.findViewById(R.id.fightr_lobby_msg_user_msg);
        targetFighterAp = (ProgressBar) targetFighterView.findViewById(R.id.fightr_lobby_msg_user_msg_time);
        targetActionGauge = (ProgressBar) targetFighterView.findViewById(R.id.fightr_header_action_gauge);
        targetFighterText = (TextView) targetFighterView.findViewById(R.id.fightr_header_text_actions);

        spinLayout = (SpinLayout) findViewById(R.id.spin_layout);

//        ImageView imageView = (ImageView) findViewById(R.id.fightr_heart);
//        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
//        imageView.startAnimation(pulse);

        // Controllers and Listeners
        skillbarListener = new FightrSkillBarController(this);
        spinLayout.setListener(skillbarListener);
        skillListener = new FightrSkillTouchEventListener(this);
        for (int i = 0; i < spinLayout.getChildCount(); i++) {
            spinLayout.getChildAt(i).setOnTouchListener(skillListener);
        }

        fightrGaugeController = new FightrGaugeController(this);
        fightrGaugeController.setActionController(skillbarListener);
        fightrMenuController = new FightrMenuController(this);
        fightrProcessorListener = new FightrProcessorListener(this);

    }

    /**
     * Initialize all services used in this activity
     */
    private void initService() {

        FightrApplication application = (FightrApplication) getApplication();
//        TODO application.getWebSocketListener().addProcessorListener(fightrProcessorListener);

        fightrMsgService = application.getMsgService();
        fightrGaugeController.setMsgService(fightrMsgService);

        AudioAttributes attribute = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();

        SoundPool soundPool = new SoundPool.Builder().setMaxStreams(3)
                .setAudioAttributes(attribute).build();
    }


    /**
     * Connects the user to the server.
     * Authentication tbd
     */
    private void login(final String address) {

        if (fightrMsgService != null && fightrMsgService.isActive()) {
            Snackbar.make(spinLayout, R.string.login_connection_active, Snackbar.LENGTH_LONG);
            return;
        }

        if (address.isEmpty()) {
            Snackbar.make(spinLayout, R.string.server_url_not_defined, Snackbar.LENGTH_LONG);
        }
        fightrMsgService.setServerUrl(address);
        fightrMsgService.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fightr_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

            case R.id.action_settings:
                //Code to run when the Create Order item is clicked
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_register:
                fightrMenuController.registerFighter();
                break;

            case R.id.action_ready:
                fightrMenuController.prepareMatch();
                fightrMenuController.readyMatch();
//                fightrSoundPool.play(R.raw.heartbeat, 1f, 1f, 0, -1, 1f);
                break;

            case R.id.action_select_fighter:
                break;

            case R.id.action_debug_server:
                fightrMenuController.reset();
                break;

            case R.id.action_debug_latency:
                fightrMenuController.computeLatency();
                break;


            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // re-established connection
        boolean isConnected = SettingsController.isConnected(getApplicationContext());
        if (!fightrMsgService.isActive() && isConnected) {
            Toast.makeText(getApplicationContext(), "Starting connection...", Toast.LENGTH_LONG).show();
            login(SettingsController.getServerUrl(getApplicationContext()));
        }

        updateSourceStatus();
        updateTargetStatus();
    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }

    public void updateSourceFighterAction(final FightAction action) {

        updateSourceStatus();
        if (action != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sourceFighterText.setText(action.getSkill().name());
                }
            });
        }
    }

    public void updateTargetFighterAction(final FightAction action) {
        updateTargetStatus();
        if (action != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    targetFighterText.setText(action.getSkill().name());
                }
            });
        }
    }

    public void updateSourceStatus() {

        final Fighter fighter = GameState.Instance.getUserFighter();
        if (fighter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                sourceImage.setImageResource(R.drawable.ow_icon_hanzo); // hardcode this for now
                sourceFighterName.setText(fighter.getName());
                sourceFighterHp.setProgress(fighter.getAttributes(AvatarAttribute.Type.HEALTH).getValue());
                sourceFighterAp.setProgress(fighter.getPower(AvatarPower.Type.ENERGY_STAMINA).getValue());
//                sourceActionGauge; TODO
            }
        });
    }

    public void updateTargetStatus() {

        final Fighter fighter = null; // TODO GameLogic.Instance;
        if (fighter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                targetImage.setImageResource(R.drawable.ow_icon_hanzo); // hardcode this for now
                targetFighterName.setText(fighter.getName());
                targetFighterHp.setProgress(fighter.getAttributes(AvatarAttribute.Type.HEALTH).getValue());
                targetFighterAp.setProgress(fighter.getPower(AvatarPower.Type.ENERGY_STAMINA).getValue());
//                targetActionGauge; TODO
            }
        });
    }

    public ProgressBar getActionGauge() {
        return sourceActionGauge;
    }

    public WebSocketService getFightrMsgService() {
        return fightrMsgService;
    }

    public FightrMenuController getFightrMenuController() {
        return fightrMenuController;
    }

    public FightrGaugeController getFightrGaugeController() {
        return fightrGaugeController;
    }

    public TextView getDebugText() {
        return fightrDebugText;
    }

    public ProgressBar getSourceActionGauge() {
        return sourceActionGauge;
    }

    public FightrSkillBarController getSkillbarController() {
        return skillbarListener;
    }
}
