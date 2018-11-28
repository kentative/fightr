package com.bytes.fightr.client.logic.processor;

import android.provider.ContactsContract;

import com.bytes.fightr.common.payload.FighterPayload;
import com.bytes.fightr.common.payload.FighterPayload.DataType;
import com.bytes.fmk.payload.Payload;
import com.bytes.fmk.payload.processor.AbstractPayloadProcessor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kent on 5/25/2017.
 */
public class StringEventProcessorTest extends AbstractProcessorTest<String> {

    @Override
    public AbstractPayloadProcessor getProcessor() {
        return new StringEventProcessor();
    }

    @Override
    public int getPayloadType() {
        return Payload.NOTIFY;
    }

    @Override
    public DataType getResponeDataType() {
        return DataType.Null;
    }

    @Override
    public DataType getDataType() {
        return DataType.Null;
    }

    @Override
    public String getData() {
        return "This is a message";
    }
}