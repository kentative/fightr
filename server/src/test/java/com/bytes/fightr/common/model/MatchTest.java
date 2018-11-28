package com.bytes.fightr.common.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.server.service.FighterService;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fightr.server.service.UserService;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fightr.server.service.comm.MockedSession;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.data.model.User.Status;

/**
 * @author KL
 */
public class MatchTest {

	private Team team1;
	private Team team2;
	private Team team3;
	private Team team4;
	
	
	@Before
	public void setup() {
		team1 = new Team(Team.Id.Team01);
		team2 = new Team(Team.Id.Team02);
		team3 = new Team(Team.Id.Team03);
		team4 = new Team(Team.Id.Team04);
		
		MockedSession session1 = new MockedSession("session1");
		MockedSession session2 = new MockedSession("session2");
		FightrServer.getInstance().getSessionRegistry().register(session1);
		FightrServer.getInstance().getSessionRegistry().register(session2);
	}
	
	@Test
	public void constructorTest() {
		
		Match match = new Match();
		assertEquals(Match.DEFAULT_MAX_ROUND, match.getMaxRound());
		assertEquals(0, match.getRounds().size());
		assertEquals(0, match.getTeams().size());		
	}
	
	/**
	 * case01: Adds 2 unique team, expects to have 2 teams
	 * case02: Add 4 teams, only 2 unique, expect to have 2 teams
	 */
	@Test
	public void addTeamTest() {
		
		Match match = new Match();
		Team team1 = new Team(Team.Id.Team01);
		Team team2 = new Team(Team.Id.Team02);
		
		// case01: Expect to have 2 teams 
		match.addTeam(team1);
		match.addTeam(team2);
		assertEquals("add 2 new teams", 2, match.getTeams().size());
		
		// case02
		match.addTeam(team1);
		match.addTeam(team2);
		assertEquals("add 2 of the same team", 2, match.getTeams().size());
		
	}
	
	/**
	 * case01: Remove 2 existing team, expect 0 teams
	 * case02: Remove teams when there are none in the match, expect 0
	 * case03: Remove a non-matching team, expect 2 teams
	 */
	@Test
	public void removeTeamTest() {
		

		// case01: Expect to have 0 teams
		Match match = new Match();
		match.addTeam(team1);
		match.addTeam(team2);
		match.removeTeam(team1);
		match.removeTeam(team2);
		assertEquals("add 2, remove 2", 0, match.getTeams().size());
		
		// case02: Expect to have 0 team
		match = new Match();
		match.removeTeam(team1);
		match.removeTeam(team2);
		assertEquals("remove 2 from empty list", 0, match.getTeams().size());
		
		// case03
		match = new Match();
		match.addTeam(team1);
		match.addTeam(team2);
		match.removeTeam(team3);
		match.removeTeam(team4);
		assertEquals("add 2, remove 2 different one", 2, match.getTeams().size());
		
	}
	
	@Test
	public void addRoundTest() {

		Round r1 = new Round();
		Round r2 = new Round();
		Round r3 = new Round();
		
		// case01
		Match match = new Match();
		match.setMaxRound(3);
		match.addRound(r1, r2, r3);
		assertEquals("add 3 rounds", 3, match.getRounds().size());
		
		// case02
		match = new Match();
		match.setMaxRound(3);
		r1 = new Round();
		match.addRound(r1, r1);
		assertEquals("adding the same round (duplicate)", 1, match.getRounds().size());		
		
	}
	
	@Test
	public void equalsTest() {
		User u1 = new User("u1");
		
		Match o1 = new MatchBuilder().addToTeam1(new Fighter("Kent").getId()).create(u1.getId());
		Match o2 = new MatchBuilder().addToTeam1(new Fighter("Kent").getId()).create(u1.getId());
		Assert.assertEquals(o1, o1);
		Assert.assertNotEquals(o1, o2);
		
		
		MatchBuilder matchBuilder = new MatchBuilder();
		Fighter f1 = new Fighter("a3t1").setId("id1");
		Fighter f2 = new Fighter("a3t2").setId("id2");
		Match o3 = matchBuilder
				.addToTeam1(f1.getId())
				.addToTeam2(f2.getId())
				.create(u1.getId());
		Match o3a = matchBuilder.reset()
				.addToTeam1(f1.getId())
				.addToTeam2(f2.getId())
				.create(u1.getId());
		Match o3b = matchBuilder.reset()
				.addToTeam1(new Fighter("b3t1").getId())
				.addToTeam2(new Fighter("a3t2").getId())
				.create(u1.getId());
		
		Match o4 = new MatchBuilder()
				.addToTeam1(new Fighter("a4t1").getId())
				.addToTeam2(new Fighter("a4t2").getId())
				.create(u1.getId());
		Assert.assertEquals(o3, o3a);
		Assert.assertNotEquals(o3, o3b);
		Assert.assertNotEquals(o3, o4);
	}
	
	
	@Test
	public void getFighterIdsTest() {
		
		User u1 = new User("u1");
		Fighter f1 = new Fighter("f1");
		Fighter f2 = new Fighter("f2");
		
		MatchBuilder matchBuilder = new MatchBuilder();
		Match m = matchBuilder
				.addToTeam1(f1.getId())
				.addToTeam2(f2.getId())
				.create(u1.getId());
		
		List<String> fighterIds = m.getFighters();
		
		Assert.assertTrue(fighterIds.contains(f1.getId()));
		Assert.assertTrue(fighterIds.contains(f2.getId()));
	}
	
	@Test
	public void isStartableTest() {
		GameService service = GameService.getInstance();
		
		User u1 = new User("u1");
		User u2 = new User("u2");
		Fighter f1 = new Fighter("f1").linkUser(u1);
		Fighter f2 = new Fighter("f2").linkUser(u2);

		service.getUserService().registerUser(u1, "session1");
		service.getUserService().registerUser(u2, "session2");
		service.getFighterService().registerFighter(f1);
		service.getFighterService().registerFighter(f2);
		MatchBuilder matchBuilder = new MatchBuilder();
		Match m = matchBuilder
				.addToTeam1(f1.getId())
				.addToTeam2(f2.getId())
				.create(u1.getId());
		
		// [0, 0]
		Assert.assertFalse(service.getMatchService().isStartable(m));

		// [1, 0]
		u1.setStatus(Status.Ready);
		Assert.assertFalse(service.getMatchService().isStartable(m));
		
		// [1, 1]
		u2.setStatus(Status.Ready);
		Assert.assertTrue(service.getMatchService().isStartable(m));
		
		// [0, 1]
		u1.setStatus(Status.Available);
		Assert.assertFalse(service.getMatchService().isStartable(m));
	}
	
	@Test
	public void queueActionTest() {
		FighterService service = GameService.getInstance().getFighterService();
		UserService userService = GameService.getInstance().getUserService();
		User u1 = new User("u1");
		User u2 = new User("u2");
		
		Fighter f1 = new Fighter("f1").linkUser(u1);
		Fighter f2 = new Fighter("f2").linkUser(u2);
		
		userService.registerUser(u1, "session1");
		userService.registerUser(u2, "session2");
		service.registerFighter(f1);
		service.registerFighter(f2);
		
		MatchBuilder matchBuilder = new MatchBuilder();
		Match m = matchBuilder
				.addToTeam1(f1.getId())
				.addToTeam2(f2.getId())
				.create(u1.getId());
		
		Assert.assertFalse(m.queueAction(new FightAction()));
		Assert.assertTrue(m.queueAction(new FightAction()));
		
	}
}
