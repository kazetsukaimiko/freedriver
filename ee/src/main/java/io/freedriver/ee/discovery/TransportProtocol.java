package io.freedriver.ee.discovery;

import java.util.Optional;

public enum TransportProtocol implements Protocol {
    TCP,
    UDP,
    ;

    private final String name;

    TransportProtocol(String name) {
        this.name = name;
    }

    TransportProtocol() {
        this(null);
    }

    @Override
    public String getName() {
        return Optional.ofNullable(name)
                .orElse(name().toLowerCase());
    }
}
