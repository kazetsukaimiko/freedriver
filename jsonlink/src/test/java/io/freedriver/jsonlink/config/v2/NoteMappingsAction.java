package io.freedriver.jsonlink.config.v2;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@MappingsAction.Type("note")
public final class NoteMappingsAction implements MappingsAction {
    public static final AtomicInteger constructed = new AtomicInteger();

    @NotNull
    private final String note;

    /** ServiceLoader provider constructor. Not used to run the action. */
    public NoteMappingsAction() {
        this.note = null;
    }

    @JsonCreator
    public NoteMappingsAction(@JsonProperty("note") String note) {
        constructed.incrementAndGet();
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NoteMappingsAction action && Objects.equals(note, action.note);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(note);
    }
}
