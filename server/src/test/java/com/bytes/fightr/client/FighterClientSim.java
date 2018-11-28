package com.bytes.fightr.client;

import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fmk.payload.Payload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FighterClientSim {

	private static Logger logger = LoggerFactory.getLogger(FighterClientSim.class);
	
	// The pretty-print enabled gson
	protected Gson pfGson = new GsonBuilder().setPrettyPrinting().create();
	
	private WebSocketClient client;

	public void setClient(WebSocketClient client) {
		this.client = client;
	}

	
	
	public void process(String msg) {
		
		FighterPayload payload = pfGson.fromJson(msg, PayloadUtil.getClass(msg));
		
		int type = payload.getType();
		DataType dataType = Enum.valueOf(FighterPayload.DataType.class, payload.getDataType());
		
		logger.info(String.format("Payload ID: %1$s Type: %2$s DataType: %3$s", 
				payload.getId(), type, dataType));
		
		switch (type) {
			
		case Payload.POST:
			
			switch(dataType) {
			case Fighter:
				List<Fighter> fighters = PayloadUtil.getDataList(payload, DataType.Fighter);
				logger.info("Payload Contents: " + fighters.size() + " total fighters");
				for (Fighter f : fighters) {
					logger.info(String.format("  Fighter: %1$s id: %2$s", f.getName(), f.getId()));
				}
				
				break;
				
			case Match:
				Match match = PayloadUtil.getData(payload, DataType.Match);
				logger.info(match.toString());
				break;
				
			case FightAction:
				break;
			case FightResult:
				break;
			case LatencyCmd:
				break;
			case LoginEvent:
				break;
			case MatchEvent:
				break;
			case Message:
				break;
			case String:
				break;
			case User:
				break;
			default:
				break;
			
			}
			
//			
//		case Ready:
//			String fighterId = PayloadUtil.getData(payload, DataType.String);
//			logger.info(String.format("Fighter %1$s is READY", fighterId));
//			break;
//			
//		case Start:
//			String matchId = PayloadUtil.getData(payload, DataType.String);
//			logger.info(String.format("Match %1$s is READY", matchId));
//			break;
//			
//		case Result:
//			FightResult fightResult = PayloadUtil.getData(payload, DataType.FightResult);
//			for (Fighter f : fightResult.getFighters()){
//				logger.info(f.toString());
//			}
//			break;
//			
//		case Debug:
//	        String action = PayloadUtil.getData(payload, DataType.String);
//	        logger.debug("Debugging server: " + action);
//
//	        Payload<String> resultPayload = null;
//	        if (action.equals("latency")) {
//	            resultPayload = computeLatency(payload);
//	        }
//        	client.send(pfGson.toJson(resultPayload));
        	
		default:
			break;
		
		}
	}

	 private Payload<String> computeLatency(Payload<String> payload) {

		 	// TODO Latency 
//	        DebugPayload p = ((DebugPayload) payload);
//	        logger.info("Client Latency: " + TimeUtil.getDelta(p.getClientTime()));
//	        return p;
		 return null;
    }
}
