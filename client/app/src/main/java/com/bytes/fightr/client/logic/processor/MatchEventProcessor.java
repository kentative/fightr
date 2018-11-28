package com.bytes.fightr.client.logic.processor;

import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MatchEventProcessor extends AbstractPayloadProcessor {

    private Logger logger = LoggerFactory.getLogger(MatchEventProcessor.class);

    /**
     * Check for valid Match:
     */
    @Override
    public boolean validate(Payload<String> payload) {

        boolean valid = super.validate(payload);
        valid = valid && (PayloadUtil.getData(payload, DataType.Match) != null);
        if (!valid) {
            logger.error("validation error - Missing expected data: Match");
        }
        return valid;
    }


    @Override
    public Payload<String> process(Payload<String> payload) {

        logger.info("Preparing match");

        Match match = PayloadUtil.getData(payload, DataType.Match);
        return null;
    }

}
