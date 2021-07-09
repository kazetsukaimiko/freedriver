package io.freedriver.victron;

import io.freedriver.serial.JSSCSerialResource;
import io.freedriver.serial.SerialPortResourceSupplier;
import io.freedriver.serial.api.params.BaudRates;
import io.freedriver.serial.api.params.SerialParams;
import io.freedriver.serial.discovery.SerialDiscovery;
import io.freedriver.serial.stream.JSSCPort;
import io.freedriver.serial.stream.JSSCSerialStream;
import io.freedriver.serial.stream.api.Accumulator;
import io.freedriver.serial.stream.api.Accumulators;
import io.freedriver.serial.stream.api.BaseStreamListener;
import io.freedriver.serial.stream.api.PortReference;
import io.freedriver.serial.stream.api.SerialStream;
import io.freedriver.serial.stream.api.StreamListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a VE.Direct Device. A VE.Direct device is NOT the connected device, eg. an MPPT Charge controller.
 * A VE.Direct device is the link to the Victron Product. If you want to detect the product, use:
 * {attemptToGetProduct}
 */
public class VEDirectColumnValueStreamer extends BaseStreamListener<VEDirectColumnValue> implements StreamListener<VEDirectColumnValue> {
    private static final Logger LOGGER = Logger.getLogger(VEDirectColumnValueStreamer.class.getName());
    private static final Path BY_ID_PATH = Paths.get("/dev/serial/by-id/");

    public VEDirectColumnValueStreamer(SerialStream stream) {
        super(stream, new ColumnValueAccumulator());
    }

    public static Stream<VEDirectColumnValueStreamer> allColumnStreamers() throws IOException {
        return allColumnStreamers(p -> true);
    }

    public static Stream<VEDirectColumnValueStreamer> allColumnStreamers(Predicate<Path> pathFilter) throws IOException {
        return VictronLink.allSerialStreams(pathFilter)
                .map(VEDirectColumnValueStreamer::new);
    }



    public static Stream<SerialStream> allSerialStreamers(Predicate<Path> pathFilter) throws IOException {
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
