package com.bytes.fightr.common.util;

public class TimeUtil {

	/**
	 * Return a long representing the current time. 
	 * This time can be used to sync the server and client.
	 * @return
	 */
	public static long now() {
		return System.currentTimeMillis();
	}
	
	/**
	 * Return the difference between the current time and the specified time. 
	 * @return
	 */
	public static long getDelta(long timestamp) {
		return getDelta(timestamp, now());
	}
	
	/**
	 * Return the difference between the stop and start time 
	 * @return
	 */
	public static long getDelta(long start, long stop) {
		return stop - start;
	}
}
