package com.bytes.fightr.client.logic;

import com.bytes.fightr.client.service.logger.Logger;
import com.bytes.fightr.client.service.logger.LoggerFactory;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.model.MatchBuilder;
import com.bytes.fmk.model.User;

/**
 * Created by Kent on 6/3/2017.
 * This class encapsulate all Match related operations
 */
public class MatchLogic {

    private Logger logger = LoggerFactory.getLogger(MatchLogic.class);

    private MatchBuilder matchBuilder;

    MatchLogic() {
        matchBuilder = new MatchBuilder();
    }


    /**
     * Request to create a new {@code Match} as host.
     * @return the match
     */
    public void initMatchAsHost() {

        GameState game = GameState.Instance;
        User user = game.getUser(game.getUser().getId());
        if (user == null) {
            throw new IllegalStateException("Request to host a match but user is null.");
        }

        Fighter fighter = game.getFighter(user.getAvatarId());

        if (fighter == null) {
            logger.warn("Request to host a match but fighter is null");
        }

        matchBuilder.reset(); // clear all fighters
        Match match = matchBuilder.addToTeam1(fighter.getId()).create(user.getId());
        game.setMatch(match);

    }

    /**
     * Request to add the specified user as an ally
     *
     * @param hostId - the user id hosting the match
     * @param allyId - the user id of the selected ally
     * @return the match
     */
    public void addAllyToMatch(String hostId, String allyId) {

        GameState game = GameState.Instance;

        try {
            User user = game.getUser(hostId);
            Fighter fighter = game.getFighter(user.getAvatarId());

            User target = game.getUser(allyId);
            Match match = matchBuilder
                    .addToTeam1(fighter.getId())
                    .addToTeam1(target.getAvatarId())
                    .create(hostId);
            game.setMatch(match);

        } catch (Exception e) {
            logger.error("Unable to add ally to a match", e);

        }
    }

    /**
     * Request to add the specified user as an enemy
     *
     * @param hostId  - the user id hosting the match
     * @param enemyId - the user id of the selected enemy
     * @return the match
     */
    public void addEnemyToMatch(String hostId, String enemyId) {

        GameState game = GameState.Instance;

        try {
            User user = game.getUser(hostId);
            Fighter fighter = game.getFighter(user.getAvatarId());

            User target = game.getUser(enemyId);
            Match match = matchBuilder
                    .addToTeam1(fighter.getId())
                    .addToTeam2(target.getAvatarId())
                    .create(hostId);
            game.setMatch(match);

        } catch (Exception e) {
            logger.error("Unable to add enemy to a match", e);

        }
    }

}
