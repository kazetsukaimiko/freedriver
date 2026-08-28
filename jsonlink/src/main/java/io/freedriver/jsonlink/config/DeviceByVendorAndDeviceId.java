package io.freedriver.jsonlink.config;

import java.nio.file.Path;
import java.util.List;

import io.freedriver.serial.api.connection.UsbId;
import io.freedriver.serial.connection.LinuxUsbSysfsDiscovery;

/**
 * Resolves {@code /dev/ttyACM*} / {@code /dev/ttyUSB*} by USB vendor/product id via sysfs.
 */
public record DeviceByVendorAndDeviceId(String vendorId, String deviceId) implements DevicePathSupplier {
    public DeviceByVendorAndDeviceId {
        UsbId id = new UsbId(vendorId, deviceId);
        vendorId = id.vendor();
        deviceId = id.product();
    }

    public UsbId usbId() {
        return new UsbId(vendorId, deviceId);
    }

    @Override
    public List<Path> get() {
        return LinuxUsbSysfsDiscovery.discover(List.of(usbId()));
    }
}
