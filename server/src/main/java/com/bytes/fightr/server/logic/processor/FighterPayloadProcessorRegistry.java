package com.bytes.fightr.server.logic.processor;

import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.PayloadProcessor;
import com.bytes.fmk.payload.processor.PayloadProcessorRegistry;

public class FighterPayloadProcessorRegistry extends PayloadProcessorRegistry {
	
	public FighterPayloadProcessorRegistry() {
		
		registerProcessor(getName(Payload.POST, DataType.User), new LoginProcessor());
		registerProcessor(getName(Payload.GET,  DataType.User), new UserProcessor());
		registerProcessor(getName(Payload.POST, DataType.Fighter), new FighterProcessor());
		registerProcessor(getName(Payload.POST, DataType.Message), new MessageProcessor());
		registerProcessor(getName(Payload.GET, DataType.Fighter), new FighterProcessor());
		registerProcessor(getName(Payload.POST, DataType.Match), new MatchProcessor());
		registerProcessor(getName(Payload.UPDATE, DataType.User), new UserProcessor());
		
		registerProcessor(getName(Payload.POST, DataType.String), new ReadyProcessor());
		registerProcessor(getName(Payload.POST, DataType.FightAction), new ActionProcessor());
		registerProcessor(getName(Payload.POST, DataType.String), new EndPayloadProcessor());
		registerProcessor(getName(Payload.POST, DataType.String), new ErrorPayloadProcessor());
		registerProcessor(getName(Payload.POST, DataType.String), new DebugProcessor());
		registerProcessor(getName(Payload.POST, DataType.LatencyCmd), new DebugProcessor());
	}
	
    /**
     * Override to expect the {@code FighterPayload#getType()} and {@code FighterPayload#getDataType()}
     * as the key to retrieve the processor.
     * @param payloadAndDataType - the payload type concatenated with the data type
     * @see #getName(Type, DataType)
     */
	@Override
    public PayloadProcessor<String> getProcessor(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Processor key cannot be null");
		}
		
        if (!processors.containsKey(name)) {
        	logger.warn("Redirecting request to null processor: " + name);
            return nullHandler;

        }
        return (processors.get(name));
    }

	/**
	 * A convenience method to retrieve the processor key
	 * @param payloadType - the payload type
	 * @param dataType - the payload data type
	 * @return
	 */
	public String getName(int payloadType, DataType dataType) {
		return getName(payloadType, dataType.name());
	}
	
	/**
	 * A convenience method to retrieve the processor key
	 * @param payloadType - the payload type
	 * @param dataType - the payload data type
	 * @return
	 */
	public String getName(int payloadType, String dataType) {
		return payloadType + dataType;
	}

}
