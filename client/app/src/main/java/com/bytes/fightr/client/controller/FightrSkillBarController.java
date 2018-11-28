package com.bytes.fightr.client.controller;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.bytes.fightr.R;
import com.bytes.fightr.client.controller.battle.FightrMainActivity;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.widget.image.FighterActionView;
import com.bytes.fightr.client.widget.spin.SpinLayout;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.action.FightAction;

import java.util.HashMap;

/**
 * Created by Kent on 10/16/2016.
 */

public class FightrSkillBarController implements SpinLayout.EventListener {


    private FightrMainActivity mainController;
    private FighterActionView selectedView;
    private FightAction action;
    private Fighter source;
    private Fighter target;

    private HashMap<String, FightAction> actionMap;


    public FightrSkillBarController(FightrMainActivity mainController) {
        this.mainController = mainController;
        this.actionMap = new HashMap<>();

        // Load equipped skill into SkillBar action map
        Fighter fighter = GameState.Instance.getUserFighter();
        actionMap.put(mainController.getString(R.string.fightr_skill_01), new FightAction(fighter.getEquipedSkills().get(1)));
        actionMap.put(mainController.getString(R.string.fightr_skill_02), new FightAction(fighter.getEquipedSkills().get(2)));
        actionMap.put(mainController.getString(R.string.fightr_skill_03), new FightAction(fighter.getEquipedSkills().get(3)));
        actionMap.put(mainController.getString(R.string.fightr_skill_04), new FightAction(fighter.getEquipedSkills().get(4)));
        actionMap.put(mainController.getString(R.string.fightr_skill_05), new FightAction(fighter.getEquipedSkills().get(5)));
        actionMap.put(mainController.getString(R.string.fightr_skill_06), new FightAction(fighter.getEquipedSkills().get(6)));

    }

    @Override
    public void onItemSelected(View view) {

        if (!(view instanceof FighterActionView)) {
            return;
        }

        selectedView = (FighterActionView) view;

        final String name = ((FighterActionView) view).getName();
        setAction(actionMap.get(name));
    }

    @Override
    public void onItemClick(View view) {
        String name = null;
        if (view instanceof FighterActionView) {
            name = ((FighterActionView) view).getName();
            selectedView = (FighterActionView) view;
            setAction(actionMap.get(name));
        }

        Toast.makeText(mainController.getApplicationContext(), name, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRotationFinished(View view) {
        Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2, view.getHeight() / 2);
        animation.setDuration(250);
        view.startAnimation(animation);
    }

    @Override
    public void onCenterClick() {

        if (selectedView != null) {
            String action = actionMap.get(selectedView.getName()).getSkill().name();
            Toast.makeText(mainController.getApplicationContext(), action, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRotationStart(View view) {

    }

    public FightAction getAction() {
        return action;
    }

    public FightAction getAction(FighterActionView view) {
        return actionMap.get(view.getName());
    }

    public void setAction(FightAction action) {
        this.action = action;
    }

    public Fighter getSource() {
        return source;
    }

    public void setSource(Fighter source) {
        this.source = source;
    }

    public Fighter getTarget() {
        return target;
    }

    public void setTarget(Fighter target) {
        this.target = target;
    }
}
