package com.bytes.fightr.server.logic.observer;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.event.UserEvent;
import com.bytes.fmk.observer.Context;
import com.bytes.fmk.observer.IObserver;
import com.bytes.fmk.payload.Payload;

public class UserObserver implements IObserver<UserEvent> {

	@Override
	public void notify(UserEvent data, Context context) {
		
		String sessionId = FightrServer.getInstance().getSessionRegistry().getSessionByUserId(data.getUserId());
		FighterPayload notificationPayload = new FighterPayload(
				Payload.NOTIFY, 
				DataType.UserEvent, 
				PayloadUtil.getGson().toJson(data));
		notificationPayload.setSourceId(sessionId);
		
		switch (context.getDestinationType()) {
		case All:
			FightrServer.getInstance().send(notificationPayload, context.getDestinationType());
			break;
			
		case Custom:
			// TODO should server send to destinations from Match...or all?
//			FightrServer.getInstance().send(notificationPayload);
			break;
			
		default:
			break;
		
		}
	}

}
