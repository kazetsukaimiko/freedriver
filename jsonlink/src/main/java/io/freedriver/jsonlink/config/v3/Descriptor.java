package io.freedriver.jsonlink.config.v3;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public abstract class Descriptor {
    private final String value;

    public Descriptor(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
