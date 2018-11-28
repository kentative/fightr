package com.bytes.gamr.model.avatar.skill.archer;

import java.util.ArrayList;

import com.bytes.gamr.model.avatar.Avatar;
import com.bytes.gamr.model.avatar.AvatarPower;
import com.bytes.gamr.model.avatar.skill.Skill;

public class Snipe extends Skill<Avatar> {

	@Override
	protected void initializeSkillParameters() {
		
		name = "Snipe";
		description = "A bow attack that hit the target weakspot. Can be interrupted.";
		powerCosts = new ArrayList<AvatarPower>();
		powerCosts.add(new AvatarPower(AvatarPower.Type.MANA_RED, 3));
		
		skillCooldown = 1;  // 1 - takes an additional turns to reactivate
		skillDelayCost = 1; // 1 - turn before effects are applied
		skillDuration = 0;  // 0 - when applied, instant effects
		turnCost = 1;		// 1 - can only be used once per turn, ends turn
		
		interruptable = true; //
		
	}

	@Override
	protected void applySkillEffects(Avatar source, Avatar target) {
		
		int bowDamage = 10;
		
		applyDamage(target, bowDamage * 2);
	}

}
