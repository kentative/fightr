package com.bytes.fightr.client.logic.processor;

import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.action.FightResult;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.bytes.fmk.payload.processor.ProcessorEvent;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarPower;

import java.util.List;

/**
 * Created by Kent on 4/14/2017.
 *
 */
class ActionEventProcessor extends AbstractPayloadProcessor {

    /**
     * Data must contain a valid Fighter
     */
    @Override
    public boolean validate(Payload<String> payload) {
        boolean valid = super.validate(payload);
        valid = valid && (PayloadUtil.getData(payload, FighterPayload.DataType.FightResult) != null);
        if (!valid) {
            logger.error("validation error - Missing expected data: FightResult");
        }
        return valid;
    }

    /**
     * Register fighter
     * Returns a list of registered fighters as potential opponents
     *
     * @param payload - the payload containing the fighter to be registered
     */
    @Override
    public Payload<String> process(Payload<String> payload) {

        Fighter opponent = GameState.Instance.getTargetFighter();
        FightResult result = PayloadUtil.getData(payload, FighterPayload.DataType.FightResult);
        List<Fighter> fighters = result.getFighters();
        List<FightAction> actions = result.getActions();
        for (FightAction action : actions) {
            updateFighterStatus(getFighter(fighters, action.getSource()), action);
            updateFighterStatus_phase1(getFighter(fighters, action.getSource()), action);
        }

        // Check health
        AvatarAttribute health;
        for (Fighter fighter : fighters) {
            health = fighter.getAttributes(AvatarAttribute.Type.HEALTH);
            if (health.getValue() <= 0) {

                if (fighter.getId().equals(opponent.getId())) {
                    // Win - opponent's health <= 0
                    notify(new ProcessorEvent<>(ProcessorEvent.Type.GameResults, "You Win!"));
                } else {
                    // GameResults
                    notify(new ProcessorEvent<>(ProcessorEvent.Type.GameResults, "You Loose!"));
                }
            }
        }
        return null;
    }

    /**
     * Get the fighter corresponding to the specified fighter id
     *
     * @param fighters the list of fighters to search
     * @param id       the id of the fighter
     * @return the fighter corresponding to the specified id
     */
    private Fighter getFighter(List<Fighter> fighters, String id) {
        for (Fighter fighter : fighters) {
            if (fighter.getId().equals(id)) {
                return fighter;
            }
        }
        return null;
    }

    /**
     * Phase 1 implementation of action results
     *
     * @param fighter the fighter performing the specified action
     * @param action  the action corresponding to the fighter
     */
    private void updateFighterStatus_phase1(Fighter fighter, FightAction action) {

        AvatarAttribute health = fighter.getAttributes(AvatarAttribute.Type.HEALTH);
        AvatarPower stamina = fighter.getPower(AvatarPower.Type.ENERGY_STAMINA);

        notify(new ProcessorEvent<>(ProcessorEvent.Type.ActionResults_P1,
                String.format("%1$-10s %2$s - (%3$s HP, %4$s stamina)",
                        fighter.getName(), action.getSkill().name(), health.getValue(), stamina.getValue())));

    }

    /**
     * Phase 2 implementation of action results
     *
     * @param fighter the fighter performing the specified action
     * @param action  the action corresponding to the fighter
     */
    private void updateFighterStatus(Fighter fighter, FightAction action) {

        //GameState.Instance.addUser(fighter);
        notify(new ProcessorEvent<>(ProcessorEvent.Type.ActionResults, action));
    }

}
