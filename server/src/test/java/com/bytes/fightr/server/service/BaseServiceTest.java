package com.bytes.fightr.server.service;

import org.junit.Before;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fightr.server.service.comm.MockedSession;
import com.bytes.fightr.server.service.comm.SessionRegistry;
import com.bytes.fmk.data.model.User;

public class BaseServiceTest {

	protected GameService service = GameService.getInstance();
	protected SessionRegistry sessionRegistry;
	
	protected MockedSession session1;
	protected MockedSession session2;
	protected MockedSession session3;
	
	protected User u1;
	protected User u2;
	protected User u3;

	protected Fighter f1;
	protected Fighter f2;
	protected Fighter f3;
	
	@Before
	public void setup() {
		
		service.reset();
		service = GameService.getInstance();
		
		session1 = new MockedSession("sessionId1");
		session2 = new MockedSession("sessionId2");
		session3 = new MockedSession("sessionId3");
		
		u1 = new User("u1");
		u2 = new User("u2");
		u3 = new User("u3");

		f1 = new Fighter("f1");
		f2 = new Fighter("f2");
		f3 = new Fighter("f2");
		
		f1.linkUser(u1);
		f2.linkUser(u2);
		f3.linkUser(u3);
		
		sessionRegistry = FightrServer.getInstance().getSessionRegistry();
		sessionRegistry.register(session1);
		sessionRegistry.register(session2);
		sessionRegistry.register(session3);
		
		service.registerAI();
	}
	
	protected User clone(User user) {
		User clone = new User(user.getDisplayName());
		clone.setId(user.getId());
		clone.setStatus(User.Status.Ready);
		return clone;
	}

	protected Fighter clone(Fighter fighter) {
		Fighter clone = new Fighter(fighter.getName());
		clone.setId(fighter.getId());
		return clone;
	}
}
