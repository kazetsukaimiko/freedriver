package io.freedriver.jsonlink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.freedriver.jsonlink.api.v1.BoardRequest;
import io.freedriver.jsonlink.api.v1.BoardResponse;
import io.freedriver.jsonlink.api.v1.DigitalState;
import io.freedriver.jsonlink.api.v1.Identifier;
import io.freedriver.jsonlink.api.v1.Mode;
import org.junit.jupiter.api.Test;

public class MarshallingTest {

    @Test
    public void testIdentifierFromJsonNumber() throws JsonProcessingException {
        assertEquals(Identifier.of(26), Connector.MAPPER.readValue("26", Identifier.class));
    }

    @Test
    public void testMappingsV2ApplianceIdentifier() throws JsonProcessingException {
        String json = """
                {
                  "eventTTL": 7,
                  "eventTTLUnit": "DAYS",
                  "mappings": [{
                    "connectorId": "0f2829e1-1804-4993-ae33-c5dd21840646",
                    "appliances": [{"identifier": 26, "name": "water_inlet"}]
                  }]
                }
                """;
        io.freedriver.jsonlink.config.v2.Mappings mappings =
                Connector.MAPPER.readValue(json, io.freedriver.jsonlink.config.v2.Mappings.class);
        assertEquals(1, mappings.getMappings().size());
        io.freedriver.jsonlink.config.v2.Mapping mapping = mappings.getMappings().iterator().next();
        assertEquals(Identifier.of(26), mapping.appliances().get(0).identifier());
    }

    @Test
    public void connectorMapperKeepsFalsePinValuesAndSkipsFirmwareVersion() throws JsonProcessingException {
        Identifier input = Identifier.of(2);
        Identifier output = Identifier.of(13);
        BoardRequest request = BoardRequest.empty()
                .modeSet(input.setMode(Mode.INPUT), output.setMode(Mode.OUTPUT))
                .digitalWrite(input.setDigital(DigitalState.HIGH), output.setDigital(DigitalState.LOW));

        JsonNode json = Connector.MAPPER.readTree(Connector.MAPPER.writeValueAsString(request));
        JsonNode mode = json.get("mode");
        assertTrue(mode.has("2"));
        assertTrue(mode.has("13"));
        assertFalse(mode.get("2").booleanValue());
        assertTrue(mode.get("13").booleanValue());
        JsonNode digital = json.get("write").get("digital");
        assertTrue(digital.has("2"));
        assertTrue(digital.has("13"));
        assertFalse(digital.get("2").booleanValue());
        assertTrue(digital.get("13").booleanValue());

        BoardResponse response = Connector.MAPPER.readValue(
                "{\"version\":[1,0,0],\"digital\":{\"13\":true}}", BoardResponse.class);
        assertEquals(Map.of(Identifier.of(13), true), response.digital());
    }
}
