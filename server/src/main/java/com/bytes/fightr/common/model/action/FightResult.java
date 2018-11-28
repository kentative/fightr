package com.bytes.fightr.common.model.action;

import java.util.ArrayList;
import java.util.List;

import com.bytes.fightr.common.model.Fighter;

public class FightResult {

	/**
	 * The list of fighters with their attributes updated from the
	 * {@code FightAction}
	 * 
	 */
	private List<Fighter> fighters;
	
	/**
	 * Key = targetId
	 * Value = target action
	 */
	private List<FightAction> actions;

	/**
	 * Constructor.
	 */
	public FightResult() {
		this.fighters = new ArrayList<Fighter>();
		this.actions = new ArrayList<FightAction>();
	}
	/**
	 * @return the fighters
	 */
	public List<Fighter> getFighters() {
		return fighters;
	}

	/**
	 * @param fighters the fighters to set
	 */
	public void setFighters(List<Fighter> fighters) {
		this.fighters = fighters;
	}
	/**
	 * @return the actions
	 */
	public List<FightAction> getActions() {
		return actions;
	}
	/**
	 * @param actions the actions to set
	 */
	public void setActions(List<FightAction> actions) {
		this.actions = actions;
	}

}
