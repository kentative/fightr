package com.bytes.fightr.client.logic;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.Team;
import com.bytes.fmk.model.User;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by Kent on 6/3/2017.
 */
public class BaseLogicTest {

    protected User enemyUser;
    protected User allyUser;

    @Before
    public void setup() {
        GameState.Instance.init();
        GameState game = GameState.Instance;
        GameLogic.Instance.build(game, true);

        enemyUser = new User("Enemy");
        Fighter enemyFighter = new Fighter("Enemy");
        enemyFighter.linkUser(enemyUser);
        game.addUser(enemyUser);
        game.addFighter(enemyFighter);

        allyUser = new User("Ally");
        Fighter allyFighter = new Fighter("Ally");
        allyFighter.linkUser(allyUser);
        game.addUser(allyUser);
        game.addFighter(allyFighter);
    }

}