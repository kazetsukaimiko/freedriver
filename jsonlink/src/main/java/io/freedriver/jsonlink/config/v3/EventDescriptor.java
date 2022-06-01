package io.freedriver.jsonlink.config.v3;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EventDescriptor extends Descriptor {
    @JsonCreator
    public EventDescriptor(String value) {
        super(value);
    }
}
