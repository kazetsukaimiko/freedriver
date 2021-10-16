package io.freedriver.base.discovery;

import java.util.Optional;

public enum CommonScopes implements Scope {
    local;

    private final String name;

    CommonScopes(String name) {
        this.name = name;
    }

    CommonScopes() {
        this(null);
    }

    @Override
    public String getName() {
        return Optional.ofNullable(name)
                .orElse(name().toLowerCase());
    }
}
