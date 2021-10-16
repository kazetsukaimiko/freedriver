package io.freedriver.base.discovery;

import java.util.Optional;

public enum TransportProtocols implements TransportProtocol {
    _tcp,
    _udp,
    ;

    private final String name;

    TransportProtocols(String name) {
        this.name = name;
    }

    TransportProtocols() {
        this(null);
    }

    @Override
    public String getName() {
        return Optional.ofNullable(name)
                .orElse(name().toLowerCase());
    }
}
