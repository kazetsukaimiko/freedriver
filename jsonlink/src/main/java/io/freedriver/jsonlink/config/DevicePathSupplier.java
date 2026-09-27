package io.freedriver.jsonlink.config;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * How to find serial device node(s). Deduced from JSON shape in {@code connectors.json}:
 *
 * <pre>
 * [
 *   { "vendorId": "2341", "deviceId": "0042" },
 *   { "path": "/dev/ttyACM0" }
 * ]
 * </pre>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(DeviceByVendorAndDeviceId.class),
        @JsonSubTypes.Type(DeviceByPath.class)
})
public sealed interface DevicePathSupplier extends Supplier<List<Path>>
        permits DeviceByVendorAndDeviceId, DeviceByPath {}
