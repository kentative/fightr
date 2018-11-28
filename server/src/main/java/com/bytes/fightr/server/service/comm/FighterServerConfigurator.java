package com.bytes.fightr.server.service.comm;

import java.text.MessageFormat;

import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * Configurator for pull service */
public final class FighterServerConfigurator extends ServerEndpointConfig.Configurator {

	/** * This field is the logger for this class. */
	private Logger logger = LoggerFactory.getLogger(FighterServerConfigurator.class);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		
		if (endpointClass == null) {
			InstantiationException e = new InstantiationException("Parameter expected to be a Class type instead of NULL.");
			logger.error(e.getMessage());
			throw e;
		}
		
		if (endpointClass.equals(FightrServer.class)) {
			FightrServer serverEndpoint = FightrServer.getInstance();
			logger.info("endpoint instance: " + serverEndpoint.toString());
			return (T) serverEndpoint;
			
		} else {
			InstantiationException e = new InstantiationException(
				MessageFormat.format("Expected instanceof \"{0}\". Got instanceof \"{1}\".",
						FightrServer.class, endpointClass));
			logger.error(e.getMessage());
			throw e;
		}
	}
}
