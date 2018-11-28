package com.bytes.fightr.client.logic;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fmk.model.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kent on 4/30/2017.
 */
public class GameStateTest {

    @Test
    public void addUser() throws Exception {

    }

    @Test
    public void removeUser() throws Exception {

    }

    @Test
    public void addFighter() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void getFighter() throws Exception {

    }

    @Before
    public void setup() {
        GameState.Instance.init();
    }

    /**
     * To test this method, change Bundle to Map<String, String>
     * @throws Exception
     */
    @Ignore
    public void loadInstanceState() throws Exception {

        GameState game = GameState.Instance;
        Map<String, String> bundle = new HashMap<>();

        User user = new User("user").setId("user@test.com");
        Fighter userFighter = new Fighter("user");
        user.setAvatarId(userFighter.getId());
        User target = new User("target").setId("target@test.com");
        Fighter targetFighter = new Fighter("target");
        target.setAvatarId(targetFighter.getId());
        Match match = new Match();

        // Set states
        game.setUserRegistered(true);
        game.setUser(user);
        game.addFighter(userFighter);
        game.addFighter(targetFighter);
        game.setMatch(match);

        // save state to bundle
//        game.saveInstanceState(bundle);

        // reset state, verify that states are reset
        game.init();
        Assert.assertFalse(game.isUserRegistered());
        Assert.assertNotEquals(user, game.getUser());
        Assert.assertNotEquals(match, game.getMatch());
        Assert.assertNotEquals(userFighter, game.getUserFighter());
        Assert.assertNotEquals(targetFighter, game.getTargetFighter());

        // restore state from bundle, verify states are matching with saved bundle
//        game.loadInstanceState(bundle);

        Assert.assertTrue(game.isUserRegistered());
        Assert.assertEquals(user, game.getUser());
        Assert.assertEquals(match, game.getMatch());
        Assert.assertEquals(userFighter, game.getUserFighter());
        Assert.assertEquals(targetFighter, game.getTargetFighter());
    }

}

