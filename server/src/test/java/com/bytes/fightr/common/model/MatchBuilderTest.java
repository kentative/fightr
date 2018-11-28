package com.bytes.fightr.common.model;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bytes.fightr.server.service.BaseServiceTest;

/**
 * @author KL
 */
public class MatchBuilderTest extends BaseServiceTest {

	private MatchBuilder matchBuilder;

	@Test
	public void createMatchAsHost() {
		
		matchBuilder = new MatchBuilder();
		Match match = matchBuilder.addToTeam1(f1.getId()).create(u1.getId());
		Assert.assertEquals(u1.getAvatarId(), match.getFighter(Team.Id.Team01));
	}

	@Test
	public void addAllyToMatch() {

		matchBuilder = new MatchBuilder();
		Match match = matchBuilder
				.addToTeam1(u1.getAvatarId())
				.addToTeam1(u2.getAvatarId()).create(u1.getId());

		List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
		List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();

		Assert.assertTrue(team1Fighters.contains(u1.getAvatarId()));
		Assert.assertTrue(team1Fighters.contains(u2.getAvatarId()));

		Assert.assertFalse(team2Fighters.contains(u1.getAvatarId()));
		Assert.assertFalse(team2Fighters.contains(u2.getAvatarId()));

	}
	
	@Test
	public void addEnemyToMatch() {

		matchBuilder = new MatchBuilder();
		Match match = matchBuilder
				.addToTeam1(u1.getAvatarId())
				.addToTeam2(u2.getAvatarId()).create(u1.getId());

		List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
		List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();

		Assert.assertTrue(team1Fighters.contains(u1.getAvatarId()));
		Assert.assertTrue(team2Fighters.contains(u2.getAvatarId()));
	}


	@Test
	public void addMultipleAlliesToMatch1() {

		matchBuilder = new MatchBuilder();
		Match match = matchBuilder
				.addToTeam1(
						f1.getId(), 
						f2.getId(), 
						f2.getId(), 
						f1.getId(), 
						f3.getId()).create(u1.getId());

		List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
		List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();

		Assert.assertEquals(3, team1Fighters.size());
		Assert.assertTrue(team1Fighters.contains(u1.getAvatarId()));
		Assert.assertTrue(team1Fighters.contains(u2.getAvatarId()));
		Assert.assertTrue(team1Fighters.contains(u3.getAvatarId()));

		Assert.assertEquals(0, team2Fighters.size());
	}
	
	@Test
	public void addMultipleAlliesToMatch2() {

		matchBuilder = new MatchBuilder();
		Match match = matchBuilder
			.addToTeam1(f1.getId())
			.addToTeam1(f2.getId()).create(u1.getId());

		matchBuilder
			.addToTeam1(f1.getId())
			.addToTeam1(f3.getId()).create(u1.getId());
		
		matchBuilder
			.addToTeam1(f1.getId())
			.addToTeam1(f3.getId()).create(u1.getId());


		List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
		List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();

		Assert.assertEquals(3, team1Fighters.size());
		Assert.assertTrue(team1Fighters.contains(u1.getAvatarId()));
		Assert.assertTrue(team1Fighters.contains(u2.getAvatarId()));
		Assert.assertTrue(team1Fighters.contains(u3.getAvatarId()));

		Assert.assertEquals(0, team2Fighters.size());
	}

}
