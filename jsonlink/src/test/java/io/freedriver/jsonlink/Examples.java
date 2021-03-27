package io.freedriver.jsonlink;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.freedriver.jsonlink.jackson.schema.v1.AnalogRead;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import io.freedriver.jsonlink.jackson.schema.v1.Request;
import org.junit.jupiter.api.Test;

public class Examples {
    private ObjectMapper om = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Test
    public void trySerialize() throws JsonProcessingException {
        Request r = new Request()
                .analogRead(new AnalogRead(Identifier.of(0), 5, 10000));
        //r.setUuid(UUID.randomUUID());
        //r.setRequestId(UUID.randomUUID());
        System.out.println(om.writeValueAsString(r));
    }
}
