package io.freedriver.electrodacus.sbms;

import io.freedriver.serial.stream.api.Accumulator;
import io.freedriver.serial.stream.api.BaseStreamListener;
import io.freedriver.serial.stream.api.SerialStream;
import io.freedriver.serial.stream.api.StreamListener;

import java.io.IOException;

public class SBMSMessageStreamer extends BaseStreamListener<SBMSMessage> implements StreamListener<SBMSMessage> {
    public SBMSMessageStreamer(SerialStream stream, Accumulator<SBMSMessage> accumulator) {
        super(stream, accumulator);
    }
}
