package com.bytes.fightr.client.logic.processor;

import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

class ActionProcessor extends AbstractPayloadProcessor {

    @Override
    public boolean validate(Payload<String> payload) {
        boolean valid = super.validate(payload);
        valid = valid && (PayloadUtil.getData(payload, DataType.FightAction) != null);
        if (!valid) {
            logger.error("validation error - Missing expected data: ActionType");
        }
        return valid;
    }


    @Override
    public Payload<String> process(Payload<String> payload) {

        FightAction action = PayloadUtil.getData(payload, DataType.FightAction);
        String sourceId = action.getSource();
        logger.info("Performing action for fighter: " + sourceId);

        return null;
    }

}
