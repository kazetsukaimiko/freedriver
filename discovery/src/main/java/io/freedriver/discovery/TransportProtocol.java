package io.freedriver.discovery;

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
        return "_" + Optional.ofNullable(name)
                .orElse(name().toLowerCase());
    }
}
