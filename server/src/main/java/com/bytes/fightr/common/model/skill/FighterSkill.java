package com.bytes.fightr.common.model.skill;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.gamr.model.avatar.skill.Skill;

/**
 * Fighter skills has a counter effect, (i.e., an attack is nullify if the target blocks)
 * Note: do not make this class abstract unless there is a {@code InstanceCreator} 
 * defined for GsonBuilder
 */
public class FighterSkill extends Skill<Fighter> {

	public enum Id {
		Attack,
		BlindingFlash,
		Block,
		ChargedAttack,
		Disrupt,
		Reload
	}
	
	@Override
	protected void initializeSkillParameters() {
		
	}

	@Override
	protected void applySkillEffects(Fighter source, Fighter target) {
		
	}
}
