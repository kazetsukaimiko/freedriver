package io.freedriver.jsonlink.config.v3;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ApplianceDescriptor extends Descriptor {
    @JsonCreator
    public ApplianceDescriptor(String value) {
        super(value);
    }
}
