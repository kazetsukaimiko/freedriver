package io.freedriver.serial.connection;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.freedriver.serial.api.connection.UsbId;

/**
 * Discovers USB serial ports by walking sysfs from {@code /dev/ttyACM*} / {@code /dev/ttyUSB*}
 * to {@code idVendor}/{@code idProduct}. Does not depend on {@code /dev/serial/by-id}.
 */
public final class LinuxUsbSysfsDiscovery {
    private static final Path SYS_CLASS_TTY = Path.of("/sys/class/tty");
    private static final Path DEV_ROOT = Path.of("/dev");
    private static final Pattern TTY_NAME = Pattern.compile("tty(ACM|USB)\\d+");
    private static final int MAX_SYSFS_WALK = 16;
    private static final Logger LOGGER = Logger.getLogger(LinuxUsbSysfsDiscovery.class.getName());

    private LinuxUsbSysfsDiscovery() {
    }

    public static boolean isSupported() {
        return Files.isDirectory(SYS_CLASS_TTY);
    }

    public static List<Path> discover(Collection<UsbId> usbIds) {
        return discover(SYS_CLASS_TTY, DEV_ROOT, usbIds);
    }

    public static List<Path> discover(Path sysClassTty, Path devRoot, Collection<UsbId> usbIds) {
        if (usbIds == null || usbIds.isEmpty()) {
            LOGGER.fine("USB sysfs discovery skipped: no vendor/device ids configured");
            return List.of();
        }
        if (!Files.isDirectory(sysClassTty)) {
            LOGGER.warning("USB sysfs discovery skipped: " + sysClassTty + " is not a directory");
            return List.of();
        }
        Set<UsbId> wanted = Set.copyOf(usbIds);
        LOGGER.fine("Scanning " + sysClassTty + " for USB ids " + wanted);
        try (Stream<Path> entries = Files.list(sysClassTty)) {
            List<Path> found = entries
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> TTY_NAME.matcher(name).matches())
                    .filter(name -> Files.exists(devRoot.resolve(name)))
                    .filter(name -> matchesUsbId(sysClassTty.resolve(name), name, wanted))
                    .map(devRoot::resolve)
                    .toList();
            LOGGER.fine("USB ids " + wanted + " matched " + found);
            return found;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to scan " + sysClassTty, e);
        }
    }

    private static boolean matchesUsbId(Path sysTtyDir, String ttyName, Set<UsbId> wanted) {
        Optional<UsbId> id = usbIdOf(sysTtyDir);
        if (id.isEmpty()) {
            LOGGER.fine("No USB vendor/device id for " + ttyName);
            return false;
        }
        boolean match = wanted.contains(id.get());
        LOGGER.fine(ttyName + " USB id " + id.get().dotted() + (match ? " matched" : " ignored"));
        return match;
    }

    static Optional<UsbId> usbIdOf(Path sysTtyDir) {
        Path deviceLink = sysTtyDir.resolve("device");
        if (!Files.exists(deviceLink)) {
            return Optional.empty();
        }
        try {
            Path current = deviceLink.toRealPath();
            for (int i = 0; i < MAX_SYSFS_WALK && current != null; i++) {
                Path vendor = current.resolve("idVendor");
                Path product = current.resolve("idProduct");
                if (Files.isRegularFile(vendor) && Files.isRegularFile(product)) {
                    try {
                        return Optional.of(new UsbId(
                                Files.readString(vendor).trim(),
                                Files.readString(product).trim()));
                    } catch (IllegalArgumentException e) {
                        return Optional.empty();
                    }
                }
                current = current.getParent();
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}
