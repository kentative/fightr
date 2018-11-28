package com.bytes.gamr.model.avatar.skill.knight;

import java.util.ArrayList;

import com.bytes.gamr.model.avatar.Avatar;
import com.bytes.gamr.model.avatar.AvatarPower;
import com.bytes.gamr.model.avatar.skill.Skill;

public class SwordSlash extends Skill<Avatar> {

	@Override
	protected void initializeSkillParameters() {
		name = "Sword Slash";
		description = "A melee attack with the sharp edge of the sword.";
		powerCosts = new ArrayList<AvatarPower>();
		powerCosts.add(new AvatarPower(AvatarPower.Type.ENERGY_ADRENALINE, 5));
		
		skillCooldown = 0;
		skillDelayCost = 0;
		skillDuration = 0;
		turnCost = 1;
	}
	
	/**
	 * 
	 */
	@Override
	public void applySkillEffects(Avatar source, Avatar target) {
		
		// get damage from this attribute
		AvatarPower attributes = source.getPower(AvatarPower.Type.ENERGY_ADRENALINE);
		
		// apply damage
		applyDamage(target, (attributes.getValue()));
	}

}
