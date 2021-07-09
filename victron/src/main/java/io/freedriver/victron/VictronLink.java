package io.freedriver.victron;

import io.freedriver.serial.api.params.BaudRates;
import io.freedriver.serial.api.params.SerialParams;
import io.freedriver.serial.stream.JSSCPort;
import io.freedriver.serial.stream.JSSCSerialStream;
import io.freedriver.serial.stream.api.PortReference;
import io.freedriver.serial.stream.api.SerialStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class VictronLink {
    private static final Logger LOGGER = Logger.getLogger(VictronLink.class.getName());
    private static final Path BY_ID_PATH = Paths.get("/dev/serial/by-id/");

    public static Stream<SerialStream> allSerialStreams() throws IOException {
        return allSerialStreams(p -> true);
    }

    public static Stream<SerialStream> allSerialStreams(Predicate<Path> pathFilter) throws IOException {
        return Files.isDirectory(BY_ID_PATH) ? Files.list(BY_ID_PATH)
                .filter(Files::isSymbolicLink)
                .filter(pathFilter)
                .map(PortReference::auto)
                .map(portReference -> JSSCPort.get(portReference, new SerialParams().setBaudRate(BaudRates.BAUDRATE_19200)))
                .map(JSSCSerialStream::new)
                :
                Stream.empty();
    }
}
