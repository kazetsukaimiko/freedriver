package io.freedriver.serial.discovery;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import jssc.SerialPortList;

public class SerialDiscovery {
    public static Stream<Path> getSerialDevices() {
        return Stream.of(SerialPortList.getPortNames())
                .map(Paths::get);
    }
}
