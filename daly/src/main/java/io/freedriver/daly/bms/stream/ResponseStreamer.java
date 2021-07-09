package io.freedriver.daly.bms.stream;

import io.freedriver.daly.bms.Response;
import io.freedriver.serial.stream.api.Accumulator;
import io.freedriver.serial.stream.api.BaseStreamListener;
import io.freedriver.serial.stream.api.SerialStream;
import io.freedriver.serial.stream.api.StreamListener;

public class ResponseStreamer extends BaseStreamListener<Response> implements StreamListener<Response> {
    private ResponseStreamer(SerialStream stream, Accumulator<Response> accumulator) {
        super(stream, accumulator);
    }

    public ResponseStreamer(SerialStream stream) {
        this(stream, new ResponseAccumlator());
    }
}
