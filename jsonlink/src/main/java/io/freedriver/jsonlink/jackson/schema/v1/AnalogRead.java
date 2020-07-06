package io.freedriver.jsonlink.jackson.schema.v1;

import java.util.Objects;

public class AnalogRead {
    private Identifier pin;
    private float voltage;
    private float resistance;

    public AnalogRead() {
    }

    public AnalogRead(Identifier pin, float voltage, float resistance) {
        this.pin = pin;
        this.voltage = voltage;
        this.resistance = resistance;
    }

    public Identifier getPin() {
        return pin;
    }

    public void setPin(Identifier pin) {
        this.pin = pin;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getResistance() {
        return resistance;
    }

    public void setResistance(float resistance) {
        this.resistance = resistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalogRead that = (AnalogRead) o;
        return Float.compare(that.voltage, voltage) == 0 &&
                Float.compare(that.resistance, resistance) == 0 &&
                Objects.equals(pin, that.pin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pin);
    }

    @Override
    public String toString() {
        return "AnalogRead{" +
                "pinNumber=" + pin +
                ", voltage=" + voltage +
                ", resistance=" + resistance +
                '}';
    }
}
