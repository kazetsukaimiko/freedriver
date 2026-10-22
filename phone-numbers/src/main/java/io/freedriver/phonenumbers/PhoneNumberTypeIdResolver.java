package io.freedriver.phonenumbers;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

/**
 * Resolves {@code type} only through {@link PhoneNumber#registeredTypes()}. Never calls
 * {@code Class.forName} and never applies Jackson default typing. An unknown or missing id fails
 * closed and constructs nothing.
 */
public final class PhoneNumberTypeIdResolver extends TypeIdResolverBase {
    @Override
    public String idFromValue(Object value) {
        return PhoneNumber.typeName(value.getClass());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return value == null ? PhoneNumber.typeName(suggestedType) : idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        Map<String, Class<? extends PhoneNumber>> types = PhoneNumber.registeredTypes();
        Class<? extends PhoneNumber> raw = id == null ? null : types.get(id);
        if (raw == null) {
            throw unknown(id);
        }
        return context.constructType(raw);
    }

    @Override
    public String getDescForKnownTypeIds() {
        return PhoneNumber.registeredTypes().keySet().toString();
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    static JsonMappingException unknown(String id) {
        String shown = id == null || id.isBlank() ? "<missing>" : id;
        return new JsonMappingException(null, "unknown PhoneNumber type '" + shown + "'");
    }
}
