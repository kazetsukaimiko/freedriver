package io.freedriver.victron;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enum representing LOAD column values.
 */
public enum LoadOutputState {
    ON,
    OFF;

    static Optional<LoadOutputState> byName(String name) {
        return Stream.of(LoadOutputState.values())
                .filter(relayState -> Objects.equals(relayState.name(), name.toUpperCase()))
                .findFirst();
    }
}
