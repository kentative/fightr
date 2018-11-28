package com.bytes.fightr.common.model.debug;

public class LatencyCmd {


	public LatencyCmd() {
		
	}

	private long clientTime;
	
	private long serverTime;
	
	/**
	 * @return the timestamp
	 */
	public long getServerTime() {
		return serverTime;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setServerTime(long timestamp) {
		this.serverTime = timestamp;
	}
	
	/**
	 * @return the clientTime
	 */
	public long getClientTime() {
		return clientTime;
	}

	/**
	 * @param clientTime the clientTime to set
	 */
	public void setClientTime(long clientTime) {
		this.clientTime = clientTime;
	}
}
