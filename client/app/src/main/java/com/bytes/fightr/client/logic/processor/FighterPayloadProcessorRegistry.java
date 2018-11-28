package com.bytes.fightr.client.logic.processor;

import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;
import com.bytes.fmk.payload.processor.PayloadProcessorRegistry;
import com.bytes.fmk.payload.processor.ProcessorListener;


public class FighterPayloadProcessorRegistry extends PayloadProcessorRegistry {

    public FighterPayloadProcessorRegistry() {

//        registerProcessor(getName(Payload.NOTIFY, DataType.String), new StringEventProcessor());
//        registerProcessor(getName(Payload.NOTIFY, DataType.UserEvent), new UserEventProcessor());
//        registerProcessor(getName(Payload.NOTIFY, DataType.ChatEvent), new ChatEventProcessor());
        registerProcessor(getName(Payload.NOTIFY, DataType.MatchEvent), new MatchEventProcessor());
        registerProcessor(getName(Payload.NOTIFY, DataType.ActionEvent), new ActionEventProcessor());

        // TODO Add Processors
//        registerProcessor(getName(Payload.NOTIFY, DataType.Fighter), new FighterProcessor());
//        registerProcessor(getName(Payload.NOTIFY, DataType.String), new StartMatchProcessor());
//        registerProcessor(getName(Payload.NOTIFY, DataType.FightAction), new ActionProcessor());
//        registerProcessor(getName(Payload.NOTIFY, DataType.String), new EndPayloadProcessor());
//        registerProcessor(getName(Type.Debug, DataType.String), new DebugProcessor());
    }


    /**
     * Add the specified listener to all registered payload processors.
     * The listener list for each processor is backed by a {@code Set}.
     * Each processor will only add the listener once.
     *
     * @param processorName the name of the processor
     * @param listener      the listener to be added
     * @see #getName(int, DataType)
     */
    public void addListener(String processorName, ProcessorListener listener) {
        if (processors.containsKey(processorName)) {
            processors.get(processorName).addListener(listener);
        }
    }

    /**
     * Remove the specified listener from all registered payload processors.
     * The listener list for each processor is backed by a {@code Set}.
     * Each processor will only add the listener once.
     *
     * @param processorName the name of the processor
     * @param listener      the listener to be removed
     * @see #getName(int, DataType)
     */
    public void removeListener(String processorName, ProcessorListener listener) {
        if (processors.containsKey(processorName)) {
            processors.get(processorName).removeListener(listener);
        }
    }

//    /**
//     * Add the specified listener to all registered payload processors.
//     * The listener list for each processor is backed by a {@code Set}.
//     * Each processor will only add the listener once.
//     *
//     * @param listener the listener to be added
//     */
//    public void addListener(ProcessorListener listener) {
//        for (PayloadProcessor processor : processors.values()) {
//            processor.addListener(listener);
//        }
//    }
//
//    /**
//     * Remove the specified listener from all registered payload processors.
//     *
//     * @param listener the listener to be removed
//     */
//    public void removeListener(ProcessorListener listener) {
//        for (PayloadProcessor processor : processors.values()) {
//            processor.removeListener(listener);
//        }
//    }

    /**
     * Override to expect the {@code FighterPayload#getType()} and {@code FighterPayload#getDataType()}
     * as the key to retrieve the processor.
     *
     * @param name - the payload type concatenated with the data type
     */
    @Override
    public AbstractPayloadProcessor getProcessor(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Processor key cannot be null");
        }

        if (!processors.containsKey(name)) {
            logger.warn("Redirecting request to null processor: " + name);
            return nullHandler;

        }
        return processors.get(name);
    }

    /**
     * A convenience method to retrieve the processor key
     *
     * @param payloadType - the payload type
     * @param dataType    - the payload data type
     * @return the name to be used as the key to retrieve the processor
     */
    public String getName(int payloadType, DataType dataType) {
        return getName(payloadType, dataType.name());
    }

    /**
     * A convenience method to retrieve the processor key
     *
     * @param payloadType - the payload type
     * @param dataType    - the payload data type
     * @return the name to be used as the key to retrieve the processor
     */
    public String getName(int payloadType, String dataType) {
        return payloadType + dataType;
    }

}
