package com.bytes.fightr.client.logic.processor;


import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.bytes.fmk.payload.processor.ProcessorEvent;

class ChatEventProcessor extends AbstractPayloadProcessor {

    @Override
    public boolean validate(Payload<String> payload) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Payload<String> process(Payload<String> payload) {

        String data = PayloadUtil.getData(payload, FighterPayload.DataType.String);

        // Log the message and echo the payload back to sender
        notify(new ProcessorEvent<>(data));

        return null;
    }
}
