package io.freedriver.phonenumbers;

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
 * A phone number serialized with a Jackson {@code type} property.
 *
 * <p>Register an implementation by listing its class in
 * {@code META-INF/services/io.freedriver.phonenumbers.PhoneNumber} and marking it with
 * {@link Type}. {@link ServiceLoader} requires a public no-arg constructor; discovery reads
 * {@link ServiceLoader.Provider#type()} and does not call that constructor. The JSON {@code type}
 * value is the logical name. It is never a class name, and Jackson default typing is not used.
 * An unknown or missing type id is rejected. The value does not fall back to {@link String} or a
 * default subtype.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeIdResolver(PhoneNumberTypeIdResolver.class)
public interface PhoneNumber {
    /**
     * Logical {@code type} value. Not a class name.
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
     * Numbers registered through {@link ServiceLoader}, keyed by logical {@link Type} name.
     */
    static Map<String, Class<? extends PhoneNumber>> registeredTypes() {
        return PhoneNumberRegistry.types();
    }

    /**
     * @throws IllegalStateException when {@code type} has no {@link Type} annotation or a blank name
     */
    static String typeName(Class<?> type) {
        Type marker = type.getAnnotation(Type.class);
        if (marker == null || marker.value() == null || marker.value().isBlank()) {
            throw new IllegalStateException(
                    type.getName() + " is not a PhoneNumber with a logical @PhoneNumber.Type");
        }
        return marker.value();
    }
}
