package io.freedriver.serial.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void findsRenumberedTtyAcmByVendorProduct() throws IOException {
        Path sysClassTty = tmp.resolve("sys/class/tty");
        Path devRoot = tmp.resolve("dev");
        Files.createDirectories(devRoot);
        createUsbTty(sysClassTty, devRoot, "ttyACM0", "1a86", "7523");
        createUsbTty(sysClassTty, devRoot, "ttyACM1", "2341", "0042");

        List<Path> found = LinuxUsbSysfsDiscovery.discover(
                sysClassTty, devRoot, List.of(UsbId.ARDUINO_SA_MEGA_2560));

        assertEquals(List.of(devRoot.resolve("ttyACM1")), found);
    }

    @Test
    void discoversBoardWhenSerialByIdIsMissing() throws IOException {
        Path sysClassTty = tmp.resolve("sys/class/tty");
        Path devRoot = tmp.resolve("dev");
        Files.createDirectories(devRoot);
        createUsbTty(sysClassTty, devRoot, "ttyACM0", "2341", "0042");
        Path byId = devRoot.resolve("serial/by-id");

        assertFalse(Files.exists(byId));
        assertEquals(
                List.of(devRoot.resolve("ttyACM0")),
                LinuxUsbSysfsDiscovery.discover(sysClassTty, devRoot, List.of(UsbId.ARDUINO_SA_MEGA_2560)));
    }

    @Test
    void discoversBoardWhenSerialByIdLinkIsStale() throws IOException {
        Path sysClassTty = tmp.resolve("sys/class/tty");
        Path devRoot = tmp.resolve("dev");
        Files.createDirectories(devRoot);
        createUsbTty(sysClassTty, devRoot, "ttyACM1", "2341", "0042");
        createUsbTty(sysClassTty, devRoot, "ttyUSB0", "1a86", "7523");
        Path byId = devRoot.resolve("serial/by-id");
        Files.createDirectories(byId);
        Path dangling = byId.resolve("usb-Arduino_Mega_2560");
        Path wrong = byId.resolve("usb-Arduino_wrong_tty");
        Files.createSymbolicLink(dangling, devRoot.resolve("ttyACM0"));
        Files.createSymbolicLink(wrong, devRoot.resolve("ttyUSB0"));

        assertTrue(Files.isSymbolicLink(dangling));
        assertFalse(Files.exists(dangling));
        assertEquals(devRoot.resolve("ttyUSB0"), Files.readSymbolicLink(wrong));
        assertEquals(
                List.of(devRoot.resolve("ttyACM1")),
                LinuxUsbSysfsDiscovery.discover(sysClassTty, devRoot, List.of(UsbId.ARDUINO_SA_MEGA_2560)));
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
