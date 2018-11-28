package com.bytes.fightr.server.logic.processor;

import com.bytes.fightr.common.model.debug.LatencyCmd;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.common.util.TimeUtil;
import com.bytes.fightr.server.service.GameService;
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
		if (action.equals("reset")) {  
			resultPayload = reset(payload);
		
		} else if (action.equals("latency")) {
			resultPayload = computeLatency(payload);
		}
		
		return resultPayload;
	}
	
	private Payload<String> computeLatency(Payload<String> payload) {
		
		LatencyCmd latency = PayloadUtil.getData(payload, DataType.LatencyCmd);
		long time = latency.getServerTime();
		if (time > 0) {
			logger.info("Server Latency: " + TimeUtil.getDelta(time));
			return null;
		}
		
		// Set Server time
		latency.setServerTime(TimeUtil.now());
		
		// send it back to the source/client
		payload.setData(gson.toJson(latency));
		payload.addDestination(payload.getSourceId());
		return payload;
	}

	private FighterPayload reset(Payload<String> payload) {
		
		logger.debug("reseting server state");
		GameService.getInstance().reset();
		return null;
	}

}
