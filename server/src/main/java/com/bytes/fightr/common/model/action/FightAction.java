package com.bytes.fightr.common.model.action;

import java.util.Arrays;

import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.gamr.model.action.Action;

public class FightAction extends Action {

	/**
	 * The action sequence.
	 * This is used for actions requiring a long activation time.
	 * Use {@value #Initiate} to indicate the start of the action sequence
	 * and {@value #Activate} to indicate that the action successfully completed.
	 */
	public enum Sequence {
		Initiate,
		
		/**
		 * Default
		 */
		Activate
	}
	
	private Sequence sequence;
	
	/*
	 * Creates a default fight action.
	 */
	public FightAction() {
		this.sequence = Sequence.Activate;
	}
	
	/**
	 * Default constructor
	 * @param skill
	 */
	public FightAction(FighterSkill.Id skill) {
		setSkill(skill);
	}
	
	/**
	 * @param targets - the targets to set
	 */
	public FightAction setTargets(String... targets) {
		setTargets(Arrays.asList(targets));
		return this;
	}

	/**
	 * @return the sequence
	 */
	public Sequence getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}
	
}
