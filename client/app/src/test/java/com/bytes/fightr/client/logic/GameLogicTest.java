package com.bytes.fightr.client.logic;

import com.bytes.fightr.common.model.Fighter;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kent on 6/3/2017.
 */
public class GameLogicTest {

    @Test
    public void build() throws Exception {

        GameState game = GameState.Instance;
        GameLogic.Instance.build(game, true);

        Fighter fighter = game.getUserFighter();

        Assert.assertNotNull(game.getUser());
        Assert.assertNotNull(fighter);
        Assert.assertEquals(6, fighter.getEquipedSkills().size());
    }

}