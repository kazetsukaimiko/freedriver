package io.freedriver.jsonlink.config.v3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@Jacksonized
public class JoystickButtonEvent extends ControlEvent {
    private final int button;
    private final ButtonState buttonState;

    public enum ButtonState {
        RELEASE,
        PRESS
    }
}
