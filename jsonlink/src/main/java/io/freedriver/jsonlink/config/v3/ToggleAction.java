package io.freedriver.jsonlink.config.v3;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ToggleAction extends ApplianceDescriptor {
    @JsonCreator
    public ToggleAction(String value) {
        super(value);
    }
}
