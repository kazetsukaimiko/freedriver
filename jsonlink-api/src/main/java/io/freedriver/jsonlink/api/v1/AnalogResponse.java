package io.freedriver.jsonlink.api.v1;

/** The board's reading of one analog pin. */
public record AnalogResponse(Identifier pin, Integer raw, Float voltage, Float resistance) {}
