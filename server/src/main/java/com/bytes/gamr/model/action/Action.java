package com.bytes.gamr.model.action;

import java.util.List;

import com.bytes.fightr.common.model.skill.FighterSkill;

public class Action {

	private FighterSkill.Id skill;
	
	/**
	 * The fighter initiating the action.
	 */
	private String source;

	/**
	 * The action's target(s), can select multiple targets
	 */
	private List<String> targets;
	
	
	public boolean isValid() {
		return (source != null && targets != null && skill != null ); 
	}

	/**
	 * @return the skill
	 */
	public FighterSkill.Id getSkill() {
		return skill;
	}

	/**
	 * @param skill the skill to set
	 */
	public void setSkill(FighterSkill.Id skill) {
		this.skill = skill;
	}
	
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the targets
	 */
	public List<String> getTargets() {
		return targets;
	}

	/**
	 * @param targets the targets to set
	 */
	public void setTargets(List<String> targets) {
		this.targets = targets;
	}
}
