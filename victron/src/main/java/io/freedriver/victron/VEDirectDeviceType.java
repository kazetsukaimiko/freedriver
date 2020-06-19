package io.freedriver.victron;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Ways to identify Victron Energy Devices. This class identifies potential Victron Energy devices to talk to. If
 * this class fails to find your device, add it here. First, look under /dev/serial/by-id/ to find something resembling
 * a link to your device. Then add an entry here to match it.
 *
 * Currently this code supports the VE.Direct USB cable.
 */
public enum VEDirectDeviceType {
    EMULATED_DEVICE(id -> "EMULATOR".equals(id.toString())),
    VE_DIRECT_USB_CABLE(id -> id.getFileName().toString()
            .startsWith("usb-VictronEnergy_BV_VE_Direct_cable"));

    private final Predicate<Path> matcher;

    VEDirectDeviceType(Predicate<Path> matcher) {
        this.matcher = matcher;
    }

    public Predicate<Path> getMatcher() {
        return matcher;
    }

    /**
     * Try to ascertain the VEDirectDeviceType from a given symlink. Currently this is done by the device id Linux
     * discovers.
     * @param symlink The path to the serial device.
     * @return An Optional VEDirectDeviceType, if matches.
     */
    public static Optional<VEDirectDeviceType> typeOfDevice(Path symlink) {
        return Stream.of(VEDirectDeviceType.values())
                .filter(type -> type.getMatcher().test(symlink))
                .findFirst();
    }
}
