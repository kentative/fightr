package com.bytes.fightr.server.logic;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;

public class GameState {

	public enum Event {
		MatchStart, RoundStart, RoundEnd, MatchEnd
	}

	private Event event;
	private Match match;
	private Fighter[] winners;
	private Fighter[] losers;

	/** Default constructor */
	public GameState(Event event) {
		this.event = event;
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return the match
	 */
	public Match getMatch() {
		return match;
	}

	/**
	 * @param match - the match to set
	 */
	public void setMatch(Match match) {
		this.match = match;
	}

	/**
	 * @return the winners
	 */
	public Fighter[] getWinners() {
		return winners;
	}

	/**
	 * @param winners - the winners to set
	 */
	public void setWinners(Fighter... winners) {
		this.winners = winners;
	}

	/**
	 * @return the losers
	 */
	public Fighter[] getLosers() {
		return losers;
	}

	/**
	 * @param losers - the losers to set
	 */
	public void setLosers(Fighter... losers) {
		this.losers = losers;
	}
}
