package com.bytes.fightr.client.logic;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.Team;
import com.bytes.fmk.model.User;

import junit.framework.Assert;

import org.junit.Test;

import java.util.List;

/**
 * Created by Kent on 6/3/2017.
 */
public class MatchLogicTest extends BaseLogicTest {

    @Test
    public void createMatchAsHost() throws Exception {

        GameState game = GameState.Instance;
        User user = game.getUser();

        GameLogic.Instance.getMatchLogic().initMatchAsHost();
        Match match = game.getMatch();
        Assert.assertEquals(user.getAvatarId(), match.getFighter(Team.Id.Team01));
    }

    @Test
    public void addAllyToMatch() throws Exception {
        GameState game = GameState.Instance;
        User user = game.getUser();

        GameLogic.Instance.getMatchLogic().addAllyToMatch(user.getId(), allyUser.getId());
        Match match = game.getMatch();
        List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
        List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();

        Assert.assertTrue(team1Fighters.contains(user.getAvatarId()));
        Assert.assertTrue(team1Fighters.contains(allyUser.getAvatarId()));

        Assert.assertFalse(team2Fighters.contains(user.getAvatarId()));
        Assert.assertFalse(team2Fighters.contains(allyUser.getAvatarId()));
    }

    @Test
    public void addMultipleAlliesToMatch() throws Exception {
        GameState game = GameState.Instance;
        User user = game.getUser();

        User ally1 = new User("Ally1");
        User ally2 = new User("Ally2");
        registerUser(ally1);
        registerUser(ally2);

        GameLogic.Instance.getMatchLogic().addAllyToMatch(user.getId(), allyUser.getId());
        Match match = game.getMatch();
        GameLogic.Instance.getMatchLogic().addAllyToMatch(user.getId(), ally1.getId());
        GameLogic.Instance.getMatchLogic().addAllyToMatch(user.getId(), ally2.getId());
        List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
        List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();

        Assert.assertTrue(team1Fighters.contains(user.getAvatarId()));
        Assert.assertTrue(team1Fighters.contains(allyUser.getAvatarId()));
        Assert.assertTrue(team1Fighters.contains(ally1.getAvatarId()));
        Assert.assertTrue(team1Fighters.contains(ally2.getAvatarId()));

        Assert.assertFalse(team2Fighters.contains(user.getAvatarId()));
        Assert.assertFalse(team2Fighters.contains(allyUser.getAvatarId()));
        Assert.assertFalse(team2Fighters.contains(ally1.getAvatarId()));
        Assert.assertFalse(team2Fighters.contains(ally2.getAvatarId()));
    }

    @Test
    public void addDuplicatesToMatch() throws Exception {
        GameState game = GameState.Instance;
        User user = game.getUser();

        User ally1 = new User("Ally1");
        User ally2 = new User("Ally2");
        registerUser(ally1);
        registerUser(ally2);

        GameLogic.Instance.getMatchLogic().addAllyToMatch(user.getId(), ally1.getId());
        Match match = game.getMatch();
        GameLogic.Instance.getMatchLogic().addAllyToMatch(user.getId(), ally1.getId());
        GameLogic.Instance.getMatchLogic().addAllyToMatch(user.getId(), ally1.getId());

        List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
        List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();

        Assert.assertEquals(2, team1Fighters.size());
        Assert.assertTrue(team1Fighters.contains(ally1.getAvatarId()));
        Assert.assertTrue(team1Fighters.contains(user.getAvatarId()));

        Assert.assertEquals(0, team2Fighters.size());
    }

    @Test
    public void addEnemyToMatch() throws Exception {
        GameState game = GameState.Instance;
        User user = game.getUser();

        GameLogic.Instance.getMatchLogic().addEnemyToMatch(user.getId(), enemyUser.getId());
        Match match = game.getMatch();
        List<String> team1Fighters = match.getTeam(Team.Id.Team01).getFighters();
        List<String> team2Fighters = match.getTeam(Team.Id.Team02).getFighters();

        Assert.assertTrue(team1Fighters.contains(user.getAvatarId()));
        Assert.assertTrue(team2Fighters.contains(enemyUser.getAvatarId()));

        Assert.assertFalse(team1Fighters.contains(enemyUser.getAvatarId()));
        Assert.assertFalse(team2Fighters.contains(user.getAvatarId()));
    }

    private User registerUser(User user) {

        GameState game = GameState.Instance;
        Fighter allyFighter = new Fighter(user.getDisplayName());
        allyFighter.linkUser(user);
        game.addUser(user);
        game.addFighter(allyFighter);

        return user;
    }
}