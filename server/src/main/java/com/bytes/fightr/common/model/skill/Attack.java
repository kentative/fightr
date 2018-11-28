package com.bytes.fightr.common.model.skill;

import java.util.ArrayList;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.gamr.model.avatar.AvatarPower;

public class Attack extends FighterSkill {

	@Override
	protected void initializeSkillParameters() {
		
		name = "Pierce Attack";
		description = "A basic attack. No mana is needed.";
		powerCosts = new ArrayList<AvatarPower>();

		// Cost 1 Stamina
		powerCosts.add(new AvatarPower(AvatarPower.Type.ENERGY_STAMINA, 1));
		
		skillCooldown = 1;
		skillDelayCost = 0;
		skillDuration = 0;
		turnCost = 500;
		
	}

	@Override
	protected void applySkillEffects(Fighter source, Fighter target) {
		
		int damage = 1;
		source.setState(State.Attacking);
		if (target.getState() != Fighter.State.Blocking) {
			applyDamage(target, damage);
			target.setState(State.None);
		}
		source.setState(State.None);
	}

}
