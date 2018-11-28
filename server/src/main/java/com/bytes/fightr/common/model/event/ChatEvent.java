package com.bytes.fightr.common.model.event;

import com.bytes.fmk.data.model.Message;

public class ChatEvent {
	
	private Message message;

	public ChatEvent() {
		this.message = null;
	}
	
	public ChatEvent(Message message) {
		this.message = message;
	}
	
	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}
	

}
