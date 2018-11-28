package com.bytes.fightr.common.model;

import java.util.ArrayList;
import java.util.List;

public class Round {

	/** The maximum number of actions per round per player **/
	private int duration;
	
	/** The winners for this {@code Round} */
	private List<String> winners;
	
	/** Default constructor */
	public Round() {
		
		this.duration = 60;
		this.winners = new ArrayList<>();
	}
	
	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the winners
	 */
	public List<String> getWinners() {
		return winners;
	}

	/**
	 * @param winners the winners to set
	 */
	public void setWinners(List<String> winners) {
		this.winners = winners;
	}
	
}
