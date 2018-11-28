package com.bytes.fightr.server.logic.processor;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import com.bytes.fightr.common.model.event.ChatEvent;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.comm.FightrServer;
import com.bytes.fmk.data.model.Message;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import mockit.Mock;
import mockit.MockUp;

public class MessageProcessorTest extends AbstractPayloadProcessorTest<Message> {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssZ");
	
	@Override
	public AbstractPayloadProcessor getProcessor() {
		return new MessageProcessor();
	}

	@Override
	public int getPayloadType() {
		return Payload.POST;
	}
	
	@Override
	public Message getData() {
		Message message = new Message();
		message.setMessage("Test message");
		message.setTime(sdf.format(Calendar.getInstance().getTime()));
		message.setUsername("username1");
		return message;
	}

	@Override
	public DataType getResponeDataType() {
		return DataType.ChatEvent;
	}

	@Override
	public DataType getDataType() {
		return DataType.Message;
	}
	
	/**
	 * input: registered user
	 * output: RESPONSE_OK
	 * destination: All registered users, containing User.Status.Available
	 */
	@Test
	public void sendMessageTest() {
		
		final Message message = getData();
	    // Create the user request payload
	    FighterPayload payload = new FighterPayload(
	            Payload.POST,
	            FighterPayload.DataType.Message,
	            PayloadUtil.getGson().toJson(message));
	    payload.setSourceId(session1.getId());
		
		// Assert notification
		new MockUp<FightrServer>() {
			@Mock
			public void send(Payload<String> payload) {
				Assert.assertEquals("3 destinations, 2 other", 2, payload.getDestinationIds().size());
				Assert.assertTrue(payload.getDestinationIds().contains(session2.getId()));
				Assert.assertTrue(payload.getDestinationIds().contains(session3.getId()));
				
				Assert.assertEquals(Payload.NOTIFY, payload.getType());
				ChatEvent notifiedMessage = PayloadUtil.getData(payload, DataType.ChatEvent);
				Assert.assertEquals(message.getMessage(), notifiedMessage.getMessage().getMessage());
			}
		};
		
		Payload<String> responsePayload = processor.process(payload);
		Assert.assertNull(responsePayload);
	}

}
