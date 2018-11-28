package com.bytes.fightr.common.model.skill;

import java.util.ArrayList;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Fighter.State;
import com.bytes.gamr.model.avatar.AvatarPower;
import com.bytes.gamr.model.avatar.skill.Skill;

public class Disrupt extends FighterSkill {

	@Override
	protected void initializeSkillParameters() {
		
		name = "Disrupt";
		description = "Interrupt target action";
		powerCosts = new ArrayList<AvatarPower>();

		powerCosts.add(new AvatarPower(AvatarPower.Type.ENERGY_STAMINA, 2));
		
		skillCooldown = 1;
		skillDelayCost = 0;
		skillDuration = 0;
		turnCost = 1000;
		
	}

	@Override
	protected void applySkillEffects(Fighter source, Fighter target) {
		
		source.setState(State.Attacking);
		Skill<Fighter> activeSkill = SkillRegistry.getInstance().getSkill(target.getActiveSkill());
		if (activeSkill != null) {
			if (activeSkill.isInterruptable()) {
				target.setState(State.Interrupted);
			}
		}
		source.setState(State.None);
	}

}
