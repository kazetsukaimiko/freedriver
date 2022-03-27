package io.freedriver.jsonlink;

import io.freedriver.base.util.accumulator.Accumulator;
import io.freedriver.jsonlink.jackson.schema.v1.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResponseAccumulator implements Accumulator<ByteArrayOutputStream, Response> {

    @Override
    public boolean isComplete(ByteArrayOutputStream baos) {
        String input = baos.toString(StandardCharsets.UTF_8);
        Map<Character, Integer> occurrenceMap = occurrenceMap(input);
        // More than one line- start over.
        if (occurrenceMap.getOrDefault('\n', 0) > 1) {
            baos.reset();
            return false;
        }
        return input.startsWith("{") && input.endsWith("}\n") && !input.contains(String.valueOf((char) 65533))
                && Objects.equals(occurrenceMap.getOrDefault('{', 0), occurrenceMap.getOrDefault('}', 0))
                && Objects.equals(occurrenceMap.getOrDefault('[', 0), occurrenceMap.getOrDefault(']', 0));
    }

    @Override
    public Response convert(ByteArrayOutputStream input) {
        try {
            return Connector.MAPPER.readValue(input.toByteArray(), Response.class);
        } catch (IOException e) {
            return null;
        }
    }

    private static Map<Character, Integer> occurrenceMap(String input) {
        Map<Character, Integer> om = new HashMap<>();
        if (input != null && !input.isEmpty()) {
            for (int i = 0; i < input.length(); i++) {
                Character key = input.charAt(i);
                if (!om.containsKey(key)) {
                    om.put(key, 1);
                } else {
                    om.put(key, om.get(key) + 1);
                }
            }
        }
        return om;
    }
}
