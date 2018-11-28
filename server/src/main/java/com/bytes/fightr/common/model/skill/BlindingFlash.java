package com.bytes.fightr.common.model.skill;

import java.util.ArrayList;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.gamr.model.avatar.AvatarPower;

public class BlindingFlash extends FighterSkill {

	@Override
	protected void initializeSkillParameters() {
		
		name = "A blinding attack";
		description = "An attack that blinds the target for 3 seconds.";
		powerCosts = new ArrayList<AvatarPower>();

		// Cost 2 stamina
		powerCosts.add(new AvatarPower(AvatarPower.Type.ENERGY_STAMINA, 2));
		
		skillCooldown = 1;
		skillDelayCost = 0;
		skillDuration = 5000;
		turnCost = 1000;
		
	}

	@Override
	protected void applySkillEffects(Fighter source, Fighter target) {
		
		int damage = 0;
		source.setState(State.Attacking);
		applyDamage(target, damage);
		target.setState(State.Blinded);
		source.setState(State.None);
	}

}
