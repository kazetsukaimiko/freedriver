package io.freedriver.serial.api;


public class FakeEchoTest extends EchoTest<FakeSerialResource> {
    @Override
    public FakeSerialResource construct() {
        return new FakeSerialResource();
    }
}
