package io.freedriver.jsonlink.config;

import static io.freedriver.jsonlink.config.ConfigMapper.MAPPER;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

/**
 * Serial devices to open, stored as a JSON array in {@code ~/.config/jsonlink/connectors.json}.
 */
@Builder(toBuilder = true)
public record ConnectorConfig(List<DevicePathSupplier> devices) {
    private static final Path CONFIG_PATH = Path.of(System.getProperty("user.home"), ".config/jsonlink");
    private static final Path CONFIG_FILE_PATH = CONFIG_PATH.resolve("connectors.json");
    private static final Logger LOGGER = Logger.getLogger(ConnectorConfig.class.getName());
    private static final TypeReference<List<DevicePathSupplier>> DEVICE_LIST = new TypeReference<>() {};
    private static volatile ConnectorConfig cached;
    private static volatile long cachedMtime = Long.MIN_VALUE;

    public ConnectorConfig {
        devices = devices == null ? defaultDevices() : List.copyOf(devices);
    }

    public static ConnectorConfig empty() {
        return new ConnectorConfig(defaultDevices());
    }

    public static List<DevicePathSupplier> defaultDevices() {
        return List.of(
                new DeviceByVendorAndDeviceId("2341", "0042"),
                new DeviceByVendorAndDeviceId("2a03", "0042"));
    }

    public static ConnectorConfig load() {
        try {
            long mtime = Files.isRegularFile(CONFIG_FILE_PATH)
                    ? Files.getLastModifiedTime(CONFIG_FILE_PATH).toMillis()
                    : -1L;
            ConnectorConfig current = cached;
            if (current != null && mtime == cachedMtime) {
                return current;
            }
            ConnectorConfig loaded = loadOrCreate();
            cached = loaded;
            cachedMtime = Files.isRegularFile(CONFIG_FILE_PATH)
                    ? Files.getLastModifiedTime(CONFIG_FILE_PATH).toMillis()
                    : mtime;
            return loaded;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Couldn't load connectors.json, using defaults", e);
            ConnectorConfig fallback = empty();
            logSuppliers("defaults after load failure", fallback);
            return fallback;
        }
    }

    private static ConnectorConfig loadOrCreate() throws IOException {
        LOGGER.info("Loading connectors.json from " + CONFIG_FILE_PATH);
        if (!Files.isDirectory(CONFIG_PATH)) {
            Files.createDirectories(CONFIG_PATH);
        }
        if (!Files.isRegularFile(CONFIG_FILE_PATH)) {
            LOGGER.info("connectors.json missing; writing Arduino Mega USB id defaults");
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(CONFIG_FILE_PATH.toFile(), defaultDevices());
            ConnectorConfig created = empty();
            logSuppliers("written defaults", created);
            return created;
        }
        JsonNode node = MAPPER.readTree(CONFIG_FILE_PATH.toFile());
        if (!node.isArray()) {
            LOGGER.warning("connectors.json is not a device list; using Arduino Mega USB id defaults");
            ConnectorConfig fallback = empty();
            logSuppliers("defaults after invalid file", fallback);
            return fallback;
        }
        ConnectorConfig loaded = new ConnectorConfig(MAPPER.convertValue(node, DEVICE_LIST));
        logSuppliers(CONFIG_FILE_PATH.toString(), loaded);
        return loaded;
    }

    private static void logSuppliers(String source, ConnectorConfig config) {
        List<DevicePathSupplier> devices = config.devices();
        LOGGER.info("Loaded " + devices.size() + " DevicePathSuppliers from " + source + ": " + devices);
    }
}
