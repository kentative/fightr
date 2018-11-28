package com.bytes.fightr.server.logic.processor;

import org.junit.Assert;
import org.junit.Test;

import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fightr.common.payload.PayloadUtil;
import com.bytes.fightr.server.service.GameService;
import com.bytes.fmk.observer.DestinationType;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

public class FighterProcessorTest extends AbstractPayloadProcessorTest<Fighter> {

	@Override
	public AbstractPayloadProcessor getProcessor() {
		return new FighterProcessor();
	}
	
	@Override
	public DataType getResponeDataType() {
		return DataType.Null;
	}

	@Override
	public int getPayloadType() {
		return Payload.POST;
	}

	@Override
	public DataType getDataType() {
		return DataType.Fighter;
	}

	@Override
	public Fighter getData() {
		GameService.getInstance().getUserService().registerUser(u1, session1.getId());
		return f1;
	}
	
	
	/**
	 * Test case01: 0 pre-registered players, Expects notification to 1 destination
	 */
	@Test
	public void registerFighterTest() {

		FighterPayload payload = new FighterPayload(
				getPayloadType(), 
				DataType.Fighter, 
				gson.toJson(f1));
		payload.setSourceId(session1.getId());
		Payload<String> responsePayload = processor.process(payload);
		Assert.assertNull(responsePayload);
	}
	
	@Test
	public void getFighterTest() {

		GameService.getInstance().getFighterService().registerFighter(f1);
		Fighter fighterId = new Fighter(null);
		fighterId.setId(f1.getId());
		
		FighterPayload payload = new FighterPayload(
				Payload.GET, 
				DataType.Fighter, 
				gson.toJson(fighterId));
		payload.setSourceId(session1.getId());
		Payload<String> responsePayload = processor.process(payload);
		
		Assert.assertNotNull(responsePayload);
		Assert.assertEquals(Payload.NOTIFY, responsePayload.getType());
		Assert.assertEquals(DataType.Fighter.toString(), responsePayload.getDataType());
		
		Fighter data = PayloadUtil.getData(responsePayload, DataType.Fighter);
		Assert.assertEquals(f1.getId(), data.getId());
		Assert.assertEquals(f1.getName(), data.getName());
		
		assertDestination(DestinationType.Source, responsePayload);
	}
	
	@Test
	public void getFighterErrorTest() {

		Fighter fighterId = new Fighter(null);
		fighterId.setId(f1.getId());
		
		FighterPayload payload = new FighterPayload(
				Payload.GET, 
				DataType.Fighter, 
				gson.toJson(fighterId));
		payload.setSourceId(session1.getId());
		Payload<String> responsePayload = processor.process(payload);
		
		Assert.assertNotNull(responsePayload);
		Assert.assertEquals(Payload.NOTIFY, responsePayload.getType());
		Assert.assertEquals(DataType.Fighter.toString(), responsePayload.getDataType());
		Assert.assertEquals(Payload.STATUS_ERROR, responsePayload.getStatus());
		
		Fighter data = PayloadUtil.getData(responsePayload, DataType.Fighter);
		Assert.assertNull(data);
	}
}
