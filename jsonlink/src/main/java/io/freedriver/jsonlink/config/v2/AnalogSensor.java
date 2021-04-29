package io.freedriver.jsonlink.config.v2;

import io.freedriver.jsonlink.jackson.schema.v1.AnalogRead;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import io.freedriver.jsonlink.jackson.schema.v1.Request;

import java.util.Objects;

public class AnalogSensor {
    private String name;
    private Identifier pin;
    private float voltage;
    private float resistance;
    private boolean inverted = false;
    private SensorModes mode;
    private Double factor;
    private long averageOver = -1L;

    public AnalogSensor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Request applyToRequest(Request r) {
        return r.analogRead(asAnalogRead());
    }

    public AnalogRead asAnalogRead() {
        return new AnalogRead(pin, voltage, resistance);
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public SensorModes getMode() {
        return mode;
    }

    public void setMode(SensorModes mode) {
        this.mode = mode;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public long getAverageOver() {
        return averageOver;
    }

    public void setAverageOver(long averageOver) {
        this.averageOver = averageOver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalogSensor that = (AnalogSensor) o;
        return Objects.equals(name, that.name) && Objects.equals(pin, that.pin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pin);
    }

    @Override
    public String toString() {
        return "AnalogSensor{" +
                "name='" + name + '\'' +
                ", pin=" + pin +
                ", voltage=" + voltage +
                ", resistance=" + resistance +
                '}';
    }
}
