package com.bytes.fightr.server.logic.observer;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.fightr.common.model.event.MatchEvent;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fightr.server.service.comm.SessionRegistry;
import com.bytes.fmk.observer.Context;
import com.bytes.fmk.observer.IObserver;
import com.bytes.fmk.payload.Payload;

public class MatchObserver implements IObserver<MatchEvent> {
	
	private static Logger logger = LoggerFactory.getLogger(MatchObserver.class);
	
	/**
	 * The list of subscriber session ids
	 */
	private Set<String> subscribers;

	/**
	 * Default constructor
	 */
	public MatchObserver() {
		subscribers = new HashSet<>();
	}
	
	@Override
	public void notify(MatchEvent data, Context context) {

		logger.info("Notify state: " + data.getState());
		
		if (context == null) {
			throw new IllegalStateException("Notification context is required");
		}
		
		SessionRegistry sessionRegistry = FightrServer.getInstance().getSessionRegistry();
		String hostSessionId = sessionRegistry.getSessionByUserId(data.getMatch().getHost());
		if (hostSessionId == null) {
			throw new IllegalStateException("Unable to determine source Id for host: " + data.getMatch().getHost());
		}
		
		Set<String> destinationsId;
		switch (data.getState()) {
			
		case Invalid:
			// Only notify the host that the request match is invalid
			destinationsId = new HashSet<String>();
			destinationsId.add(hostSessionId);
			break;
			
		case Searching:
			// Send to all
			destinationsId = getSubscribers();
			break;
			
		case Ready:
			// Send to other
			destinationsId = getSubscribers();
			destinationsId.remove(context.getSource());
			break;
			
		default:
			destinationsId = getSubscribers();
			break;
		
		}

		FighterPayload notificationPayload = new FighterPayload(
				Payload.NOTIFY, 
				DataType.MatchEvent, 
				PayloadUtil.getGson().toJson(data));

		notificationPayload.setSourceId(context.getSource());
		notificationPayload.addDestinations(destinationsId);
		FightrServer.getInstance().send(notificationPayload);
	}

	/**
	 * @return the subscribers
	 */
	public Set<String> getSubscribers() {
		return new HashSet<String>(subscribers);
	}

	/**
	 * @param sessionIds - the subscriber session ids
	 */
	public void addSubscribers(Set<String> sessionIds) {
		subscribers.addAll(sessionIds);
	}

}
