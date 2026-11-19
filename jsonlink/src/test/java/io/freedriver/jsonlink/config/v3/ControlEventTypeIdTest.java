package io.freedriver.jsonlink.config.v3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import io.freedriver.jsonlink.config.ConfigMapper;
import io.freedriver.jsonlink.controlevent.UnreadBinaryNameHolder;
import io.freedriver.jsonlink.jackson.JsonLinkModule;
import org.junit.jupiter.api.Test;

class ControlEventTypeIdTest {
    private static final String SAME_PACKAGE_ID = ".SamePackageUnread";
    private static final String BINARY_ID = "io.freedriver.jsonlink.controlevent.UnreadBinaryName";
    private static final String QUALIFIED_BUTTON = "io.freedriver.jsonlink.config.v3.JoystickButtonEvent";

    private static final JoystickButtonEvent SAMPLE = JoystickButtonEvent.builder()
            .button(7)
            .buttonState(JoystickButtonEvent.ButtonState.PRESS)
            .build();

    @Test
    void writesLogicalNameAndReadsItBack() throws Exception {
        String written = ConfigMapper.MAPPER.writeValueAsString(SAMPLE);
        assertEquals("""
                {
                  "@c" : "joystick-button",
                  "button" : 7,
                  "buttonState" : "PRESS"
                }""", written);
        assertEquals(SAMPLE, ConfigMapper.MAPPER.readValue(written, ControlEvent.class));
        assertFalse(SamePackageUnreadHolder.initialized);
        assertFalse(UnreadBinaryNameHolder.initialized);
    }

    @Test
    void eachMapperWritesTheLogicalName() throws Exception {
        String compact = """
                {"@c":"joystick-button","button":7,"buttonState":"PRESS"}""";
        for (ObjectMapper mapper : plainMappers()) {
            assertEquals(compact, mapper.writeValueAsString(SAMPLE));
            assertEquals(SAMPLE, mapper.readValue(compact, ControlEvent.class));
        }
    }

    @Test
    void readsTheHistoricalAlias() throws Exception {
        String historical = """
                {"@c":".JoystickButtonEvent","button":4,"buttonState":"RELEASE"}""";
        JoystickButtonEvent expected = JoystickButtonEvent.builder()
                .button(4)
                .buttonState(JoystickButtonEvent.ButtonState.RELEASE)
                .build();
        for (ObjectMapper mapper : mappers()) {
            assertEquals(expected, mapper.readValue(historical, ControlEvent.class));
        }
    }

    @Test
    void refusesUnknownBlankMissingAndQualifiedIds() {
        List<Refused> refused = List.of(
                new Refused("{\"@c\":\"no-such-event\",\"button\":1,\"buttonState\":\"PRESS\"}", "no-such-event"),
                new Refused("{\"@c\":\"\",\"button\":1,\"buttonState\":\"PRESS\"}", "''"),
                new Refused("{\"button\":1,\"buttonState\":\"PRESS\"}", "missing type id"),
                new Refused(
                        "{\"@c\":\"" + QUALIFIED_BUTTON + "\",\"button\":1,\"buttonState\":\"PRESS\"}",
                        QUALIFIED_BUTTON),
                new Refused(
                        "{\"@c\":\"" + SAME_PACKAGE_ID + "\",\"button\":1,\"buttonState\":\"PRESS\"}",
                        SAME_PACKAGE_ID),
                new Refused(
                        "{\"@c\":\"" + BINARY_ID + "\",\"button\":1,\"buttonState\":\"PRESS\"}",
                        BINARY_ID));
        for (ObjectMapper mapper : mappers()) {
            for (Refused item : refused) {
                InvalidTypeIdException thrown = assertThrows(
                        InvalidTypeIdException.class,
                        () -> mapper.readValue(item.json(), ControlEvent.class));
                assertTrue(thrown.getMessage().contains(item.fragment()), thrown.getMessage());
            }
        }
        assertFalse(SamePackageUnreadHolder.initialized);
        assertFalse(UnreadBinaryNameHolder.initialized);
    }

    @Test
    void joystickButtonEventHasNoPublicNoArgConstructor() {
        assertThrows(NoSuchMethodException.class, () -> JoystickButtonEvent.class.getConstructor());
    }

    private static List<ObjectMapper> mappers() {
        return List.of(ConfigMapper.MAPPER, JsonLinkModule.getMapper(), new ObjectMapper());
    }

    private static List<ObjectMapper> plainMappers() {
        return List.of(JsonLinkModule.getMapper(), new ObjectMapper());
    }

    private record Refused(String json, String fragment) {
    }
}
