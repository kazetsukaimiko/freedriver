package io.freedriver.jsonlink.config.v3;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@c")
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = JoystickButtonEvent.class,
                name = "joystick-button",
                names = {".JoystickButtonEvent"})
})
public abstract class ControlEvent {

}
