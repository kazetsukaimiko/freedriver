package io.freedriver.phonenumbers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class UsPhoneNumberTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void resetConstructions() {
        UsPhoneNumber.constructed.set(0);
    }

    @Test
    void registryListsTheLogicalNameWithoutConstructingANumber() {
        Map<String, Class<? extends PhoneNumber>> types = PhoneNumber.registeredTypes();

        assertEquals(Set.of("us"), types.keySet());
        assertEquals(UsPhoneNumber.class, types.get("us"));
        assertEquals("us", PhoneNumber.typeName(UsPhoneNumber.class));
        assertFalse(types.containsKey(UsPhoneNumber.class.getName()));
        assertThrows(UnsupportedOperationException.class, () -> types.put("other", UsPhoneNumber.class));
        assertEquals(0, UsPhoneNumber.constructed.get());
    }

    @Test
    void parsesAndNormalizesToE164() {
        UsPhoneNumber number = UsPhoneNumber.parse("+14155552671");

        assertEquals("+14155552671", number.toString());
        assertEquals("+14155552671", number.getNumber());
        assertEquals("us", number.type());
        assertEquals(number, UsPhoneNumber.parse("+14155552671"));
        assertEquals(number.hashCode(), UsPhoneNumber.parse("+14155552671").hashCode());
        assertNotEquals(number, UsPhoneNumber.parse("+12025550123"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"+14155552671", "+12025550123", "+10000000000", "+19999999999", "+11234567890"})
    void exposesTheAreaCodeAsTheThreeDigitsAfterPlusOne(String e164) {
        UsPhoneNumber number = UsPhoneNumber.parse(e164);

        assertEquals(e164.substring(2, 5), number.getUsAreaCode());
        assertEquals("+1" + number.getUsAreaCode() + e164.substring(5), number.toString());
        assertEquals(12, number.toString().length());
    }

    @ParameterizedTest
    @ValueSource(strings = {"+442079460958", "+33142685300", "+8613812345678", "+71234567890", "+21055551212"})
    void rejectsAnyOtherCountryCode(String raw) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> UsPhoneNumber.parse(raw));
        assertTrue(error.getMessage().contains("+1"), error.getMessage());
        assertFalse(error.getMessage().contains(raw), error.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(
            strings = {
                "",
                " ",
                "4155552671",
                "14155552671",
                "+1415555267",
                "+141555526711",
                "+1 4155552671",
                "+14155552671 ",
                " +14155552671",
                "+1-415-555-2671",
                "(415) 555-2671",
                "+1 (415) 555-2671",
                "0014155552671",
                "+1abcdefghij",
                "+1415555267a",
                "++1415555267",
                "+14155552671\n",
                "\uFF0B14155552671"
            })
    void rejectsAnyShapeOtherThanLeadingPlusOneAndTenDigits(String raw) {
        assertThrows(IllegalArgumentException.class, () -> UsPhoneNumber.parse(raw));
    }

    @Test
    void roundTripsJsonWithoutWritingTheClassNameOrTheAreaCodeField() throws Exception {
        UsPhoneNumber number = UsPhoneNumber.parse("+14155552671");
        UsPhoneNumber.constructed.set(0);

        String json = mapper.writeValueAsString(number);

        assertEquals(0, UsPhoneNumber.constructed.get());
        JsonNode node = mapper.readTree(json);
        assertEquals("us", node.get("type").asText());
        assertEquals("+14155552671", node.get("number").asText());
        assertEquals(2, node.size());
        assertFalse(json.contains(UsPhoneNumber.class.getName()), json);
        assertFalse(json.contains("usAreaCode"), json);

        PhoneNumber back = mapper.readValue(json, PhoneNumber.class);
        assertEquals(number, back);
        assertEquals("415", ((UsPhoneNumber) back).getUsAreaCode());
        assertEquals("+14155552671", back.toString());
        assertEquals(1, UsPhoneNumber.constructed.get());
    }

    @Test
    void logicalTypeIdIsNotAClassName() {
        JsonTypeInfo info = PhoneNumber.class.getAnnotation(JsonTypeInfo.class);

        assertEquals(JsonTypeInfo.Id.CUSTOM, info.use());
        assertEquals("type", info.property());
        assertEquals(JsonTypeInfo.class, info.defaultImpl());
        assertEquals("us", PhoneNumber.typeName(UsPhoneNumber.class));
        assertFalse(PhoneNumber.typeName(UsPhoneNumber.class).contains("."));
        assertThrows(IllegalStateException.class, () -> PhoneNumber.typeName(String.class));
    }

    @Test
    void classNameInThePayloadIsRejected() {
        String className = UsPhoneNumber.class.getName();
        JsonProcessingException error = assertThrows(
                JsonProcessingException.class,
                () -> mapper.readValue(
                        "{\"type\":\"" + className + "\",\"number\":\"+14155552671\"}", PhoneNumber.class));

        assertTrue(error.getMessage().contains(className), error.getMessage());
        assertTrue(error.getMessage().contains("unknown PhoneNumber type"), error.getMessage());
        assertEquals(0, UsPhoneNumber.constructed.get());
    }

    @Test
    void unknownTypeIdIsRejected() {
        JsonProcessingException error = assertThrows(
                JsonProcessingException.class,
                () -> mapper.readValue("{\"type\":\"ca\",\"number\":\"+14155552671\"}", PhoneNumber.class));

        assertTrue(error.getMessage().contains("unknown PhoneNumber type 'ca'"), error.getMessage());
        assertEquals(0, UsPhoneNumber.constructed.get());
    }

    @Test
    void missingTypeIdIsRejected() {
        for (String json : new String[] {
            "{\"number\":\"+14155552671\"}",
            "{}",
            "{\"type\":null,\"number\":\"+14155552671\"}",
            "{\"type\":\"\",\"number\":\"+14155552671\"}",
            "{\"type\":\"   \",\"number\":\"+14155552671\"}"
        }) {
            UsPhoneNumber.constructed.set(0);
            JsonProcessingException error =
                    assertThrows(JsonProcessingException.class, () -> mapper.readValue(json, PhoneNumber.class), json);
            assertEquals(0, UsPhoneNumber.constructed.get(), json + " -> " + error.getMessage());
        }
    }

    @Test
    void doesNotFallBackToStringOrADefaultSubtype() throws Exception {
        JsonProcessingException scalar = assertThrows(
                JsonProcessingException.class, () -> mapper.readValue("\"+14155552671\"", PhoneNumber.class));
        assertEquals(0, UsPhoneNumber.constructed.get(), scalar.getMessage());

        Object asObject = mapper.readValue("\"+14155552671\"", Object.class);
        assertInstanceOf(String.class, asObject);
        assertFalse(asObject instanceof PhoneNumber);

        JsonProcessingException missing = assertThrows(
                JsonProcessingException.class,
                () -> mapper.readValue("{\"number\":\"+14155552671\"}", PhoneNumber.class));
        assertFalse(missing.getMessage().contains("UsPhoneNumber"), missing.getMessage());
        assertEquals(0, UsPhoneNumber.constructed.get());
    }

    @Test
    void defaultTypingIsOffAndAPayloadCannotNameAClass() throws Exception {
        assertNull(mapper.getDeserializationConfig().getDefaultTyper(mapper.constructType(PhoneNumber.class)));

        String wrapper = "[\"io.freedriver.phonenumbers.UsPhoneNumber\",{\"number\":\"+14155552671\"}]";
        assertThrows(JsonProcessingException.class, () -> mapper.readValue(wrapper, PhoneNumber.class));
        assertInstanceOf(List.class, mapper.readValue(wrapper, Object.class));
        assertEquals(0, UsPhoneNumber.constructed.get());

        String named = "{\"@class\":\"java.util.ArrayList\",\"number\":\"+14155552671\"}";
        assertThrows(JsonProcessingException.class, () -> mapper.readValue(named, PhoneNumber.class));
        assertEquals(0, UsPhoneNumber.constructed.get());

        String nested = "{\"type\":\"us\",\"number\":{\"@class\":\"java.lang.Object\"}}";
        assertThrows(JsonProcessingException.class, () -> mapper.readValue(nested, PhoneNumber.class));

        for (String typeId : new String[] {"US", "Us", "java.lang.Object", UsPhoneNumber.class.getName()}) {
            UsPhoneNumber.constructed.set(0);
            String gadget = "{\"type\":\"" + typeId + "\",\"number\":\"+14155552671\"}";
            JsonProcessingException error =
                    assertThrows(JsonProcessingException.class, () -> mapper.readValue(gadget, PhoneNumber.class));
            assertTrue(error.getMessage().contains("unknown PhoneNumber type '" + typeId + "'"), error.getMessage());
            assertNoClassForName(error);
            assertEquals(0, UsPhoneNumber.constructed.get(), typeId);
        }
    }

    @Test
    void validTypeWithABadNumberIsRejectedByTheParser() {
        JsonProcessingException error = assertThrows(
                JsonProcessingException.class,
                () -> mapper.readValue("{\"type\":\"us\",\"number\":\"+442079460958\"}", PhoneNumber.class));

        assertTrue(error.getMessage().contains("+1XXXXXXXXXX"), error.getMessage());
        assertFalse(error.getMessage().contains("+442079460958"), error.getMessage());
        assertEquals(1, UsPhoneNumber.constructed.get());
    }

    private static void assertNoClassForName(Throwable error) {
        for (Throwable current = error; current != null; current = current.getCause()) {
            assertFalse(current instanceof ClassNotFoundException, current.toString());
            assertFalse(current.getClass().getName().contains("ClassNotFound"), current.toString());
        }
    }
}
