package io.freedriver.jsonlink.jackson.schema.v1;

import lombok.Builder;

@Builder(toBuilder = true)
public record AnalogRead(Identifier pin, float voltage, float resistance) {}
