package com.bytes.fightr.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.fmk.data.model.User;
import com.bytes.gamr.model.avatar.Avatar;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarPower;

public class Fighter extends Avatar {
	
	public enum State {
		None,
		Attacking,
		Blocking,
		Preparing, 
		Blinded, 
		Interrupted 
	}
	
	/** Serialization ID, not really used */
	private static final long serialVersionUID = 1;
	
	private volatile State state;
	
	
	/**
	 * This uniquely identifies a fighter
	 */
	private String id;
	
	/**
	 * The owner of this avatar
	 */
	private String userId;
	
	// Skill
	private Map<Integer, FighterSkill.Id> equipedSkills;
		
	// All known skills for this fighter
	private Collection<FighterSkill.Id> skills;
		
	// The active action
	private FighterSkill.Id activeSkill;
		
	/**
	 * Create a Fighter with the specified name. 
	 * Default type to Human
	 * @param name
	 */
	public Fighter(String name) {
		this(AvatarType.Human, name);
	}
	
	/**
	 * Create a Fighter with the specified type and name.
	 * @param type
	 * @param name
	 */
	public Fighter(AvatarType type, String name) {
		super(type, name);
		
		setId(UUID.randomUUID().toString());
		equipedSkills = new HashMap<>();
		skills = new ArrayList<>();
		generateInitialStats();
	}

	/**
	 * 
	 */
	private void generateInitialStats() {
		
		// Attributes
		this.attributes.put(AvatarAttribute.Type.HEALTH, new AvatarAttribute(AvatarAttribute.Type.HEALTH, 5, 10));
		
		// Powers
		this.powers.put(AvatarPower.Type.ENERGY_STAMINA, new AvatarPower(AvatarPower.Type.ENERGY_STAMINA, 10, 100));
				
		// Skills
		setMaxEquipedSkill(8);
		
		// State
		this.state = State.Preparing;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public Fighter setId(String id) {
		this.id = id;
		return this;
	}
	
	/**
	 * Associate the specified User to this Fighter 
	 * @param user - the user to associate this fighter to
	 */
	public Fighter linkUser(User user) {
		this.setId(user.getId());
		this.setUserId(user.getId());
		user.setAvatarId(this.getId());
		return this;
	}
	
	/**
	 * Request to update a fighter with data from another instance.
	 * The id must match.
	 * @param fighter the fighter containing the new data
	 * @return true if the update was successful
	 */
	public boolean update(Fighter fighter) {
		if (fighter == null || !id.equals(fighter.getId())) {
			return false;
		}
		
		setState(fighter.getState());
		
		if (fighter.getEquipedSkills() != null) {
			setEquipedSkills(fighter.getEquipedSkills());
		}
		
		if (fighter.getSkills() != null) {
			setSkills(fighter.getSkills());
		}
		
		setActiveSkill(fighter.getActiveSkill());
		setAttributes(fighter.getAttributes());
		setPowers(fighter.getPowers());
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Fighter) {
			String id1 = getId();
			String id2 = ((Fighter) obj).getId();
			
			if (id1 == null || id2 == null) {
				return id1 == id2;
			}
			
			return id1.equals(id2);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (getId() == null) return 0;
		return getId().hashCode();
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the equipedSkills
	 */
	public Map<Integer, FighterSkill.Id> getEquipedSkills() {
		return equipedSkills;
	}

	/**
	 * @param equipedSkills the equipedSkills to set
	 */
	public void setEquipedSkills(Map<Integer, FighterSkill.Id> equipedSkills) {
		if (equipedSkills == null || equipedSkills.isEmpty()) {
			throw new IllegalArgumentException(
				"Equipped skills should never be set to null or empty. This is likely a mistake.");
		}
		this.equipedSkills = equipedSkills;
	}

	/**
	 * All skills belonging to this Fighter, 
	 * equipped and non-equipped skills
	 * @return the skills
	 */
	public Collection<FighterSkill.Id> getSkills() {
		return skills;
	}

	/**
	 * @param skills the skills to set
	 */
	public void setSkills(Collection<FighterSkill.Id> skills) {
		if (skills == null || skills.isEmpty()) {
			throw new IllegalArgumentException(
				"Skills should never be set to null or empty. This is likely a mistake.");
		}
		this.skills = skills;
	}

	/**
	 * @return the activeSkill
	 */
	public FighterSkill.Id getActiveSkill() {
		return activeSkill;
	}

	/**
	 * @param activeSkill the activeSkill to set
	 */
	public void setActiveSkill(FighterSkill.Id activeSkill) {
		this.activeSkill = activeSkill;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
