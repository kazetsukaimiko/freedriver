package io.freedriver.serial.api;

import java.nio.file.Path;

public class FakeEchoTest extends EchoTest<FakeSerialResource> {
    @Override
    public FakeSerialResource construct() {
        return new FakeSerialResource();
    }
}
