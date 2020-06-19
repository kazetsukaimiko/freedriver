package io.freedriver.victron;

import io.freedriver.serial.SerialListener;
import io.freedriver.serial.SerialPortResourceSupplier;
import io.freedriver.serial.SerialReader;
import io.freedriver.serial.SerialResource;
import jssc.SerialPortList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a VE.Direct Device. A VE.Direct device is NOT the connected device, eg. an MPPT Charge controller.
 * A VE.Direct device is the link to the Victron Product. If you want to detect the product, use:
 * {attemptToGetProduct}
 */
public class VEDirectReader extends SerialReader {
    private static final Logger LOGGER = Logger.getLogger(VEDirectReader.class.getName());
    private static final Path BY_ID_PATH = Paths.get("/dev/serial/by-id/");

    private final VEDirectDeviceType type;

    /**
     * Don't construct these yourself. Use the static methods below.
     * @param type
     * @param resourceSupplier
     */
    public VEDirectReader(VEDirectDeviceType type, Supplier<SerialResource> resourceSupplier) {
        super(resourceSupplier);
        this.type = type;
    }

    public VEDirectDeviceType getType() {
        return type;
    }



    private static <X> void readUntilChecksum(SerialListener<X> listener) {
        while (VEDirectColumnValue.fromSerial(listener.nextLine())
            .map(value -> value.getColumn() != VEDirectColumn.CHECKSUM)
            .orElse(true) && listener.hasNext()) {
        }
    }


    /**
     * Reads VEDirectColumnValues from the VE.Direct device. While this usually maps 1:1 Line:VEDirectColumnValue,
     * any unsupported column values are ignored.
     * @return A Stream of VEDirectColumnValues.
     */
    public Stream<VEDirectColumnValue> readAsColumns() {
        return openStream(VEDirectColumnValue.accumulator());
    }
    /**
     * Reads multiple VEDirectColumns, accumulating them into a VEDirectMessage. The Checksum Column is the delimiter.
     * @return A Stream of VEDirectMessages.
     */
    public Stream<VEDirectMessage> readAsMessages() {
        return openStream(VEDirectMessage.accumulator());
    }

    /**
     * Opens a VEDirectMessage stream and gets the first message.
     * @return An Optional VEDirectMessage.
     */
    public Optional<VEDirectMessage> readOneMessage() {
        try (Stream<VEDirectMessage> messageStream = readAsMessages()) {
            return messageStream.findFirst();
        }
    }

    /**
     * Convenience Method to try and get the VictronProduct connected to the VEDirectDevice. It accomplishes this by
     * getting a single message.
     * @return An Optional VictronProduct.
     */
    public Optional<VictronDevice> attemptToGetProduct() {
        return readOneMessage()
                .flatMap(VictronDevice::of);
    }


    @Override
    public String toString() {
        return "VEDirectDevice{" +
                "type=" + type +
                '}';
    }

    public static Stream<VEDirectReader> allVEDirectDevices() throws IOException {
        return allVEDirectDevices(p -> true);
    }

    /**
     * Scan for all Victron-related Serial devices, using Linux /dev/serial to qualify.
     * @return A Stream of VEDirectDevices found. If this doesn't find your devices, see {VEDirectDeviceType}.
     * @throws IOException
     */
    public static Stream<VEDirectReader> allVEDirectDevices(Predicate<Path> pathFilter) throws IOException {
        Set<Path> serialPorts = allSerialPorts()
                .filter(pathFilter)
                .collect(Collectors.toSet());
        return Files.isDirectory(BY_ID_PATH) ? Files.list(BY_ID_PATH)
                .filter(Files::isSymbolicLink)
                .map(symlink -> VEDirectReader.toVEDirectDevice(serialPorts, symlink))
                .flatMap(Optional::stream)
                :
                Stream.empty();
    }


    /**
     * Using jssc, get all known Serial Ports. Internal/private.
     * @return A Stream of Paths to known Serial ports.
     */
    private static Stream<Path> allSerialPorts() {
        return Stream.of(SerialPortList.getPortNames())
                .map(Paths::get);
    }

    /**
     * Try to match a known jssc SerialPort to a recognizable VEDirectDevice. Internal/private.
     * @param serialPorts Paths of ports to consider.
     * @param deviceId The deviceId to qualify.
     * @return
     */
    private static Optional<VEDirectReader> toVEDirectDevice(Set<Path> serialPorts, Path deviceId) {
        return Optional.of(deviceId)
                .flatMap(VEDirectDeviceType::typeOfDevice)
                .filter(type -> serialPorts.contains(readSymbolicLink(deviceId)))
                .map(type -> new VEDirectReader(type, new SerialPortResourceSupplier(deviceId)));
    }

    /**
     * Convenience method to read a symlink without checked exceptions.
     * @param symlink Path the symlink points to.
     * @return
     */
    public static Path readSymbolicLink(Path symlink) {
        try {
            return symlink.toRealPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VEDirectReader that = (VEDirectReader) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}
