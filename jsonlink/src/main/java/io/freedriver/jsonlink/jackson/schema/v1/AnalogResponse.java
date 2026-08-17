package io.freedriver.jsonlink.jackson.schema.v1;

import lombok.Builder;

@Builder(toBuilder = true)
public record AnalogResponse(Identifier pin, Integer raw, Float voltage, Float resistance) {}
