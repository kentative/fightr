package com.bytes.fightr.client.logic.processor;

/**
 * Created by Kent on 4/22/2017.
 */


import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;


public class DebugProcessor extends AbstractPayloadProcessor {

    @Override
    public boolean validate(Payload<String> payload) {
        return super.validate(payload);
    }


    @Override
    public Payload<String> process(Payload<String> payload) {

        String action = PayloadUtil.getData(payload, DataType.String);
        logger.debug("Debugging server: " + action);

        Payload<String> resultPayload = null;
        if (action.equals("latency")) {
            resultPayload = computeLatency(payload);
        }

        return resultPayload;
    }

    private Payload<String> computeLatency(Payload<String> payload) {

        // TODO Latency
//        DebugPayload p = ((DebugPayload) payload);
//
//        // compute client time
//        notify(new ProcessorEvent<>("Client Latency: " + TimeUtil.getDelta(p.getClientTime())));

        // send it back to the source/server, no sessionId needed
        return null;
    }

}
