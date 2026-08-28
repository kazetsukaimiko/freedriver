package io.freedriver.jsonlink.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class DevicePathSupplierTest {
    private static final TypeReference<List<DevicePathSupplier>> LIST = new TypeReference<>() {};

    @Test
    void deductsVendorDeviceAndPath() throws JsonProcessingException {
        String json =
                """
                [
                  { "vendorId": "2341", "deviceId": "0042" },
                  { "path": "/dev/ttyACM0" }
                ]
                """;

        List<DevicePathSupplier> devices = ConfigMapper.MAPPER.readValue(json, LIST);

        assertEquals(2, devices.size());
        DeviceByVendorAndDeviceId usb = assertInstanceOf(DeviceByVendorAndDeviceId.class, devices.get(0));
        assertEquals("2341", usb.vendorId());
        assertEquals("0042", usb.deviceId());
        DeviceByPath byPath = assertInstanceOf(DeviceByPath.class, devices.get(1));
        assertEquals("/dev/ttyACM0", byPath.path());
        assertEquals(List.of(Path.of("/dev/ttyACM0")), byPath.get());
    }

    @Test
    void roundTripsWithoutTypeProperty() throws JsonProcessingException {
        ObjectMapper mapper = ConfigMapper.MAPPER;
        List<DevicePathSupplier> original = List.of(
                new DeviceByVendorAndDeviceId("2a03", "0042"), new DeviceByPath("/dev/ttyUSB0"));
        String json = mapper.writeValueAsString(original);
        assertTrue(json.contains("vendorId"));
        assertTrue(json.contains("path"));
        assertTrue(!json.contains("@type"));

        List<DevicePathSupplier> back = mapper.readValue(json, LIST);
        assertEquals(original, back);
    }
}
