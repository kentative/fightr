package com.bytes.fightr.client.controller.battle.processor;

import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.bytes.fmk.payload.processor.ProcessorEvent;

class FighterProcessor extends AbstractPayloadProcessor {

    /**
     * Data must contain a valid Fighter
     */
    @Override
    public boolean validate(Payload<String> payload) {
        boolean valid = super.validate(payload);
        valid = valid && (PayloadUtil.getDataList(payload, DataType.Fighter) != null);
        if (!valid) {
            logger.error("validation error - Missing expected data: Fighter");
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

        // Pass the payload data to the controller to display in a new activity
        notify(new ProcessorEvent<>(ProcessorEvent.Type.Fighters, payload.getData()));

        return null;
    }

}
