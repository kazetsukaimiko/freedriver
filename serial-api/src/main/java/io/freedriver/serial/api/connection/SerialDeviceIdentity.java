package io.freedriver.serial.api.connection;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Stable identity for a USB serial device, keyed on its {@code /dev/serial/by-id/} symlink.
 * The underlying {@code /dev/ttyACM*} path may change across reconnects; this identity does not.
 */
public final class SerialDeviceIdentity {
    private final Path byIdPath;
    private final String linkName;
    private final Optional<String> bus;
    private final Optional<String> vendor;
    private final Optional<String> product;
    private final Optional<String> serial;

    public SerialDeviceIdentity(
            Path byIdPath,
            String linkName,
            Optional<String> bus,
            Optional<String> vendor,
            Optional<String> product,
            Optional<String> serial) {
        this.byIdPath = Objects.requireNonNull(byIdPath, "byIdPath");
        this.linkName = Objects.requireNonNull(linkName, "linkName");
        this.bus = Objects.requireNonNull(bus, "bus");
        this.vendor = Objects.requireNonNull(vendor, "vendor");
        this.product = Objects.requireNonNull(product, "product");
        this.serial = Objects.requireNonNull(serial, "serial");
    }

    public static SerialDeviceIdentity of(Path byIdPath) {
        Path normalized = byIdPath.toAbsolutePath().normalize();
        String linkName = normalized.getFileName().toString();
        return parse(normalized, linkName);
    }

    static SerialDeviceIdentity parse(Path byIdPath, String linkName) {
        if (linkName.startsWith("usb-")) {
            return parseUsb(byIdPath, linkName);
        }
        if (linkName.startsWith("pci-")) {
            return new SerialDeviceIdentity(
                    byIdPath,
                    linkName,
                    Optional.of("pci"),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty());
        }
        return new SerialDeviceIdentity(
                byIdPath,
                linkName,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
    }

    /**
     * Parses a Linux udev {@code /dev/serial/by-id/} symlink name.
     *
     * <p>Expected format: {@code usb-<vendor>_<product>_<serial>-if<iface>-port<port>}
     * <br>Example: {@code usb-VictronEnergy_BV_VE_Direct_cable_VE6HQ20-if00-port0}
     * <br>Example: {@code usb-1a86_7523_USB_Serial-if00-port0}
     *
     * <p>The portion before {@code -if} is split on {@code _} into vendor, product, and
     * serial (product/serial may themselves contain underscores).
     */
    private static SerialDeviceIdentity parseUsb(Path byIdPath, String linkName) {
        String body = linkName.substring("usb-".length());
        int ifaceIndex = body.indexOf("-if");
        String identityPart = ifaceIndex >= 0 ? body.substring(0, ifaceIndex) : body;
        String[] parts = identityPart.split("_", 3);
        Optional<String> vendor = parts.length > 0 ? optionalNonBlank(parts[0]) : Optional.empty();
        Optional<String> product = parts.length > 1 ? optionalNonBlank(parts[1]) : Optional.empty();
        Optional<String> serial = parts.length > 2 ? optionalNonBlank(parts[2]) : Optional.empty();
        return new SerialDeviceIdentity(
                byIdPath,
                linkName,
                Optional.of("usb"),
                vendor,
                product,
                serial);
    }

    private static Optional<String> optionalNonBlank(String value) {
        return value == null || value.isBlank() ? Optional.empty() : Optional.of(value);
    }

    public Path byIdPath() {
        return byIdPath;
    }

    public String linkName() {
        return linkName;
    }

    public Optional<String> bus() {
        return bus;
    }

    public Optional<String> vendor() {
        return vendor;
    }

    public Optional<String> product() {
        return product;
    }

    public Optional<String> serial() {
        return serial;
    }

    public boolean matchesLinkNamePrefix(String prefix) {
        return linkName.startsWith(prefix);
    }

    public boolean linkNameContains(String fragment) {
        return linkName.contains(fragment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SerialDeviceIdentity that = (SerialDeviceIdentity) o;
        return Objects.equals(byIdPath, that.byIdPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(byIdPath);
    }

    @Override
    public String toString() {
        return "SerialDeviceIdentity{" + byIdPath + "}";
    }
}