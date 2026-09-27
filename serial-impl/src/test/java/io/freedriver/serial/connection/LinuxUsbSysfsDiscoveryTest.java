package io.freedriver.serial.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.freedriver.serial.api.connection.UsbId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LinuxUsbSysfsDiscoveryTest {
    @TempDir
    Path tmp;

    @Test
    void findsTtyAcmByConfiguredVidPid() throws IOException {
        Path sysClassTty = tmp.resolve("sys/class/tty");
        Path devRoot = tmp.resolve("dev");
        Files.createDirectories(devRoot);
        createUsbTty(sysClassTty, devRoot, "ttyACM0", "2341", "0042");
        createUsbTty(sysClassTty, devRoot, "ttyUSB0", "1a86", "7523");

        List<Path> found = LinuxUsbSysfsDiscovery.discover(
                sysClassTty, devRoot, List.of(UsbId.ARDUINO_SA_MEGA_2560));

        assertEquals(List.of(devRoot.resolve("ttyACM0")), found);
    }

    @Test
    void emptyWantedIdsReturnsNothing() throws IOException {
        Path sysClassTty = tmp.resolve("sys/class/tty");
        Path devRoot = tmp.resolve("dev");
        Files.createDirectories(devRoot);
        createUsbTty(sysClassTty, devRoot, "ttyACM0", "2341", "0042");

        assertTrue(LinuxUsbSysfsDiscovery.discover(sysClassTty, devRoot, List.of()).isEmpty());
    }

    @Test
    void missingSysfsReturnsEmpty() {
        assertTrue(LinuxUsbSysfsDiscovery.discover(
                        tmp.resolve("missing"), tmp.resolve("dev"), UsbId.arduinoMega2560())
                .isEmpty());
    }

    @Test
    void collectedListSurvivesDirectoryClose() throws IOException {
        Path sysClassTty = tmp.resolve("sys/class/tty");
        Path devRoot = tmp.resolve("dev");
        Files.createDirectories(devRoot);
        createUsbTty(sysClassTty, devRoot, "ttyACM0", "2a03", "0042");

        List<Path> found = LinuxUsbSysfsDiscovery.discover(
                sysClassTty, devRoot, List.of(UsbId.ARDUINO_ORG_MEGA_2560));
        Files.deleteIfExists(sysClassTty.resolve("ttyACM0").resolve("device"));

        assertEquals(1, found.size());
        assertEquals(devRoot.resolve("ttyACM0"), found.get(0));
    }

    private static void createUsbTty(
            Path sysClassTty, Path devRoot, String ttyName, String vendor, String product)
            throws IOException {
        Path usbDevice = tmpUsbDevice(sysClassTty, ttyName, vendor, product);
        Path ttyDir = sysClassTty.resolve(ttyName);
        Files.createDirectories(ttyDir);
        Files.createSymbolicLink(ttyDir.resolve("device"), usbDevice.resolve(ttyName + "-iface"));
        Files.createFile(devRoot.resolve(ttyName));
    }

    private static Path tmpUsbDevice(Path sysClassTty, String ttyName, String vendor, String product)
            throws IOException {
        Path usbDevice = sysClassTty.getParent().getParent().resolve("devices").resolve(ttyName + "-usb");
        Path iface = usbDevice.resolve(ttyName + "-iface");
        Files.createDirectories(iface);
        Files.writeString(usbDevice.resolve("idVendor"), vendor + "\n");
        Files.writeString(usbDevice.resolve("idProduct"), product + "\n");
        return usbDevice;
    }
}
