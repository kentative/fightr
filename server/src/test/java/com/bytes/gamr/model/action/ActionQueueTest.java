package com.bytes.gamr.model.action;

import org.junit.Assert;
import org.junit.Test;

import com.bytes.fightr.common.model.action.FightAction;
import com.bytes.fightr.common.model.skill.FighterSkill;


public class ActionQueueTest {
	
	@Test
	public void addTest() {
		
		int capacity = 5;
		ActionQueue<Action> q = new ActionQueue<>(capacity);
		
		for (int i = 0; i < capacity; i++) {
			q.add(new FightAction(FighterSkill.Id.Block));
		}
		Assert.assertTrue(q.size() == capacity);
		
		q.clear();
		Assert.assertTrue(q.size() == 0);
		for (int i = 0; i < capacity; i++) {
			q.add(new FightAction(FighterSkill.Id.Block));
		}
		Assert.assertTrue(q.size() == capacity);
		
		for (int i = 0; i < capacity; i++) {
			Assert.assertNotNull(q.poll());
		}
		Assert.assertNull(q.poll());
	}
	
	@Test
	public void orderTest() {
		
		int capacity = 5;
		ActionQueue<Integer> q = new ActionQueue<>(capacity);
		
		for (int i = 0; i < capacity; i++) {
			q.add(new Integer(i));
		}
		
		for (int i = 0; i < capacity; i++) {
			Assert.assertEquals((Integer) i, q.poll());
		}
	}

}
