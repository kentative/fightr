package com.bytes.fightr.common.model.data;

import java.io.Serializable;

public class DataImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String data;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
