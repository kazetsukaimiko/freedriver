package io.freedriver.ee.discovery;

import java.util.Optional;

/**
 * Common Application protocols
 */
public enum ApplicationProtocols implements ApplicationProtocol {
    HTTP,
    FTP,
    SSH,

    ;

    private final String name;

    ApplicationProtocols(String name) {
        this.name = name;
    }

    ApplicationProtocols() {
        this(null);
    }

    @Override
    public String getName() {
        return Optional.ofNullable(name)
                .orElse(name().toLowerCase());
    }
}
