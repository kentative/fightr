package com.bytes.fightr.client.controller.battle.processor;

import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.bytes.fmk.payload.processor.ProcessorEvent;

class StartMatchProcessor extends AbstractPayloadProcessor {

    @Override
    public boolean validate(Payload<String> payload) {
        boolean valid = super.validate(payload);
        valid = valid && (PayloadUtil.getData(payload, DataType.String) != null);
        if (!valid) {
            logger.error("validation error - Missing expected data: Fighter id");
        }
        return valid;
    }


    /**
     * Handle the payload containing:
     *
     * Type.Start,
     * DataType.String,
     * match.getId());
     *
     * @param payload
     * @return
     */
    @Override
    public Payload<String> process(Payload<String> payload) {

        String matchId = PayloadUtil.getData(payload, DataType.String);
        notify(new ProcessorEvent<Object>(ProcessorEvent.Type.StartMatch, matchId));
        String fighterId = PayloadUtil.getData(payload, DataType.String);
        logger.info("Confirming ready status for fighter " + fighterId);


        return null;
    }

}
