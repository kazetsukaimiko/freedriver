package io.freedriver.jsonlink.config.v3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import com.fasterxml.jackson.core.type.TypeReference;
import io.freedriver.base.util.file.DirectoryProviders;
import io.freedriver.base.util.file.PathProvider;
import io.freedriver.jsonlink.config.ConfigMapper;
import lombok.Builder;

@Builder(toBuilder = true)
public record Mappings(
        Map<ApplianceDescriptor, Appliance> appliances,
        Map<EventDescriptor, ControlEvent> controlEvents,
        Map<EventDescriptor, List<ToggleAction>> toggleActions) {

    private static final String JSONLINK = "jsonlink";
    private static final String MAPPINGS = "mappings";
    private static final String APPLIANCES = "appliances";
    private static final String CONTROLS = "controls";

    public Mappings {
        appliances = appliances == null ? Map.of() : Map.copyOf(appliances);
        controlEvents = controlEvents == null ? Map.of() : Map.copyOf(controlEvents);
        toggleActions = toggleActions == null ? Map.of() : Map.copyOf(toggleActions);
    }

    public static Mappings load() throws IOException {
        Map<EventDescriptor, List<ToggleAction>> toggleActions = new LinkedHashMap<>();
        DirectoryProviders.CONFIG.getProvider()
                .subdir(JSONLINK)
                .subdir(MAPPINGS)
                .createIfNeeded()
                .files(path -> path.endsWith(".json"))
                .map(PathProvider::get)
                .forEach(controlFile -> loadJson(controlFile, new TypeReference<List<ToggleAction>>() {})
                        .ifPresent(toggleAction -> toggleActions.put(
                                descriptor(EventDescriptor::new, controlFile),
                                toggleAction
                        )));

        Map<ApplianceDescriptor, Appliance> appliances = new LinkedHashMap<>();
        DirectoryProviders.CONFIG.getProvider()
                .subdir(JSONLINK)
                .subdir(MAPPINGS)
                .subdir(APPLIANCES)
                .createIfNeeded()
                .files(path -> path.endsWith(".json"))
                .map(PathProvider::get)
                .forEach(applianceFile -> loadJson(applianceFile, Appliance.class)
                        .ifPresent(appliance -> appliances.put(
                                descriptor(ApplianceDescriptor::new, applianceFile),
                                appliance
                        )));

        Map<EventDescriptor, ControlEvent> controlEvents = new LinkedHashMap<>();
        DirectoryProviders.CONFIG.getProvider()
                .subdir(JSONLINK)
                .subdir(MAPPINGS)
                .subdir(CONTROLS)
                .createIfNeeded()
                .files(path -> path.endsWith(".json"))
                .map(PathProvider::get)
                .forEach(controlFile -> loadJson(controlFile, ControlEvent.class)
                        .ifPresent(control -> controlEvents.put(
                                descriptor(EventDescriptor::new, controlFile),
                                control
                        )));

        return new Mappings(appliances, controlEvents, toggleActions);
    }

    private static <D extends Descriptor> D descriptor(Function<String, D> constructor, Path absolutePath) {
        return constructor.apply(absolutePath.getFileName().toString());
    }

    private static <T> Optional<T> loadJson(Path path, TypeReference<T> klazz) {
        try {
            return Optional.of(ConfigMapper.MAPPER.readValue(path.toFile(), klazz));
        } catch (IOException e) {
            ConfigMapper.LOGGER.log(Level.WARNING, "Couldn't load JSON:", e);
            return Optional.empty();
        }
    }

    private static <T> Optional<T> loadJson(Path path, Class<T> klazz) {
        try {
            return Optional.of(ConfigMapper.MAPPER.readValue(path.toFile(), klazz));
        } catch (IOException e) {
            ConfigMapper.LOGGER.log(Level.WARNING, "Couldn't load JSON:", e);
            return Optional.empty();
        }
    }
}
