package com.bytes.fightr.common.model.skill;

import java.util.ArrayList;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.gamr.model.avatar.AvatarPower;

public class Reload extends FighterSkill {

	@Override
	protected void initializeSkillParameters() {
		
		name = "Reload";
		description = "Reload ammo.";
		powerCosts = new ArrayList<AvatarPower>();

		// Cost 0 stamina
		powerCosts.add(new AvatarPower(AvatarPower.Type.ENERGY_STAMINA, 0));
		
		skillCooldown = 1;
		skillDelayCost = 0;
		skillDuration = 0;
		turnCost = 1000;
		
	}

	@Override
	protected void applySkillEffects(Fighter source, Fighter target) {
		
		source.setState(State.Preparing);
		source.addToPower(AvatarPower.Type.ENERGY_STAMINA, 1);
		source.setState(State.None);
		
	}

}
