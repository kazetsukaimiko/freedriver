package io.freedriver.base.discovery;

import java.util.Optional;

/**
 * Common Application protocols
 */
public enum ApplicationProtocols implements ApplicationProtocol {
    _http,
    _ftp,
    _ssh,

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
