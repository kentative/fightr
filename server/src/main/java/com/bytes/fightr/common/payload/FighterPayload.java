package com.bytes.fightr.common.payload;

import com.bytes.fmk.payload.AbstractPayload;

/**
 * This payload uses JSON as the data container (i.e., Payload<String>) 
 * It identifies the type of the data with the dataType attribute
 * Documentation: Payload
 */
public class FighterPayload extends AbstractPayload<String> {

	public enum DataType {
		User,
		Fighter, 
		Match, 
		String, 
		FightAction,		
		FightResult,
		Message,
		
		// Server to Client
		LoginEvent,
		UserEvent,
		MatchEvent, // Ready, Start
		ChatEvent,
		ActionEvent,
		Result,
		
		// For DEBUG
		ResetCmd,
		LatencyCmd,
		
		Null,  
	}
	
	/**
	 * The string representation of the payload data type
	 */
	private String dataType;
	
	/**
	 * 
	 * @param type
	 */
	public FighterPayload(int type) {
		this(type, DataType.String, null);
	}
	
	/**
	 * 
	 * @param type
	 * @param dataType
	 */
	public FighterPayload(int type, DataType dataType) {
		this(type, dataType, null);
	}
	
	/**
	 * 
	 * @param type
	 */
	public FighterPayload(int type, DataType dataType, String data) {
		setType(type);
		setDataType(dataType.name());
		setData(data);
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
