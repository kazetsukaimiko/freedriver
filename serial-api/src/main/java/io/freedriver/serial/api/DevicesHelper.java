package io.freedriver.serial.api;

import java.util.List;


public interface DevicesHelper<S extends SerialDevice> {
    List<S> getDevices();
}
