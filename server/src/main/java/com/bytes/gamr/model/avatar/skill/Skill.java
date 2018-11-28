package com.bytes.gamr.model.avatar.skill;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bytes.gamr.model.avatar.Avatar;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarPower;


public abstract class Skill<T extends Avatar> {

	protected transient Logger logger = LoggerFactory.getLogger(getClass());
	
	/** Name of the skill. This is displayed to the player for activation. */
	protected transient String name;
	
	/** 
	 * Concise description of the skill. It should be worded as elegantly as possible. 
	 * Should contain type of attack and its general mean to achieved its effects, 
	 * (i.e., Fire Arrow - "A bow attack. Red mana is used to imbue fire on the arrow." 
	 */  
	protected transient String description;
			
	/**
	 * This list of power required to activate this skill. If more than powers 
	 * are required (i.e., skill required that target has less than 50% health), 
	 * override {@link #prepare(Avatar, ArrayList, Avatar)} to perform additional requirements.
	 */
	protected transient ArrayList<AvatarPower> powerCosts;
	
	/** 
	 * The amount of turn or time require to perform this skill. 
	 * A -1 value means this skill can be performed repeatedly.  
	 */
	protected transient int turnCost;
	
	/** 
	 * The number of turns or time elapsed before the effect this skill is active.
	 * Should always be positive. Zero value means this skill is effective immediately.
	 */
	protected transient int skillDelayCost;
	
	/** 
	 * The number of turns or time elapsed before the effect of this skill is inactive. 
	 * A -1 value means this skill last indefinitely (i.e., channeling), 
	 * used for sustained skills. Zero value means this skill effect 
	 * only apply in this turn.
	 */
	protected transient int skillDuration;
	
	/** 
	 * The number turns or time required before this skill can be re-activated. 
	 */ 
	protected transient int skillCooldown;
	
	/**
	 * Indicates whether the skill can be interrupted. 
	 * Used for skill taking more than 1 turn to activate.
	 */
	protected transient boolean interruptable;
	
	/**
	 * This counters decrement each turns.  
	 * It tracks the number of turns since the skill was activated. 
	 * When this reaches 0, the skill affects will be applied.
	 */
	protected transient int delayCounter;
	
	protected transient int cooldownCounter;
	protected transient int durationCounter;
	
	protected transient SkillListener<T> listener;
	
	/**
	 * Skill constructor.
	 */
	public Skill() {
		
		// Defaults
		name = "Attack";
		description = "A punch";
		powerCosts = new ArrayList<AvatarPower>();
		powerCosts.add(new AvatarPower(AvatarPower.Type.ENERGY_ADRENALINE, -1));
		
		skillCooldown = 0;  	// 1 - takes an additional turns to reactivate
		skillDelayCost = 0; 	// 1 - turn before effects are applied
		skillDuration = 0;  	// 0 - when applied, instant effects
		turnCost = 1;			// 1 - can only be used once per turn, ends turn
		
		interruptable = true; 	// If attacked, this skill is cancelled.
		
		// Implementation provided by subclass
		initializeSkillParameters();
		
		// These variables are not part of the skill parameters:
		delayCounter = skillDelayCost;
		cooldownCounter = skillCooldown;
		durationCounter = skillDuration;
	}
	
	/** 
	 * Initializes skill parameters. 
	 * This allows concrete skill classes to efficiently defines its parameters. 
	 */
	protected abstract void initializeSkillParameters();
	
	/**
	 * Apply the skill effects.
	 */
	protected abstract void applySkillEffects(T source, T target);
	
	
	/**
	 * Apply damage to the specified target. 
	 *  
	 * @param target - the target of the damage
	 * @param damage - a negative value
	 */
	protected void applyDamage(Avatar target, int damage) {
		target.addToAttributes(AvatarAttribute.Type.HEALTH, -damage);
	}
	
	/**
	 * This is the default implementation. 
	 * Prepare the skill for activation, check for all required conditions.
	 * Returns true if the skill is ready and can be activated, otherwise, returns false.
	 * 
	 * @param source - the avatar activating the skill
	 * @param target - the target of the skill
	 * @return true if this skill can be activated, false otherwise
	 */
	protected boolean prepare(Avatar source, ArrayList<AvatarPower> costs, Avatar target) {
		
		// Check for required cost
		boolean hasRequiredCost = hasRequiredCost(source, costs);

		// Check for delay
		boolean hasDelay = false;
		if (hasRequiredCost) {
			hasDelay = (delayCounter > 0);
		}
		
		// Prepared if these conditions are satisfied:
		return hasRequiredCost && !hasDelay;
	}
	
	/**
	 * 
	 * @param source
	 * @param costs
	 * @return
	 */
	private boolean hasRequiredCost(Avatar source, ArrayList<AvatarPower> costs) {
		for (AvatarPower cost : costs) {
			AvatarPower attr = source.getPower(cost.getType());
			if (attr == null) {
				System.err.println("Missing required cost: " + cost.getType());
				return false;
			}
			
			if ((attr.getValue() < cost.getValue())) {
				return false;
			}
		}
		
		return true;
	}
	

	/**
	 * Apply activation cost of this skill
	 * @param source - the avatar invoking the skill
	 * @param costs - the list containing the costs of the skill
	 */
	protected void applyActivationCost(Avatar source, List<AvatarPower> costs) {
		
		for (AvatarPower attr : costs) {
			source.addToPower(attr.getType(), -(attr.getValue()));
		}
	}

	
	/**
	 * This is the default implementation. 
	 * Perform the requested skill.
	 * 
	 * @param source - the avatar activating the skill
	 * @param target - the target of the skill
	 */
	public void activate(T source,  T target) {
		
		ArrayList<AvatarPower> costs = getPowerCosts();
		
		// Prepare skill
		if (prepare(source, costs, target)) {
			
			// Apply activation cost
			applyActivationCost(source, costs);
			
			applySkillEffects(source, target);
			
			// Reset delay counter, this applies to turn-based
			delayCounter = skillDelayCost;
			
			if (listener != null) {
				listener.activated(this, source, target);
			}
		}
	}

	/**
	 * @return the powerCosts
	 */
	public ArrayList<AvatarPower> getPowerCosts() {
		return powerCosts;
	}

	/**
	 * @return the turnCost
	 */
	public int getTurnCost() {
		return turnCost;
	}

	/**
	 * @return the skillDelayCost
	 */
	public int getSkillDelayCost() {
		return skillDelayCost;
	}

	/**
	 * @return the skillDuration
	 */
	public int getSkillDuration() {
		return skillDuration;
	}

	/**
	 * @return the skillCooldown
	 */
	public int getSkillCooldown() {
		return skillCooldown;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Decrement all counters for this skill
	 */
	public void decrementCounters() {
		
		delayCounter--;
		cooldownCounter--;
		durationCounter--;
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public boolean isEnabled(Avatar source, Avatar target) {
		return hasRequiredCost(source, getPowerCosts());
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(SkillListener<T> listener) {
		this.listener = listener;
	}

	/**
	 * @return the interruptable
	 */
	public boolean isInterruptable() {
		return interruptable;
	}

	/**
	 * @param interruptable the interruptable to set
	 */
	public void setInterruptable(boolean interruptable) {
		this.interruptable = interruptable;
	}
}
