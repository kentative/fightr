package com.bytes.fightr.common.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bytes.fightr.common.model.skill.FighterSkill;
import com.bytes.fightr.common.model.skill.SkillRegistry;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.bytes.gamr.model.avatar.Avatar.AvatarType;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarPower;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FighterTest {

	private static FighterSkill.Id activeSkill;
	private static FighterSkill.Id firstEquippedSkill;
	
	@BeforeClass
	public static void init() {
		activeSkill = FighterSkill.Id.Attack;
		firstEquippedSkill = FighterSkill.Id.Attack; 
	}
	
	@Test
	public void constructorTest() {

		Fighter f1 = new Fighter(AvatarType.Human, "Ryu");

		assertEquals("Initial Health", 5, f1.getAttributes(AvatarAttribute.Type.HEALTH).getValue());
		assertEquals("Initial Stamina", 10, f1.getPower(AvatarPower.Type.ENERGY_STAMINA).getValue());
	}

	@Test
	public void equalsTest() {

		Fighter f1 = new Fighter("Kent");
		Fighter f2 = new Fighter("Kent");
		f2.setId(f1.getId());
		Assert.assertEquals(f1, f2);

		Fighter f3 = new Fighter("Kent").setId("id1");
		Fighter f4 = new Fighter("Kent").setId("id1");
		Assert.assertEquals(f3, f4);

		Fighter f5 = new Fighter("Kent").setId("id1");
		Fighter f6 = new Fighter("Kent").setId("id2");
		Assert.assertNotEquals(f5, f6);
		
		Fighter f7 = new Fighter("Kent").setId(null);
		Fighter f8 = new Fighter("Kent").setId("id2");
		Assert.assertNotEquals(f7, f8);
		Assert.assertNotEquals(f8, f7);
		Assert.assertEquals(f7, f7);
	}

	@Test
	public void toJsonValueTest() {
		Gson gson = new Gson();
		Fighter f1 = new Fighter("Kent");
		initFighter(f1);
		String json = gson.toJson(f1);

		AbstractPayload<String> p = new FighterPayload(Payload.POST, DataType.Fighter, json);
		Assert.assertNotNull(p);
	}

	@Test
	public void fromJsonValueTest() {
		
		SkillRegistry skillR = SkillRegistry.getInstance();
		Gson gson = new Gson();
		Fighter f1 = new Fighter("Kent");
		initFighter(f1);
		String json = gson.toJson(f1);

		Fighter fighter = gson.fromJson(json, new TypeToken<Fighter>() {}.getType());
		Assert.assertNotNull(fighter);
		Assert.assertEquals(skillR.getSkill(activeSkill).getName(), 
				skillR.getSkill(fighter.getActiveSkill()).getName());
		Assert.assertEquals(skillR.getSkill(firstEquippedSkill).getName(), 
				skillR.getSkill(fighter.getEquipedSkills().get(1)).getName());
		
	}

	/**
	 * Create a fighter with equipped skills
	 */
	private void initFighter(Fighter fighter) {

		Map<Integer, FighterSkill.Id> skills = new HashMap<Integer, FighterSkill.Id>(6);
		skills.put(1, firstEquippedSkill);
		skills.put(2, FighterSkill.Id.Block);
		skills.put(3, FighterSkill.Id.Reload);
		skills.put(4, FighterSkill.Id.Disrupt);
		skills.put(5, FighterSkill.Id.ChargedAttack);
		skills.put(6, FighterSkill.Id.BlindingFlash);
		fighter.setEquipedSkills(skills);
		fighter.setSkills(skills.values());
		fighter.setActiveSkill(activeSkill);

	}
}
