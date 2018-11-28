package com.bytes.fightr.server.logic.processor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.PayloadProcessor;

public class FightrPayloadRegistryTest {

	private FighterPayloadProcessorRegistry registry;
	
	@Before 
	public void setup() {
		 registry = new FighterPayloadProcessorRegistry();
	}
	
	@Test
    public void getProcessorTest() {
		
		assertProcessor(LoginProcessor.class, 
				Payload.POST, DataType.User);
		
		assertProcessor(FighterProcessor.class, 
				Payload.POST, DataType.Fighter);
		
		assertProcessor(MatchProcessor.class, 
				Payload.POST, DataType.Match);
		
		assertProcessor(MatchProcessor.class, 
				Payload.POST, DataType.Match);
		
		assertProcessor(MessageProcessor.class, 
				Payload.POST, DataType.Message);
		
		assertProcessor(DebugProcessor.class, 
				Payload.POST, DataType.String);
		
		assertProcessor(DebugProcessor.class, 
				Payload.POST, DataType.LatencyCmd);
		
    }
	
	private void assertProcessor(Class<?> expected, int payloadType, DataType dataType) {
		PayloadProcessor<String> processor = registry.getProcessor(registry.getName(payloadType, dataType.name()));
		Assert.assertEquals(
				expected.getCanonicalName(), 
				processor.getClass().getCanonicalName());
		
	}
}
