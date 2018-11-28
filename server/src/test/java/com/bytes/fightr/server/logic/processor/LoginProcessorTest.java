package com.bytes.fightr.server.logic.processor;

import org.junit.Assert;
import org.junit.Test;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.event.LoginEvent;
import com.bytes.fmk.data.event.UserEvent;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.data.model.User.Status;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import mockit.Mock;
import mockit.MockUp;

public class LoginProcessorTest extends AbstractPayloadProcessorTest<User> {

	@Override
	public AbstractPayloadProcessor getProcessor() {
		return new LoginProcessor();
	}
	
	@Override
	public DataType getResponeDataType() {
		return DataType.LoginEvent;
	}

	@Override
	public int getPayloadType() {
		return Payload.POST;
	}

	@Override
	public DataType getDataType() {
		return DataType.User;
	}

	@Override
	public User getData() {
		return u1;
	}
	
	/**
	 * input: valid user
	 * output: Register OK
	 * notification: ALL registered users, containing User.Status.Available
	 */
	@Test
	public void registerUserTest() {
		
		final User newUser = new User("newUser");
		Payload<String> payload = new FighterPayload(
				getPayloadType(), 
				DataType.User, 
				gson.toJson(newUser));
		payload.setSourceId(session3.getId());
		registerSession(session3.getId());
		
		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				assertUserStateChangeNotification(
						payload, 
						Status.Available, 
						UserEvent.Type.UserAdded);
			}
		};
		
		// Assert response
		Payload<String> responsePayload = processor.process(payload);
		Assert.assertEquals(Payload.NOTIFY, responsePayload.getType());
		LoginEvent data = PayloadUtil.getData(responsePayload, DataType.LoginEvent);
		Assert.assertEquals(LoginEvent.Type.RegisterOK, data.getType());
	}
	
	/**
	 * input: duplicated user
	 * output: error - non unique id
	 * notification: none
	 */
	@Test
	public void registerNonUniqueUserTest() {
		
		// Register u1
		u1.setStatus(Status.New);
		String sessionId3 = "sessionId3";
		Payload<String> payload1 = new FighterPayload(
				getPayloadType(), 
				DataType.User, 
				gson.toJson(u1));
		payload1.setSourceId(sessionId3);
		registerSession(sessionId3);
		processor.process(payload1);
		
		// Register another user, duplicate of u1
		User duplicatedUser = new User(u1.getDisplayName());
		duplicatedUser.setId(u1.getId());
		String sessionId4 = "sessionId4";
		Payload<String> payload2 = new FighterPayload(
				getPayloadType(), 
				DataType.User, 
				gson.toJson(u1));
		payload2.setSourceId(sessionId4);
		registerSession(sessionId4);

		// Assert notification
		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				Assert.assertNotEquals(Payload.NOTIFY, payload.getType());
			}
		};
				
		Payload<String> responsePayload = processor.process(payload2);
		Assert.assertEquals(Payload.NOTIFY, responsePayload.getType());
		LoginEvent data = PayloadUtil.getData(responsePayload, DataType.LoginEvent);
		Assert.assertEquals(LoginEvent.Type.RegisterFailed, data.getType());
		
		// Assert destination
		Assert.assertEquals("Only 1 destination, source of the requesting session", 1, responsePayload.getDestinationIds().size());
		Assert.assertTrue(responsePayload.getDestinationIds().contains(sessionId4));
	}
	
	/**
	 * input: registered user
	 * output: Login OK
	 * notification: All registered users, containing User.Status.Available
	 */
	@Test
	public void loginUserTest() {
		
		service.getUserService().registerUser(u1, session1.getId());
		
		// Login u1
		u1.setStatus(Status.Offline);
		Payload<String> payload = new FighterPayload(
				Payload.POST, 
				DataType.User, 
				gson.toJson(u1));
		payload.setSourceId(session1.getId());
		
		// Assert notification
		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				assertUserStateChangeNotification(
						payload, 
						Status.Available, 
						UserEvent.Type.StateUpdate);
			}
		};
		
		processor.process(payload);
		Payload<String> responsePayload = processor.process(payload);
		Assert.assertEquals(Payload.NOTIFY, responsePayload.getType());
		LoginEvent data = PayloadUtil.getData(responsePayload, DataType.LoginEvent);
		Assert.assertEquals(LoginEvent.Type.LoginOK, data.getType());
		
		// Assert destination
		Assert.assertEquals("Only 1 destination, source of the requesting session", 1, responsePayload.getDestinationIds().size());
		Assert.assertTrue(responsePayload.getDestinationIds().contains(session1.getId()));
	}
	
}
