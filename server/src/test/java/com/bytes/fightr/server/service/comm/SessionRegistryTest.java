package com.bytes.fightr.server.service.comm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bytes.fmk.data.model.User;


public class SessionRegistryTest {
	
	private SessionRegistry registry;
	
	@Before
	public void setup() {
		registry = new SessionRegistry();
	}
	
	@Test
	public void getUserIdTest() {
		
		User u1 = new User("u1"); 
		MockedSession session = new MockedSession();
		session.setSessionId("mockedSession1");
		
		// Register user session
		registry.register(session);
		registry.registerUser(u1.getId(), session.getId());
		Assert.assertEquals("User session registered, expects user id", 
				u1.getId(), registry.getUserId(session.getId()));
		
		// Unregister user session
		registry.unregister(session.getId());
		Assert.assertEquals("User session unregister, expects null", 
			null, registry.getUserId(session.getId()));
	}

}
