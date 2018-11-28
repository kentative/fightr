package com.bytes.fightr.common.model.skill;

import java.util.HashMap;
import java.util.Map;

public class SkillRegistry {

	private Map<FighterSkill.Id, FighterSkill> skills;
	
	private static SkillRegistry instance;
	
	public static SkillRegistry getInstance() {
		if (instance == null) {
			instance = new SkillRegistry();
		}
		return instance;
	}
	
	public SkillRegistry() {
		skills = new HashMap<FighterSkill.Id, FighterSkill>();
		skills.put(FighterSkill.Id.Attack, new Attack());
		skills.put(FighterSkill.Id.BlindingFlash, new BlindingFlash());
		skills.put(FighterSkill.Id.Block, new Block());
		skills.put(FighterSkill.Id.ChargedAttack, new ChargedAttack());
		skills.put(FighterSkill.Id.Disrupt, new Disrupt());
		skills.put(FighterSkill.Id.Reload, new Reload());
	}
	
	public FighterSkill getSkill(FighterSkill.Id id) {
		return skills.get(id);
	}
}
