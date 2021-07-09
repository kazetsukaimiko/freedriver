package io.freedriver.serial.stream.api;

public class LineListener extends BaseStreamListener<String> implements StreamListener<String> {
    public LineListener(SerialStream stream) {
        super(stream, Accumulators.linesString());
    }
}
