package com.bytes.fightr.server.logic.observer;

import org.junit.Assert;
import org.junit.Test;

import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.BaseServiceTest;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.event.UserEvent;
import com.bytes.fmk.data.model.User.Status;
import com.bytes.fmk.payload.Payload;

import mockit.Mock;
import mockit.MockUp;

public class UserObserverTest extends BaseServiceTest {

	
	/**
	 * Verify that when the user status is updated, a payload is sent out 
	 * with the updated information.
	 */
	@Test
	public void notifyTest() {
		
		final Status updatedStatus = Status.Battle;
		service.getUserService().registerUser(u1, session1.getId());

		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				
				Assert.assertEquals(3, payload.getDestinationIds().size());
				Assert.assertTrue(payload.getDestinationIds().contains(session1.getId()));
				
				Assert.assertEquals(Payload.NOTIFY, payload.getType());
				Assert.assertEquals(DataType.UserEvent.toString(), payload.getDataType());
				UserEvent data = PayloadUtil.getData(payload, DataType.UserEvent);
				Assert.assertEquals(u1.getId(), data.getUserId());
				Assert.assertEquals(updatedStatus, data.getStatus());
				
			}
		};
		
		u1.setStatus(updatedStatus);
		
	}
	
	/**
	 * Verify that when the user status is set to the same status (i.e., not changed)
	 * a payload is NOT sent out. 
	 */
	@Test
	public void noNotifyTest() {
		
		u1.setStatus(Status.New);
		// Registering the user will register the observer
		service.getUserService().registerUser(u1, session1.getId());

		final Status updatedStatus = Status.Battle;
		u1.setStatus(updatedStatus);
		new MockUp<FightrServer>() {

			@Mock
			public void send(Payload<String> payload) {
				Assert.fail();
			}
		};
		
		u1.setStatus(updatedStatus);
		
	}
}
