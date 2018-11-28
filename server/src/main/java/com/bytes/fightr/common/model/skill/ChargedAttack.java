package com.bytes.fightr.common.model.skill;

import java.util.ArrayList;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.gamr.model.avatar.AvatarPower;

public class ChargedAttack extends FighterSkill {

	@Override
	protected void initializeSkillParameters() {
		
		name = "Charged Attack";
		description = "A strong attack.";
		powerCosts = new ArrayList<AvatarPower>();

		powerCosts.add(new AvatarPower(AvatarPower.Type.ENERGY_STAMINA, 2));
		
		skillCooldown = 1;
		skillDelayCost = 0;
		skillDuration = 0;
		turnCost = 3000; // 3 seconds to activate
		interruptable = true;
		
	}

	@Override
	protected void applySkillEffects(Fighter source, Fighter target) {
		
		int damage = 5;
		if (source.getState() != State.Interrupted 
				&& source.getState() != State.Blinded
				&& target.getState() != Fighter.State.Blocking) {
			applyDamage(target, damage);
		}
		source.setState(State.None);
	}

}
