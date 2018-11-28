package com.bytes.fightr.server.logic.processor;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.BaseServiceTest;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fightr.server.service.comm.MockedSession;
import com.bytes.fmk.data.event.UserEvent;
import com.bytes.fmk.data.model.User;
import com.bytes.fmk.observer.DestinationType;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.google.gson.Gson;

/**
 * Provides the default scaffolding needed to test a payload processor. 
 * 	This includes user1 and user2 and their corresponding fighters and sessions
 * 
 * @author Kent
 *
 */
public abstract class AbstractPayloadProcessorTest<T> extends BaseServiceTest {

	protected static Gson gson = new Gson();

	protected AbstractPayloadProcessor processor;

	protected int payloadType;
	protected FighterPayload basicFlowPayload;
	
	@Before 
	public void init() {
		super.setup();
		
		// Initialize abstract data
		processor = getProcessor();
		payloadType = getPayloadType();
		basicFlowPayload = getBasicFlowPayload();
	}

	/**
	 * A basic test to verify the expected payload type and data type of the processor
	 */
	@Test
	public void validationTest() {
		Assert.assertTrue("Payload validation", processor.validate(basicFlowPayload));
	}
	
	/**
	 * A basic test to verify that the overall flow has no errors. 
	 * Do not rely on this test to validate processor specific logic.
	 */
	@Test
	public void processTest() {
		Payload<String> responsePayload = processor.process(basicFlowPayload);
		if (responsePayload != null) {
			Assert.assertEquals("Payload response expected to have the correct response data type", 
				getResponeDataType().toString(), responsePayload.getDataType());
		}
	}


	/**
	 * @return the processor
	 */
	public abstract AbstractPayloadProcessor getProcessor();
	

	/**
	 * @return the payloadType
	 */
	public abstract int getPayloadType();


	/**
	 * @return the payload
	 */
	private FighterPayload getBasicFlowPayload() {
		
		FighterPayload payload = new FighterPayload(
				getPayloadType(),
				getDataType(),
				gson.toJson(getData())
		);
		payload.setSourceId(session1.getId());
		return payload;
	}
	
	/**
	 * Get the default response data type
     * @return the data type of the response payload
	 */
	public abstract DataType getResponeDataType();

    /**
     * Get the data type of the payload to be process by the processor
     * @return the data type
     */
	public abstract DataType getDataType();

    /**
     * Get the data object to be processed by the processor
     * @return the data object
     */
	public abstract T getData();
	
	/**
	 * 
	 * @param sessionId
	 */
	protected void registerSession(String sessionId) {
		MockedSession session = new MockedSession();
		session.setSessionId(sessionId);
		FightrServer.getInstance().getSessionRegistry().register(session);
	}
	
	protected void assertDestination(DestinationType destinationType, Payload<String> responsePayload) {
		
		switch (destinationType) {
		case All:
			break;
		case Other:
			break;
		case Source:
			Assert.assertEquals("Only 1 destination, source of the requesting session", 1, responsePayload.getDestinationIds().size());
			Assert.assertTrue(responsePayload.getDestinationIds().contains(session1.getId()));
			break;
		default:
			break;
		
		}
	}
	
	/**
	 * // Assert notification, all except self
	 * @param payload - the payload
	 * @param status - the optional user status
	 * @param type - the optional user event type
	 */
	protected void assertUserStateChangeNotification(Payload<String> payload, User.Status status, UserEvent.Type type) {
		Assert.assertEquals("3 destinations, 2 existing, 1 new", 3, payload.getDestinationIds().size());
		Assert.assertTrue(payload.getDestinationIds().contains(session1.getId()));
		Assert.assertTrue(payload.getDestinationIds().contains(session2.getId()));
		Assert.assertTrue(payload.getDestinationIds().contains(session3.getId()));
		
		Assert.assertEquals(Payload.NOTIFY, payload.getType());
		UserEvent state = PayloadUtil.getData(payload, DataType.UserEvent);
		
		if (type != null) {
			Assert.assertEquals(type, state.getType());
		}
		
		if (status != null) {
			Assert.assertEquals(status, state.getStatus());
		}
	}
	
	/**
	 * // Assert notification, all except self
	 * @param payload - the payload
	 * @param status - the optional user status
	 * @param type - the optional user event type
	 */
	protected void assertUserStateChangeNotification(
			Payload<String> payload, 
			User.Status status, 
			UserEvent.Type type, 
			List<String> destinations) {
		
		Assert.assertEquals(destinations.size(), payload.getDestinationIds().size());
		for (String destination : destinations) {
			Assert.assertTrue(payload.getDestinationIds().contains(destination));
		}
		Assert.assertEquals(Payload.NOTIFY, payload.getType());
		UserEvent state = PayloadUtil.getData(payload, DataType.UserEvent);
		if (type != null) {
			Assert.assertEquals(type, state.getType());
		}
		if (status != null) {
			Assert.assertEquals(status, state.getStatus());
		}
	}
}
