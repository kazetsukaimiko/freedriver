package io.freedriver.jsonlink.config.v3;

public class JoystickButtonEvent extends ControlEvent {
    private int button;
    private  ButtonState buttonState;

    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
    }

    public ButtonState getButtonState() {
        return buttonState;
    }

    public void setButtonState(ButtonState buttonState) {
        this.buttonState = buttonState;
    }

    public enum ButtonState {
        RELEASE,
        PRESS;
    }
}
