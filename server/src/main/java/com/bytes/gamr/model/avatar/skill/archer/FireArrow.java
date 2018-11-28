package com.bytes.gamr.model.avatar.skill.archer;

import java.util.ArrayList;

import com.bytes.gamr.model.avatar.Avatar;
import com.bytes.gamr.model.avatar.AvatarPower;
import com.bytes.gamr.model.avatar.skill.Skill;

public class FireArrow extends Skill<Avatar> {

	@Override
	protected void initializeSkillParameters() {
		
		name = "Fire Arrow";
		description = "A bow attack. Red mana is used to imbue fire on the arrow.";
		powerCosts = new ArrayList<AvatarPower>();
		powerCosts.add(new AvatarPower(AvatarPower.Type.MANA_RED, 3));
		
		skillCooldown = 1;
		skillDelayCost = 0;
		skillDuration = 0;
		turnCost = 1;
		
	}

	@Override
	protected void applySkillEffects(Avatar source, Avatar target) {
		
		int bowDamage = 10;
		
		applyDamage(target, bowDamage + 3);
	}

}
