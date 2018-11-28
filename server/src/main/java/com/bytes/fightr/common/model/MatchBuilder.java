package com.bytes.fightr.common.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MatchBuilder {

	private Set<String> team1;
	private Set<String> team2;
	
	private Match match;
	
	public MatchBuilder() {
		this.match = new Match();
		this.team1 = new HashSet<String>();
		this.team2 = new HashSet<String>();
	}
	
	public MatchBuilder reset() {
		match = new Match(match.getId());
		this.team1 = new HashSet<String>();
		this.team2 = new HashSet<String>();
		return this;
	}
	
	public Match create(String hostId) {
		
		Team t1 = new Team(Team.Id.Team01);
		Team t2 = new Team(Team.Id.Team02);
		
		for (String id : team1) {t1.addFighter(id);}
		for (String id : team2) {t2.addFighter(id);}
		
		match.setHost(hostId);
		match.addTeam(t1);
		match.addTeam(t2);
		return match;
	}

	public MatchBuilder addToTeam1(String...fighters) {
		if (fighters != null && fighters.length > 0) {
			team2.removeAll(Arrays.asList(fighters));
			team1.addAll(Arrays.asList(fighters));
		}
		return this;
	}
	
	public MatchBuilder addToTeam2(String...fighters) {
		if (fighters != null && fighters.length > 0) {
			team1.removeAll(Arrays.asList(fighters));
			team2.addAll(Arrays.asList(fighters));
		}
		return this;
	}

	public MatchBuilder setMaxRound(int maxRound) {
		match.setMaxRound(maxRound);
		return this;
	}

}
