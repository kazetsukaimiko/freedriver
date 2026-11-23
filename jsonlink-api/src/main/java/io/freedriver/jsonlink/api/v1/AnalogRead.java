package io.freedriver.jsonlink.api.v1;

/** A request to read one analog pin through a divider of the given voltage and resistance. */
public record AnalogRead(Identifier pin, float voltage, float resistance) {}
