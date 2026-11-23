package io.freedriver.jsonlink.api.v1;

/** One pin and the mode to set it to. */
public record ModeSet(Identifier pinNumber, Mode mode) {}
