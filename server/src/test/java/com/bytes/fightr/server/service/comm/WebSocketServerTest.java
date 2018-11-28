package com.bytes.fightr.server.service.comm;

import java.util.UUID;

import javax.websocket.Session;

import org.junit.Test;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fmk.observer.DestinationType;
import com.bytes.fmk.payload.AbstractPayload;
import com.bytes.fmk.payload.Payload;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;

public class WebSocketServerTest extends AbstractFightrServerTest {

	
	@Test
	public void sendTest(@Mocked final Session s1, @Mocked final Session s2, @Mocked final Session s3) {
		
		new Expectations() {{
			s1.getId();
			result = UUID.randomUUID().toString();
			
			s2.getId();
			result = UUID.randomUUID().toString();
			
			s3.getId();
			result = UUID.randomUUID().toString();
		}};
		
		FightrServer server = FightrServer.getInstance();
		SessionRegistry sessionRegistry = new SessionRegistry();
		sessionRegistry.register(s1);
		sessionRegistry.register(s2);
		sessionRegistry.register(s3);
		
		Deencapsulation.setField(server, "sessionRegistry", sessionRegistry);
		
		AbstractPayload<String> payload = new FighterPayload(Payload.POST);
		payload.setSourceId("sourceSessionId");
		server.send(payload, DestinationType.All);
		
		new Verifications() {{
			s1.isOpen();
			times=1;
			
			s2.isOpen();
			times=1;
			
			s3.isOpen();
			times=1;
		}};
	}
}
