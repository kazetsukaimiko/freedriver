package io.freedriver.serial.stream.api;

import io.freedriver.serial.api.FakeSerialResource;

public class FakeSerialResourceStreamsEchoTest extends StreamsEchoTest<FakeSerialResource> {
    @Override
    public FakeSerialResource construct() {
        return new FakeSerialResource();
    }
}
