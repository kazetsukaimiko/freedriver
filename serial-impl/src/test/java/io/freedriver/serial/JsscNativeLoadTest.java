package io.freedriver.serial;

import static org.junit.jupiter.api.Assertions.fail;

import jssc.SerialPortList;
import org.junit.jupiter.api.Test;

class JsscNativeLoadTest {
    @Test
    void getPortNamesLoadsTheNativeLibrary() {
        try {
            SerialPortList.getPortNames();
        } catch (Throwable thrown) {
            fail("SerialPortList.getPortNames() threw while loading jssc", thrown);
        }
    }
}
