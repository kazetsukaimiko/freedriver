package io.freedriver.victron;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enum representing Relay column values.
 */
public enum RelayState {
    ON,
    OFF;

    static Optional<RelayState> byName(String name) {
        return Stream.of(RelayState.values())
                .filter(relayState -> Objects.equals(relayState.name(), name.toUpperCase()))
                .findFirst();
    }
}
