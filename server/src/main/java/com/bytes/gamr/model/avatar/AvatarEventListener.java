package com.bytes.gamr.model.avatar;

public interface AvatarEventListener {

	/**
	 * Attribute update notification, (i.e., health, strength, etc...).
	 * @param avatar the avatar
	 * @param attr the attribute type
	 * @param value the updated value
	 */
	public void attributeChanged(Avatar avatar, AvatarAttribute attr, int value);
	
	/**
	 * Attribute update notification, (i.e., energy, mana, etc...)
	 * @param avatar the avatar
	 * @param power the power type
	 * @param value the updated value
	 */
	public void powerChanged(Avatar avatar, AvatarPower power, int value);
	
}
