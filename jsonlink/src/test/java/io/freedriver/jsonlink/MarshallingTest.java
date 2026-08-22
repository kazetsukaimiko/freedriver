package io.freedriver.jsonlink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.freedriver.jsonlink.jackson.JsonLinkModule;
import io.freedriver.jsonlink.jackson.schema.base.BaseResponse;
import io.freedriver.jsonlink.jackson.schema.base.Version;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import org.junit.jupiter.api.Test;

public class MarshallingTest {

    protected ObjectMapper mapper = JsonLinkModule.getMapper();

    @Test
    public void testMarshallVersion() throws JsonProcessingException {
        String json = "{ \"version\": [1,0,0] }";
        BaseResponse baseResponse = mapper.readValue(json, BaseResponse.class);

        Version expected = new Version(1,0,0);
        Version unexpected = new Version(2,0,0);

        assertEquals(expected, baseResponse.version());
        assertNotEquals(unexpected, baseResponse.version());
    }

    @Test
    public void testIdentifierFromJsonNumber() throws JsonProcessingException {
        assertEquals(Identifier.of(26), mapper.readValue("26", Identifier.class));
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
                mapper.readValue(json, io.freedriver.jsonlink.config.v2.Mappings.class);
        assertEquals(1, mappings.getMappings().size());
        io.freedriver.jsonlink.config.v2.Mapping mapping = mappings.getMappings().iterator().next();
        assertEquals(Identifier.of(26), mapping.appliances().get(0).identifier());
    }
}
