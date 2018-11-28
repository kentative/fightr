package com.bytes.fightr.common.model.skill;

import java.util.ArrayList;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.gamr.model.avatar.AvatarPower;

public class Block extends FighterSkill {

	@Override
	protected void initializeSkillParameters() {
		
		name = "Block";
		description = "Blocks an attack.";
		powerCosts = new ArrayList<AvatarPower>();

		// Cost 1 stamina
		powerCosts.add(new AvatarPower(AvatarPower.Type.ENERGY_STAMINA, 1));
		
		skillCooldown = 1;
		skillDelayCost = 0;
		skillDuration = 0;
		turnCost = 500;
		
	}

	@Override
	protected void applySkillEffects(Fighter source, Fighter target) {
		int damage = 0;
		
		// Maintain blocking state until attacked
		source.setState(State.Blocking);
		applyDamage(target, damage);
	}

}
