package com.bytes.gamr.model.avatar.skill.archer;

import java.util.ArrayList;

import com.bytes.gamr.model.avatar.Avatar;
import com.bytes.gamr.model.avatar.AvatarPower;
import com.bytes.gamr.model.avatar.skill.Skill;

public class QuickShot extends Skill<Avatar> {

	@Override
	protected void initializeSkillParameters() {
		name = "Quick Shot";
		description = "A quick bow attack. Damage is based on adrenaline. Does not end turn.";
		powerCosts = new ArrayList<AvatarPower>();
		powerCosts.add(new AvatarPower(AvatarPower.Type.MANA_RED, 1));
		
		skillCooldown = 0;
		skillDelayCost = 0;
		skillDuration = 0;
		turnCost = 0;
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
