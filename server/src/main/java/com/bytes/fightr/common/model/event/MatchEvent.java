package com.bytes.fightr.common.model.event;

import com.bytes.fightr.common.model.Match;

public class MatchEvent {
	
	private Match match;
	
	private Match.State state;

	public MatchEvent(Match.State state, Match match) {
		this.state = state;
		this.match = match;
	}
	
	/**
	 * @return the match
	 */
	public Match getMatch() {
		return match;
	}

	/**
	 * @param match the match to set
	 */
	public void setMatch(Match match) {
		this.match = match;
	}

	/**
	 * @return the state
	 */
	public Match.State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(Match.State state) {
		this.state = state;
	}

}
