package com.bytes.gamr.model.avatar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the Player, AI and human controlled
 * @author Kent
 */
public abstract class Avatar implements Serializable {

	protected transient Logger logger = LoggerFactory.getLogger(getClass());
	
	/** */
	private static final long serialVersionUID = 1L;

	/** Player types */
	public enum AvatarType {
		AI, 
		Human,
		Companion, // Human controlled AI
		NPC
	}
	
	
	// Type of avatar
	protected AvatarType type;
	
	// Name of the avatar
	protected String name;
	
	// Avatar attributes
	protected HashMap<AvatarAttribute.Type, AvatarAttribute> attributes;
	
	// Avatar status
	protected HashMap<AvatarPower.Type, AvatarPower> powers;
	
	// Event listener 
	protected AvatarEventListener eventListener;
	
	// The maximum number of skills an avatar can equip during a battle
	private int maxEquipedSkill;
	
	/**
	 * 
	 * @param type
	 * @param name
	 */
	public Avatar(AvatarType type, String name) {
		this.type = type;
		this.name = name;
		this.maxEquipedSkill = 3;
		
		attributes = new HashMap<AvatarAttribute.Type, AvatarAttribute>();
		powers = new HashMap<AvatarPower.Type, AvatarPower>();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getName() + "\n");
		
		// Sort powers
		ArrayList<AvatarPower> avatarPowers = new ArrayList<>(powers.values());
		Collections.sort(avatarPowers);
		
		// Sort attributes
		ArrayList<AvatarAttribute> avatarAttr = new ArrayList<AvatarAttribute>(attributes.values());
		Collections.sort(avatarAttr);

		for (AvatarAttribute attr : avatarAttr) {
			sb.append(String.format(" - %1$-17s %2$ 3d%3$s", 
				attr.getType().toString(), attr.getValue(), "\n"));			
		}
		
		for (AvatarPower power : avatarPowers) {
			sb.append(String.format(" - %1$-17s %2$ 3d%3$s", 
				power.getType().toString(), power.getValue(), "\n"));			
		}
		
		return sb.toString();
	}
	
	/**
	 * @return the type
	 */
	public AvatarType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(AvatarType type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public AvatarAttribute getAttributes(AvatarAttribute.Type type) {
		return attributes.get(type);
	}

	/**
	 * 
	 * @param type
	 * @param attribute
	 */
	public void setAttributes(AvatarAttribute.Type type, AvatarAttribute attribute) {
		this.attributes.put(type, attribute);
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public AvatarPower getPower(AvatarPower.Type type) {
		return powers.get(type);
	}

	/**
	 * Add the specified attribute to the avatar.
	 * @param type - the attribute type
	 * @param value - the value to add or subtract
	 */
	public void addToAttributes(AvatarAttribute.Type type, int value) {
		
		AvatarAttribute attr = getAttributes(type);
		if (attr == null) {
			logger.error("Attribute is null. Type is " + type);
			return;
		}
		
		attr.setValue(attr.getValue() + value);
		
		if (eventListener != null) {
			eventListener.attributeChanged(this, attr, value);
		}
	}	
	
	
	/**
	 * Add to the avatar's power with the matching power type.
	 * @param type - the power type
	 * @param value - the value to add or subtract
	 */
	public void addToPower(AvatarPower.Type type, int value) {

		AvatarPower attr = powers.get(type);
		attr.addValue(value);
		
		if (eventListener != null) {
			eventListener.powerChanged(this, attr, value);
		}
	}


	/**
	 * @return the eventListener
	 */
	public AvatarEventListener getEventListener() {
		return eventListener;
	}

	/**
	 * @param eventListener the eventListener to set
	 */
	public void setEventListener(AvatarEventListener eventListener) {
		this.eventListener = eventListener;
	}


	/**
	 * @return the maxEquipedSkill
	 */
	public int getMaxEquipedSkill() {
		return maxEquipedSkill;
	}

	/**
	 * @param maxEquipedSkill the maxEquipedSkill to set
	 */
	public void setMaxEquipedSkill(int maxEquipedSkill) {
		this.maxEquipedSkill = maxEquipedSkill;
	}

	/**
	 * @return the attributes
	 */
	public HashMap<AvatarAttribute.Type, AvatarAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(HashMap<AvatarAttribute.Type, AvatarAttribute> attributes) {
		if (attributes == null || attributes.isEmpty()) {
			throw new IllegalArgumentException("Attributes should not be set to null or empty.");
		}
		this.attributes = attributes;
	}

	/**
	 * @return the powers
	 */
	public HashMap<AvatarPower.Type, AvatarPower> getPowers() {
		return powers;
	}

	/**
	 * @param powers the powers to set
	 */
	public void setPowers(HashMap<AvatarPower.Type, AvatarPower> powers) {
		if (powers == null || powers.isEmpty()) {
			throw new IllegalArgumentException("Powers should not be set to null or empty.");
		}
		this.powers = powers;
	}
}
