package com.bytes.gamr.model.avatar.profession;

import com.bytes.gamr.model.avatar.Avatar;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarPower;

public class Knight extends Avatar {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param type
	 * @param name
	 */
	public Knight(AvatarType type, String name) {
		super(type, name);
		
		generateInitialStats();
	}
	
	private void generateInitialStats() {
		
		// Attributes
		attributes.put(AvatarAttribute.Type.HEALTH, 
			new AvatarAttribute(AvatarAttribute.Type.HEALTH, 120, 1000));
		
		attributes.put(AvatarAttribute.Type.DEXTERITY, 
				new AvatarAttribute(AvatarAttribute.Type.DEXTERITY, 6, 1000));
		
		attributes.put(AvatarAttribute.Type.STRENGTH, 
				new AvatarAttribute(AvatarAttribute.Type.STRENGTH, 10, 1000));
		
		attributes.put(AvatarAttribute.Type.INTELLIGENCE, 
				new AvatarAttribute(AvatarAttribute.Type.INTELLIGENCE, 5, 1000));
		
		// Powers
		powers.put(AvatarPower.Type.MANA_RED, 
				new AvatarPower(AvatarPower.Type.MANA_RED, 0, 100));
		
		powers.put(AvatarPower.Type.MANA_BLUE, 
				new AvatarPower(AvatarPower.Type.MANA_BLUE, 0, 100));
		
		powers.put(AvatarPower.Type.MANA_GREEN, 
				new AvatarPower(AvatarPower.Type.MANA_GREEN, 0, 100));
		
		powers.put(AvatarPower.Type.MANA_LIGHT, 
				new AvatarPower(AvatarPower.Type.MANA_LIGHT, 0, 100));
		
		powers.put(AvatarPower.Type.MANA_DARK, 
				new AvatarPower(AvatarPower.Type.MANA_DARK, 0, 100));
		
		powers.put(AvatarPower.Type.MANA_ELEMENTAL, 
				new AvatarPower(AvatarPower.Type.MANA_ELEMENTAL, 0, 100));
		
		powers.put(AvatarPower.Type.ENERGY_ADRENALINE, 
				new AvatarPower(AvatarPower.Type.ENERGY_ADRENALINE, 20, 100));
		
		powers.put(AvatarPower.Type.ENERGY_STAMINA, 
				new AvatarPower(AvatarPower.Type.ENERGY_STAMINA, 20, 100));
		
		// Skills
		setMaxEquipedSkill(8);

	}
}
