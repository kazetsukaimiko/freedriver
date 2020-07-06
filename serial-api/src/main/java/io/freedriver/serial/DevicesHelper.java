package io.freedriver.serial;

import java.util.List;


public interface DevicesHelper<S extends SerialDevice> {
    List<S> getDevices();
}
