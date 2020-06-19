package io.freedriver.serial;

import javax.usb.UsbDevice;
import javax.usb.UsbException;
import java.io.UnsupportedEncodingException;

public class USBSerialDevice implements SerialDevice {
    private final String manufacturerString;

    public USBSerialDevice(String manufacturerString) {
        this.manufacturerString = manufacturerString;
    }

    public USBSerialDevice(UsbDevice device) {
        try {
            this.manufacturerString = device.getManufacturerString();
        } catch (UsbException | UnsupportedEncodingException e) {
            throw new SerialResourceException("Unable to read USB Device info", e);
        }
    }

    public String getManufacturerString() {
        return manufacturerString;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.USB;
    }


}
