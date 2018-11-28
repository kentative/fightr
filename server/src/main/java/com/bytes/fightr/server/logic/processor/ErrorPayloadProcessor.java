package com.bytes.fightr.server.logic.processor;

import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

public class ErrorPayloadProcessor extends AbstractPayloadProcessor {

	@Override
	public boolean validate(Payload<String> payload) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public AbstractPayload<String> process(Payload<String> payload) {
		
		return null;
	}

}
