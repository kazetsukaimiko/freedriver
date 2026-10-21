package io.freedriver.jsonlink.config.v2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.ServiceLoader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * One step in a board's {@code startupActions} list in {@code mappings_v2.json}.
 *
 * <p>Register an implementation by listing its class in
 * {@code META-INF/services/io.freedriver.jsonlink.config.v2.MappingsAction} and marking it with
 * {@link Type}. {@link ServiceLoader} requires a public no-arg constructor; discovery reads
 * {@link ServiceLoader.Provider#type()} and does not call that constructor. The JSON {@code type}
 * value is the logical name. It is never a class name, and Jackson default typing is not used.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(MappingsActionTypeIdResolver.class)
public interface MappingsAction {
    /**
     * Logical {@code type} value written in {@code mappings_v2.json}. Not a class name.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Type {
        String value();
    }

    /**
     * Logical name from {@link Type} on the runtime class.
     */
    @JsonIgnore
    default String type() {
        return typeName(getClass());
    }

    /**
     * Actions registered through {@link ServiceLoader}, keyed by logical {@link Type} name.
     */
    static Map<String, Class<? extends MappingsAction>> registeredTypes() {
        return MappingsActionRegistry.types();
    }

    /**
     * @throws IllegalStateException when {@code type} has no {@link Type} annotation or a blank name
     */
    static String typeName(Class<?> type) {
        Type marker = type.getAnnotation(Type.class);
        if (marker == null || marker.value() == null || marker.value().isBlank()) {
            throw new IllegalStateException(
                    type.getName() + " is not a MappingsAction with a logical @MappingsAction.Type");
        }
        return marker.value();
    }
}
