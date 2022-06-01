package io.freedriver.jsonlink.config.v3;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public abstract class Descriptor {
    private final String value;

    public Descriptor(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Descriptor that = (Descriptor) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
