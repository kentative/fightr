package com.bytes.fightr.client.logic.processor;

/**
 * Created by Kent on 5/25/2017.
 */


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fmk.model.User;
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
public abstract class AbstractProcessorTest<T> {

    protected static Gson gson = new Gson();

    protected GameState gameState;
    protected AbstractPayloadProcessor processor;

    protected int payloadType;
    protected FighterPayload basicFlowPayload;

    protected User u1;
    protected User u2;

    protected Fighter f1;
    protected Fighter f2;

    protected String sessionId1;
    protected String sessionId2;

    @Before
    public void init() {

        GameState.Instance.init();
        gameState = GameState.Instance;

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
                    getResponeDataType(), responsePayload.getDataType());
        }
    }

    /**
     * @return the payload
     */
    private FighterPayload getBasicFlowPayload() {

        FighterPayload payload = new FighterPayload(
                getPayloadType(),
                getDataType(),
                gson.toJson(getData())
        );
        payload.setSourceId(sessionId1);
        return payload;
    }

    /**
     * Get the processor
     * @return the processor
     */
    public abstract AbstractPayloadProcessor getProcessor();


    /**
     * Get the default payload type
     * @return the payloadType
     */
    public abstract int getPayloadType();

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

}
