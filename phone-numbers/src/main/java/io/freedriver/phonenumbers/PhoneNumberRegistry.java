package io.freedriver.phonenumbers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

/** ServiceLoader index for {@link PhoneNumber}. Package-private so the cache is not API. */
final class PhoneNumberRegistry {
    private static final Map<String, Class<? extends PhoneNumber>> TYPES = load();

    private PhoneNumberRegistry() {}

    static Map<String, Class<? extends PhoneNumber>> types() {
        return TYPES;
    }

    // Defining loader, not the thread context loader, same as the other freedriver SPIs.
    private static Map<String, Class<? extends PhoneNumber>> load() {
        ServiceLoader<PhoneNumber> loader =
                ServiceLoader.load(PhoneNumber.class, PhoneNumber.class.getClassLoader());
        Map<String, Class<? extends PhoneNumber>> types = new LinkedHashMap<>();
        for (ServiceLoader.Provider<PhoneNumber> provider : loader.stream().toList()) {
            Class<?> raw = provider.type();
            if (!PhoneNumber.class.isAssignableFrom(raw)) {
                throw new IllegalStateException(raw.getName() + " does not implement PhoneNumber");
            }
            String logical = PhoneNumber.typeName(raw);
            if (logical.equals(raw.getName())) {
                throw new IllegalStateException(
                        raw.getName() + " @PhoneNumber.Type must be a logical name, not a class name");
            }
            Class<?> previous = types.putIfAbsent(logical, raw.asSubclass(PhoneNumber.class));
            if (previous != null) {
                throw new IllegalStateException("duplicate PhoneNumber type '" + logical + "' from "
                        + previous.getName() + " and " + raw.getName());
            }
        }
        return Map.copyOf(types);
    }
}
