package io.freedriver.serial.api.connection;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * USB vendor/product id pair, independent of udev {@code /dev/serial/by-id} names.
 *
 * <p>Values are normalized to 4-digit lowercase hex (e.g. {@code 2341:0042}).
 */
public record UsbId(String vendor, String product) {
    private static final Pattern HEX = Pattern.compile("[0-9a-f]{1,4}");

    /** Arduino SA Mega 2560 R3 (CDC ACM). */
    public static final UsbId ARDUINO_SA_MEGA_2560 = UsbId.parse("2341:0042");

    /** arduino.org Mega 2560 R3 (CDC ACM). */
    public static final UsbId ARDUINO_ORG_MEGA_2560 = UsbId.parse("2a03:0042");

    public UsbId {
        vendor = normalize(vendor);
        product = normalize(product);
    }

    public static UsbId parse(String spec) {
        Objects.requireNonNull(spec, "spec");
        String[] parts = spec.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("USB id must be vendor:product, got: " + spec);
        }
        return new UsbId(parts[0], parts[1]);
    }

    public static List<UsbId> arduinoMega2560() {
        return List.of(ARDUINO_SA_MEGA_2560, ARDUINO_ORG_MEGA_2560);
    }

    public String dotted() {
        return vendor + ":" + product;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("USB id component required");
        }
        String hex = value.trim().toLowerCase(Locale.ROOT);
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        if (!HEX.matcher(hex).matches()) {
            throw new IllegalArgumentException("Invalid USB id component: " + value);
        }
        return String.format("%4s", hex).replace(' ', '0');
    }
}
