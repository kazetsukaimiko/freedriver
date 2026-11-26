package io.freedriver.jsonlink.api.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.jupiter.api.Test;

/**
 * The contract of the schema's JSON form: every type reads back from what it writes, on a
 * plain {@link ObjectMapper} with nothing registered.
 */
class BoardSchemaRoundTripTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Identifier PIN_2 = Identifier.of(2);
    private static final Identifier PIN_13 = Identifier.of(13);

    private static BoardRequest request() {
        return new BoardRequest(
                        UUID.fromString("7d4f6f0e-3c1b-4e0a-9d55-0a4f4d2f6b11"),
                        UUID.fromString("0b9c2a52-8a43-4c0e-b0a5-6d1c3b7e9f20"),
                        null,
                        null,
                        null,
                        Boolean.TRUE,
                        List.of(Identifier.of(7)),
                        List.of(Identifier.of(8)))
                .modeSet(PIN_2.setMode(Mode.INPUT), PIN_13.setMode(Mode.OUTPUT))
                .digitalWrite(PIN_2.setDigital(DigitalState.HIGH), PIN_13.setDigital(DigitalState.LOW))
                .digitalRead(PIN_2)
                .analogRead(Identifier.of(14).getAnalog(3.3f, 10_000f));
    }

    private static BoardResponse response() {
        return new BoardResponse(
                UUID.fromString("7d4f6f0e-3c1b-4e0a-9d55-0a4f4d2f6b11"),
                UUID.fromString("0b9c2a52-8a43-4c0e-b0a5-6d1c3b7e9f20"),
                new BoardInfo(List.of(PIN_2, PIN_13), List.of(Identifier.of(14))),
                List.of("info"),
                List.of("error"),
                List.of("debug"),
                Map.of(PIN_2, true, PIN_13, false),
                List.of(new AnalogResponse(Identifier.of(14), 512, 1.65f, 10_000f)));
    }

    @Test
    void boardRequestReadsBackEqual() throws JsonProcessingException {
        BoardRequest original = request();

        BoardRequest readBack = MAPPER.readValue(MAPPER.writeValueAsString(original), BoardRequest.class);

        assertEquals(original, readBack);
    }

    @Test
    void boardRequestMapKeysReadBackAsIdentifiers() throws JsonProcessingException {
        BoardRequest original = request();

        BoardRequest readBack = MAPPER.readValue(MAPPER.writeValueAsString(original), BoardRequest.class);

        assertEquals(Map.of(PIN_2, Mode.INPUT, PIN_13, Mode.OUTPUT), readBack.mode());
        assertEquals(
                Map.of(PIN_2, DigitalState.HIGH, PIN_13, DigitalState.LOW),
                readBack.write().digital());
        readBack.mode().keySet().forEach(key -> assertInstanceOf(Identifier.class, key));
        readBack.write().digital().keySet().forEach(key -> assertInstanceOf(Identifier.class, key));
    }

    @Test
    void boardRequestWireForm() {
        JsonNode json = MAPPER.valueToTree(request());

        JsonNode mode = json.get("mode");
        assertTrue(mode.get("2").isBoolean());
        assertFalse(mode.get("2").booleanValue(), "INPUT is false");
        assertTrue(mode.get("13").booleanValue(), "OUTPUT is true");

        JsonNode digital = json.get("write").get("digital");
        assertTrue(digital.get("2").isBoolean());
        assertFalse(digital.get("2").booleanValue(), "HIGH is false");
        assertTrue(digital.get("13").booleanValue(), "LOW is true");

        assertTrue(json.get("turn_off").get(0).isInt(), "an Identifier value is a JSON number");
        assertEquals(7, json.get("turn_off").get(0).intValue());
        assertEquals(8, json.get("turn_on").get(0).intValue());
        assertEquals(2, json.get("read").get("digital").get(0).intValue());

        assertEquals(Set.of("digital", "analog"), Set.copyOf(fieldNames(json.get("read"))));
        assertEquals(Set.of("digital"), Set.copyOf(fieldNames(json.get("write"))));
        assertEquals(
                Set.of("uuid", "requestId", "mode", "read", "write", "boardInfo", "turn_off", "turn_on"),
                Set.copyOf(fieldNames(json)));
    }

    @Test
    void boardResponseReadsBackEqual() throws JsonProcessingException {
        BoardResponse original = response();

        BoardResponse readBack = MAPPER.readValue(MAPPER.writeValueAsString(original), BoardResponse.class);

        assertEquals(original, readBack);
        readBack.digital().keySet().forEach(key -> assertInstanceOf(Identifier.class, key));
    }

    @Test
    void boardResponseWireForm() {
        JsonNode json = MAPPER.valueToTree(response());

        assertTrue(json.get("digital").get("2").booleanValue());
        assertFalse(json.get("digital").get("13").booleanValue());
        assertEquals(14, json.get("analog").get(0).get("pin").intValue());
        assertEquals(
                Set.of("uuid", "requestId", "boardInfo", "info", "error", "debug", "digital", "analog"),
                Set.copyOf(fieldNames(json)));
    }

    @Test
    void boardReplyWithOnlySomePropertiesReadsAndSkipsTheFirmwareVersion() throws JsonProcessingException {
        BoardResponse read = MAPPER.readValue("{\"version\":[1,0,0],\"digital\":{\"13\":true}}", BoardResponse.class);

        assertEquals(Map.of(PIN_13, true), read.digital());
        assertEquals(List.of(), read.error());
    }

    @Test
    void emptyValuesReadBackEqual() throws JsonProcessingException {
        assertEquals(
                BoardRequest.empty(),
                MAPPER.readValue(MAPPER.writeValueAsString(BoardRequest.empty()), BoardRequest.class));
        assertEquals(
                BoardResponse.empty(),
                MAPPER.readValue(MAPPER.writeValueAsString(BoardResponse.empty()), BoardResponse.class));
    }

    @Test
    void modeBooleanIsTheInverseOfItsModeValue() {
        for (Mode mode : Mode.values()) {
            assertEquals(mode, Mode.ofModeValue(mode.getModeValue()));
        }
        assertEquals(Mode.OUTPUT, Mode.ofModeValue(true));
        assertEquals(Mode.INPUT, Mode.ofModeValue(false));
    }

    @Test
    void identifierReadsFromNumberAndFromKeyDigits() throws JsonProcessingException {
        assertEquals("13", MAPPER.writeValueAsString(PIN_13));
        assertEquals(PIN_13, MAPPER.readValue("13", Identifier.class));
        assertEquals(PIN_13, Identifier.valueOf("13"));
    }

    @Test
    void analogIdentifierReadsBackAsValueAndAsKey() throws JsonProcessingException {
        AnalogIdentifier a3 = new AnalogIdentifier(3);

        assertEquals("\"A3\"", MAPPER.writeValueAsString(a3));
        assertEquals(a3, MAPPER.readValue("\"A3\"", AnalogIdentifier.class));

        Map<AnalogIdentifier, Integer> readings = Map.of(a3, 512);
        String json = MAPPER.writeValueAsString(readings);
        assertEquals("{\"A3\":512}", json);
        assertEquals(
                readings,
                MAPPER.readValue(
                        json,
                        MAPPER.getTypeFactory().constructMapType(Map.class, AnalogIdentifier.class, Integer.class)));
    }


    @Test
    void anotherUnknownReplyPropertyStillFails() {
        assertThrows(
                UnrecognizedPropertyException.class,
                () -> MAPPER.readValue("{\"versoin\":[1,0,0]}", BoardResponse.class));
    }

    private static List<String> fieldNames(JsonNode node) {
        return node.properties().stream().map(Map.Entry::getKey).toList();
    }
}
