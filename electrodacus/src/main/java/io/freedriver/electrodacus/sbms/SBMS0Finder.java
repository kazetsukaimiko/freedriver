package io.freedriver.electrodacus.sbms;

import io.freedriver.serial.JSSCSerialResource;
import io.freedriver.serial.api.SerialPortResourceSupplier;
import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.params.BaudRate;
import io.freedriver.serial.api.params.BaudRates;
import io.freedriver.serial.api.params.SerialParams;
import io.freedriver.serial.stream.api.SerialEntityStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SBMS0Finder {
    private static final Logger LOGGER = Logger.getLogger(SBMS0Finder.class.getName());
    private static final String LINUX_SERIAL_BY_ID_PATH = "/dev/serial/by-id/";
    private static final String SBMS0_MANUF_CODE = "1a86";

    public static Stream<Path> findSBMS0Units() {
        if (Files.isDirectory(Paths.get(LINUX_SERIAL_BY_ID_PATH))) {
            try (Stream<Path> serialDevices = Files.list(Paths.get(LINUX_SERIAL_BY_ID_PATH))){
                List<Path> deviceList = serialDevices.collect(Collectors.toList());
                return deviceList
                        .stream()
                        .map(Path::toAbsolutePath)
                        .filter(SBMS0Finder::match);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Couldn't scan " + LINUX_SERIAL_BY_ID_PATH, e);
            }
        }
        return Stream.empty();
    }

    public static Stream<SBMSMessage> findFirstSBMS0() {
        return SBMS0Finder.findSBMS0Units()
                .findFirst()
                .map(SBMS0Finder::open)
                .orElseGet(Stream::empty);
    }

    public static Stream<SBMSMessage> open(Path path) {
        return open(path, new SerialParams().setBaudRate(BaudRates.BAUDRATE_115200));
    }

    public static Stream<SBMSMessage> open(String path, BaudRate baudRate) {
        return open(Paths.get(path), new SerialParams().setBaudRate(baudRate));
    }

    public static Stream<SBMSMessage> open(Path path, SerialParams serialParams) {
        return new SerialEntityStream<>(
                new JSSCSerialResource(path, serialParams),
                new SBMSMessageAccumulator(path))
                .stream();
    }

    public static boolean match(Path path) {
        return path.toString().contains(SBMS0_MANUF_CODE);
    }

    public static boolean noMatch(Path path) {
        return !match(path);
    }
}
