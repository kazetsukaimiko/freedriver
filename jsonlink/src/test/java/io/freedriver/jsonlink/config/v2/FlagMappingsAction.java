package io.freedriver.jsonlink.config.v2;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@MappingsAction.Type("flag")
public final class FlagMappingsAction implements MappingsAction {
    public static final AtomicInteger constructed = new AtomicInteger();

    private final boolean on;

    /** ServiceLoader provider constructor. Not used to run the action. */
    public FlagMappingsAction() {
        this.on = false;
    }

    @JsonCreator
    public FlagMappingsAction(@JsonProperty("on") boolean on) {
        constructed.incrementAndGet();
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FlagMappingsAction action && on == action.on;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(on);
    }
}
