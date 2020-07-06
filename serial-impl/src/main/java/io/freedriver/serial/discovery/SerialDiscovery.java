package io.freedriver.serial.discovery;

import jssc.SerialPortList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class SerialDiscovery {
    public static Stream<Path> getSerialDevices() {
        return Stream.of(SerialPortList.getPortNames())
                .map(Paths::get);
    }
}
