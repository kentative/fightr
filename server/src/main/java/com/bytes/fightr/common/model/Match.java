package com.bytes.fightr.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Team.Id;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.event.MatchEvent;
import com.bytes.fmk.observer.Context;
import com.bytes.fmk.observer.IObserver;
import com.bytes.gamr.model.action.ActionQueue;

/**
 * Match
 * New > Prepared > Confirmed > Active > Completed
 * @author Kent
 *
 */
public class Match {
	

	public enum State {
		/**
		 * Default state when created 
		 * An event to indicate the host is waiting for confirmation from the target 
		 */
		New,
		
		Searching,

		/** 
		 * An event to indicate the target has decline the match invitation, 
		 * or the host has cancelled the match 
		 */
		Declined,
			
		/**
		 * Match configured as is is accepted by all players
		 */
		Ready,
		
		/**
		 * Match in progress.
		 */
		Active,
		
		/**
		 * Match is completed.
		 */
		Completed, 
		
		Invalid
	}
	
	
	public static final int DEFAULT_MAX_ROUND = 1;
	public static final int DEFAULT_ACTION_QUEUE_SIZE = 2;
	
	
	private static Logger logger = LoggerFactory.getLogger(Match.class);

	/**
	 * The match id
	 */
	private String id;
	
	/**
	 * The user id hosting the match
	 */
	private String host;
	
	/** The number of rounds in this match */
	private int maxRound;

	/** The list of team in this match */
	private List<Team> teams;
	
	/** The {@code Round} in this match */
	private List<Round> rounds;
	
	private transient ActionQueue<FightAction> actionQueue;
	
	private State state;
	
	private transient IObserver<MatchEvent> observer;
	
	/** Default constructor */
	public Match() {
		this(UUID.randomUUID().toString());
	}
	
	public Match(String id) {
		this.id = id;
		this.maxRound = DEFAULT_MAX_ROUND;
		this.rounds = new ArrayList<>();
		this.teams = new ArrayList<>();
		this.state = State.New;
		this.actionQueue = new ActionQueue<>(DEFAULT_ACTION_QUEUE_SIZE);
	}

	/**
	 * Adds a {@code Team} to this match. 
	 * Merge existing fighters into the same team
	 * @param teams - the teams to be added
	 */
	public void addTeam(Team... teams) {
		
		for (Team t : teams) {
			Team existingTeam = getTeam(t.getId());
			if (existingTeam == null) {
				this.teams.add(t);
			} else {
				for (String fighterId : t.getFighters()) {
					existingTeam.addFighter(fighterId);
				}
			}
		}
	}

	/**
	 * Remove a {@code Team} from this match
	 * @param teams - the teams to be removed
	 */
	public void removeTeam(Team... teams) {
		
		for (Team t : teams) {
			if (this.teams.contains(t)) {
				this.teams.remove(t);
			}
		}
	}
	
	
	/**
	 * Get the team based on the specified team id
	 * @param id - the team id
	 * @return the team corresponding to the specified id, null if not found
	 */
	public Team getTeam(Team.Id id) {
		
		for (Team t : teams) {
			if (t.getId() == id){
				return t;
			}
		}
		return null;
	}
	/**
	 * 
	 * @param rounds - the number of rounds to be added
	 * @throws IllegalArgumentException if the number of round exceeds maximum number of rounds. 
	 * None of the rounds will be added.
	 */
	public void addRound(Round... rounds) throws IllegalArgumentException {
		
		if (this.rounds.size() + rounds.length <= maxRound) {
			for (Round r : rounds) {
				if (!this.rounds.contains(r)) {
					this.rounds.add(r);
				}
			}
		} else {
			throw new IllegalArgumentException("Exceed maximum number of rounds: " + maxRound);
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		
		StringBuffer sb = new StringBuffer(); 
		sb.append(System.lineSeparator());
		sb.append(String.format("Match ID: %s \n", id));
		sb.append(String.format("State: %s \n", state));
		sb.append(String.format("Max round: %d \n", maxRound));
		
		sb.append("Team Information:\n");
		for (Team t : teams) sb.append(" - " + t.toString() + System.lineSeparator()); 
		
		if (!rounds.isEmpty()) {
			sb.append("Round Information:\n");
			for (Round r : rounds) sb.append(" - " + r.toString() + System.lineSeparator());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
	
	/**
	 * Get the list of winners for this {@code Match}
	 * @return the list of winners
	 */
	public List<String> getWinners() {

		List<String> winners = new ArrayList<>();
		for (Round round : rounds) {
			winners.addAll(round.getWinners());
		}
		return winners;
	}

	
	/**
	 * @return the maximum number of rounds
	 */
	public int getMaxRound() {
		return maxRound;
	}

	/**
	 * @param maxRound
	 *            the maximum number of round to set
	 */
	public void setMaxRound(int maxRound) {
		this.maxRound = maxRound;
	}

	/**
	 * @return the rounds
	 */
	public List<Round> getRounds() {
		return rounds;
	}

	/**
	 * @param rounds
	 *            the rounds to set
	 */
	public void setRounds(List<Round> rounds) {
		this.rounds = rounds;
	}

	/**
	 * @return the teams
	 */
	public List<Team> getTeams() {
		return teams;
	}

	/**
	 * Get the list of fighter ids in this match
	 * @return the list of figher ids
	 */
	public List<String> getFighters() {
		List<String> fighterIds = new ArrayList<>();
		for (Team t : getTeams()) {
			fighterIds.addAll(t.getFighters());
		}
		return fighterIds;
	}
	
	/**
	 * Returns a team that is joinable Indicate if the match is joinable.
	 * Match is considered full if it has 2 fighters, one from Team1 and one from Team2
	 * @return the team that is joinable, null if match is full
	 */
	public Team getJoinableTeam() {
		
		if (getTeam(Id.Team01).getFighters().size() < 1) {
			return getTeam(Id.Team01);
		}
		
		if (getTeam(Id.Team02).getFighters().size() < 1) {
			return getTeam(Id.Team02);
		}
		return null;
	}

	/**
	 * Request to join the match
	 * @param fighter - the fighter joining the match
	 * @param team - the team
	 */
	public boolean join(String fighter, Team team) {
		
		Team teamToJoin = getTeam(team.getId());
		if (teamToJoin == null || teamToJoin.getFighters().size() <= 1) {
			logger.error("Invalid or full team.");
			return false;
		}
		return teamToJoin.addFighter(fighter);
	}

	/**
	 * Indicates that the match is joinable
	 * @return
	 */
	public boolean isJoinable() {
		return (getTeam(Id.Team01).getFighters().size() < 1) || 
			   (getTeam(Id.Team02).getFighters().size() < 1); 
	}
	
	
	/**
	 * Get the first fighter on the specified team. 
	 * Expect only 1 fighter to be on the specified team.
	 * @param teamId - the team id
	 * @return the first fighter in the team
	 */
	public String getFighter(Team.Id teamId) {

		List<String> fighters = getTeam(teamId).getFighters();
		if (fighters.size() != 1) {
			logger.info("Team " + teamId + " has does not have exactly 1 fighter");
		}
		
		// expect only 1 fighter in this team
		return fighters.get(0);
	}
	
	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 * @param fighterId - the id of the fighter making the change
	 */
	public void setState(State state, Context context) {
		if (this.state != state) {
			this.state = state;
			if (observer != null) {
				observer.notify(new MatchEvent(state, this), context);
			}
		}
	}
	

	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof Match)) {
			return false;
		}
		
		Match m = (Match) obj;
		return  (this.getId().equals(m.getId())) &&
				(this.getState().ordinal() == m.getState().ordinal()) &&
				(this.getMaxRound() == m.getMaxRound()) && 
				(this.getTeams().equals(m.getTeams()));
	}

	@Override
	public int hashCode() {
	    return getId().hashCode();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Queue up the action for the match
	 * @return true if the queue is full
	 */
	public boolean queueAction(FightAction action) {
		if (actionQueue.size() >= DEFAULT_ACTION_QUEUE_SIZE) {
			actionQueue.clear();
		}
		actionQueue.add(action);
		return (actionQueue.size() == DEFAULT_ACTION_QUEUE_SIZE);
	}

	public FightAction pollAction() {
		return actionQueue.poll();
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the observer
	 */
	public IObserver<MatchEvent> getObserver() {
		return observer;
	}

	/**
	 * @param observer the observer to set
	 */
	public void setObserver(IObserver<MatchEvent> observer) {
		this.observer = observer;
	}
}
