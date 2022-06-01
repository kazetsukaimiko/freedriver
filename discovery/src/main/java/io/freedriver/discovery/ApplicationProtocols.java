package io.freedriver.discovery;

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
        return "_" + Optional.ofNullable(name)
                .orElse(name().toLowerCase());
    }
}
