package com.bytes.fightr.common.model;

import java.util.ArrayList;
import java.util.List;

public class Team {

	public enum Id {
		Team01, Team02, Team03, Team04
	}

	private Id id;
	
	/** The list of fighters participating in this {@Code Match} */
	private List<String> fighters;

	public Team(Id id) {
		this.id = id;
		fighters = new ArrayList<String>();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.id + " = {");
		for (String f : fighters) {
			sb.append(f + ", ");
		}
		if (sb.length() > 2) {
			return sb.substring(0, sb.length()-2) + "}";
		}
		return sb.toString();
	}
	
	/**
	 * Adds the specified fighter to this team.
	 * @param fighter - The fighter to be added
	 * @return - True if the fighter was added to the team
	 */
	public boolean addFighter(String fighter) {
		if (!fighters.contains(fighter)) {
			return fighters.add(fighter);
		}
		return true;
	}
	
	/**
	 * @return the id
	 */
	public Id getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Id id) {
		this.id = id;
	}

	/**
	 * @return the fighters
	 */
	public List<String> getFighters() {
		return fighters;
	}

	/**
	 * @param fighters
	 *            the fighters to set
	 */
	public void setFighters(List<String> fighters) {
		this.fighters = fighters;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof Team)) {
			return false;
		}
		
		Team t = (Team) obj;
		return 
		(
			(this.getId() == (t.getId())) &&
			(this.getFighters().equals(t.getFighters()))
		);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
