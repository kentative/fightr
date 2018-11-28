package com.bytes.fightr.client.logic;

import com.bytes.fightr.client.service.logger.Logger;
import com.bytes.fightr.client.service.logger.LoggerFactory;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.fmk.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kent on 6/3/2017.
 *
 */
public enum GameLogic {

    Instance;

    private Logger logger = LoggerFactory.getLogger(GameLogic.class);

    private MatchLogic matchLogic;

    GameLogic() {
        matchLogic = new MatchLogic();
    }

    /**
     * Create the initial state of the game for a new user
     */
    public void build(GameState game, boolean forcedReset) {

        if (!forcedReset && game.getUser() != null) {
            throw new IllegalStateException("Initializing a user but a non null instance exists.");
        }

        game.init();

        String defaultName = "Kydan";
        User user = new User(defaultName);
        user.setId(defaultName);
        game.setUserRegistered(false);
        game.setUser(user);
        game.setMatch(null);

        Fighter userAvatar = createDefaultFighter(user);
        userAvatar.linkUser(user);
        game.addFighter(userAvatar);
        game.saveInstanceState();
    }

    /**
     * Create a default Fighter
     *
     * @return a default fighter
     */
    public Fighter createDefaultFighter(User user) {

        Fighter fighter = new Fighter(user.getDisplayName());
        Map<Integer, FighterSkill.Id> skills = new HashMap<>(6);
        skills.put(1, FighterSkill.Id.Attack);
        skills.put(2, FighterSkill.Id.Block);
        skills.put(3, FighterSkill.Id.Reload);
        skills.put(4, FighterSkill.Id.Disrupt);
        skills.put(5, FighterSkill.Id.ChargedAttack);
        skills.put(6, FighterSkill.Id.BlindingFlash);
        fighter.setEquipedSkills(skills);
        fighter.setSkills(skills.values());
        return fighter;
    }

    public MatchLogic getMatchLogic() {
        return matchLogic;
    }
}
