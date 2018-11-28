package com.bytes.gamr.model.avatar;

public class AvatarAttribute implements Comparable<AvatarAttribute> {

	public static int MAX_VALUE = 10;
	
	public enum Type {
		
		// Attributes
		HEALTH,
		
		STRENGTH,
		
		DEXTERITY,
		
		INTELLIGENCE,
		
		VITALITY		
	}
	
	private Type type;	
	
	private int value;
	
	private int maxValue;
	

	/**
	 * Default constructor.
	 * @param type
	 */
	public AvatarAttribute(Type type) {
		this(type, 0, MAX_VALUE);
	}
	
	/**
	 * Constructor for skill cost. 
	 * The maximum value is often not needed and is defaulted to the value.
	 * @param type
	 */
	public AvatarAttribute(Type type, int value) {
		this(type, value, value);
	}
	
	/**
	 * Constructor for avatar creation.
	 * @param type - the attribute type
	 * @param value - the initial value 
	 * @param maxValue - the maximum value the avatar can have for this attribute type
	 */
	public AvatarAttribute(Type type, int value, int maxValue) {
		this.type = type;
		this.value = value;
		this.maxValue = maxValue;
	}
	
	/**
	 * @param value the value to set
	 */
	public void addValue(int value) {
		this.value += value;
	}

	
	@Override
	public int compareTo(AvatarAttribute attr) {
		
		if (attr != null) {
			return this.type.compareTo(attr.getType());
		}
		return -1;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return the maxValue
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}	
	
}
