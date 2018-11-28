package com.bytes.fightr.server.logic.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.event.ChatEvent;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.model.Message;
import com.bytes.fmk.observer.DestinationType;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

public class MessageProcessor extends AbstractPayloadProcessor {

	private static Logger logger = LoggerFactory.getLogger(MessageProcessor.class);
	
	/**
	 * @param payload - the payload containing the user to be registered
	 */
	@Override
	public AbstractPayload<String> process(Payload<String> payload) {
		
		logger.info("Proccesing Chat Message");
	
		Message message = PayloadUtil.getData(payload, DataType.Message);
		ChatEvent event = new ChatEvent(message);
		
		// Send notification to all others connected users
		FighterPayload notificationPayload = new FighterPayload(
				Payload.NOTIFY, 
				DataType.ChatEvent, 
				gson.toJson(event));
		notificationPayload.setSourceId(payload.getSourceId());
		FightrServer.getInstance().send(notificationPayload, DestinationType.Other);

		return null;
	}
}
