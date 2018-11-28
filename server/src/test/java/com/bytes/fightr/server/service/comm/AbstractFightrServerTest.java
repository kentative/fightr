package com.bytes.fightr.server.service.comm;

import java.util.UUID;

import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.server.logic.processor.ActionProcessor;
import com.bytes.fightr.server.logic.processor.EndPayloadProcessor;
import com.bytes.fightr.server.logic.processor.ErrorPayloadProcessor;
import com.bytes.fightr.server.logic.processor.FighterProcessor;
import com.bytes.fightr.server.logic.processor.MatchProcessor;
import com.bytes.fightr.server.logic.processor.MessageProcessor;
import com.bytes.fightr.server.logic.processor.ReadyProcessor;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;
import com.google.gson.Gson;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

public class AbstractFightrServerTest {

	protected Gson gson;
	
	@Mocked Session session;
	
	@Before
	public void setup() {
		gson = new Gson();
		
		new Expectations() {{
			session.getId();
			result = UUID.randomUUID().toString();
		}};
	}

	/**
	 * Verifies the correct corresponding processor is called for the payload of type 
	 * {@code FightrPayload.Type#Search} 
	 * @param processor - the expected processor
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void onMessageRegisterFighter(@Mocked final FighterProcessor processor) {

		new Expectations() {{
			processor.validate((AbstractPayload<String>) any);
			result = true;
		}};
		
		FightrServer server = FightrServer.getInstance();
		FighterPayload payload = new FighterPayload(Payload.POST);
		payload.setDataType(DataType.Fighter.name());
		String json = gson.toJson(payload);
		server.onMessage(json, session);
		
		new Verifications() {{
			processor.process((AbstractPayload<String>) any);
			times=1;
		}};

	}
	
	/**
	 * Verifies the correct corresponding processor is called for the payload of type 
	 * {@code FightrPayload.Type#Ready} 
	 * @param processor - the expected processor
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void onMessagePrepareTest(@Mocked final MatchProcessor processor) {

		new Expectations() {{
			processor.validate((AbstractPayload<String>) any);
			result = true;
		}};
		
		FightrServer server = FightrServer.getInstance();
		FighterPayload payload = new FighterPayload(Payload.POST);
		payload.setDataType(DataType.Match.name());
		String json = gson.toJson(payload);
		server.onMessage(json, session);
		
		new Verifications() {{
			processor.process((AbstractPayload<String>) any);
			times=1;
		}};

	}
	
	
	/**
	 * Verifies the correct corresponding processor is called for the payload of type 
	 * {@code FightrPayload.Type#Start} 
	 * @param processor - the expected processor
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void onMessageStartTest(@Mocked final ReadyProcessor processor) {

		new Expectations() {{
			processor.validate((AbstractPayload<String>) any);
			result = true;
		}};
		
		FightrServer server = FightrServer.getInstance();
		FighterPayload payload = new FighterPayload(Payload.POST);
		payload.setDataType(DataType.MatchEvent.name());
		String json = gson.toJson(payload);
		server.onMessage(json, session);
		
		new Verifications() {{
			processor.process((AbstractPayload<String>) any);
			times=1;
		}};

	}
	
	/**
	 * Verifies the correct corresponding processor is called for the payload of type 
	 * {@code FightrPayload.Type#Action} 
	 * @param processor - the expected processor
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void onMessageActionTest(@Mocked final ActionProcessor processor) {

		new Expectations() {{
			processor.validate((AbstractPayload<String>) any);
			result = true;
		}};
		
		FightrServer server = FightrServer.getInstance();
		FighterPayload payload = new FighterPayload(Payload.POST);
		payload.setDataType(DataType.FightAction.name());
		String json = gson.toJson(payload);
		server.onMessage(json, session);
		
		new Verifications() {{
			processor.process((AbstractPayload<String>) any);
			times=1;
		}};

	}
	
	/**
	 * Verifies the correct corresponding processor is called for the payload of type 
	 * {@code FightrPayload.Type#End} 
	 * @param processor - the expected processor
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void onMessageEndTest(@Mocked final EndPayloadProcessor processor) {

		new Expectations() {{
			processor.validate((AbstractPayload<String>) any);
			result = true;
		}};
		
		FightrServer server = FightrServer.getInstance();
		FighterPayload payload = new FighterPayload(Payload.POST);
		payload.setDataType(DataType.MatchEvent.name());
		String json = gson.toJson(payload);
		server.onMessage(json, session);
		
		new Verifications() {{
			processor.process((AbstractPayload<String>) any);
			times=1;
		}};

	}
	
	/**
	 * Verifies the correct corresponding processor is called for the payload of type 
	 * {@code FightrPayload.Type#Message} 
	 * @param processor - the expected processor
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void onMessageMessageTest(@Mocked final MessageProcessor processor) {

		new Expectations() {{
			processor.validate((AbstractPayload<String>) any);
			result = true;
		}};
		
		FightrServer server = FightrServer.getInstance();
		FighterPayload payload = new FighterPayload(
				Payload.POST, DataType.String, "Test Message");
		payload.setDataType(DataType.String.name());
		String json = gson.toJson(payload);
		server.onMessage(json, session);
		
		new Verifications() {{
			processor.process((AbstractPayload<String>) any);
			times=1;
		}};

	}
	
	/**
	 * Verifies the correct corresponding processor is called for the payload of type 
	 * {@code FightrPayload.Type#Error} 
	 * @param processor - the expected processor
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void onMessageErrorTest(@Mocked final ErrorPayloadProcessor processor) {

		new Expectations() {{
			processor.validate((AbstractPayload<String>) any);
			result = true;
		}};
		
		FightrServer server = FightrServer.getInstance();
		FighterPayload payload = new FighterPayload(Payload.POST);
		payload.setDataType(DataType.String.name());
		String json = gson.toJson(payload);
		server.onMessage(json, session);
		
		new Verifications() {{
			processor.process((AbstractPayload<String>) any);
			times=1;
		}};

	}
}
