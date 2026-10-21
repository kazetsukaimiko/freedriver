package io.freedriver.jsonlink.config.v2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

/** ServiceLoader index for {@link MappingsAction}. Package-private so the cache is not API. */
final class MappingsActionRegistry {
    private static final Map<String, Class<? extends MappingsAction>> TYPES = load();

    private MappingsActionRegistry() {}

    static Map<String, Class<? extends MappingsAction>> types() {
        return TYPES;
    }

    // Defining loader, not the thread context loader, same as the other freedriver SPIs.
    private static Map<String, Class<? extends MappingsAction>> load() {
        ServiceLoader<MappingsAction> loader =
                ServiceLoader.load(MappingsAction.class, MappingsAction.class.getClassLoader());
        Map<String, Class<? extends MappingsAction>> types = new LinkedHashMap<>();
        for (ServiceLoader.Provider<MappingsAction> provider : loader.stream().toList()) {
            Class<?> raw = provider.type();
            if (!MappingsAction.class.isAssignableFrom(raw)) {
                throw new IllegalStateException(raw.getName() + " does not implement MappingsAction");
            }
            String logical = MappingsAction.typeName(raw);
            if (logical.equals(raw.getName())) {
                throw new IllegalStateException(
                        raw.getName() + " @MappingsAction.Type must be a logical name, not a class name");
            }
            Class<?> previous = types.putIfAbsent(logical, raw.asSubclass(MappingsAction.class));
            if (previous != null) {
                throw new IllegalStateException("duplicate MappingsAction type '" + logical + "' from "
                        + previous.getName() + " and " + raw.getName());
            }
        }
        return Map.copyOf(types);
    }
}
