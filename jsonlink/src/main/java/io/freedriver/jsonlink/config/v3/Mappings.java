package io.freedriver.jsonlink.config.v3;

import com.fasterxml.jackson.core.type.TypeReference;
import io.freedriver.base.util.file.DirectoryProviders;
import io.freedriver.base.util.file.PathProvider;
import io.freedriver.jsonlink.Connector;
import io.freedriver.jsonlink.config.ConfigMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

public class Mappings {

    private static final String JSONLINK = "jsonlink";
    private static final String MAPPINGS = "mappings";
    private static final String APPLIANCES = "appliances";
    private static final String CONTROLS = "controls";

    private final Map<ApplianceDescriptor, Appliance> appliances =  new LinkedHashMap<>();
    private final Map<EventDescriptor, ControlEvent> controlEvents =  new LinkedHashMap<>();
    private final Map<EventDescriptor, List<ToggleAction>> toggleActions = new LinkedHashMap<>();

    public Map<ApplianceDescriptor, Appliance> getAppliances() {
        return appliances;
    }

    public Map<EventDescriptor, ControlEvent> getControlEvents() {
        return controlEvents;
    }

    public Map<EventDescriptor, List<ToggleAction>> getToggleActions() {
        return toggleActions;
    }

    public static Mappings load() throws IOException {
        Mappings mappings = new Mappings();
        DirectoryProviders.CONFIG.getProvider()
                .subdir(JSONLINK)
                .subdir(MAPPINGS)
                .createIfNeeded()
                .files(path -> path.endsWith(".json"))
                .map(PathProvider::get)
                .forEach(controlFile -> loadJson(controlFile, new TypeReference<List<ToggleAction>>() {})
                        .ifPresent(toggleAction -> mappings.getToggleActions().put(
                                descriptor(EventDescriptor::new, controlFile),
                                toggleAction
                        )));
        DirectoryProviders.CONFIG.getProvider()
                .subdir(JSONLINK)
                .subdir(MAPPINGS)
                .subdir(APPLIANCES)
                .createIfNeeded()
                .files(path -> path.endsWith(".json"))
                .map(PathProvider::get)
                .forEach(applianceFile -> loadJson(applianceFile, Appliance.class)
                        .ifPresent(appliance -> mappings.getAppliances().put(
                                descriptor(ApplianceDescriptor::new, applianceFile),
                                appliance
                        )));
        DirectoryProviders.CONFIG.getProvider()
                .subdir(JSONLINK)
                .subdir(MAPPINGS)
                .subdir(CONTROLS)
                .createIfNeeded()
                .files(path -> path.endsWith(".json"))
                .map(PathProvider::get)
                .forEach(controlFile -> loadJson(controlFile, ControlEvent.class)
                        .ifPresent(control -> mappings.getControlEvents().put(
                                descriptor(EventDescriptor::new, controlFile),
                                control
                        )));

        return mappings;
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
