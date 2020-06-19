package io.freedriver.serial;

import javax.usb.UsbDevice;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class USBDeviceHelper implements DevicesHelper<USBSerialDevice> {

    @Override
    public List<USBSerialDevice> getDevices() {
        return allUSBDevices()
                .peek(usbDevice -> {

                })
                .map(USBSerialDevice::new)
                .collect(Collectors.toList());


    }


    @SuppressWarnings("unchecked")
    public Stream<UsbDevice> allUSBDevices() {
        try {
            return ((Stream<Object>) UsbHostManager.getUsbServices()
                    .getRootUsbHub()
                    .getAttachedUsbDevices()
                    .stream())
                    .map(UsbDevice.class::cast);
        } catch (UsbException e) {
            throw new SerialResourceException("Unable to read USB Devices", e);
        }
    }
}
