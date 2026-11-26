package io.freedriver.jsonlink.config.v2;

import io.freedriver.jsonlink.api.v1.AnalogRead;
import io.freedriver.jsonlink.api.v1.BoardRequest;
import io.freedriver.jsonlink.api.v1.Identifier;
import lombok.Builder;

@Builder(toBuilder = true)
public record AnalogSensor(
        String name,
        Identifier pin,
        float voltage,
        float resistance,
        boolean inverted,
        SensorModes mode,
        Double factor,
        long averageOver) {
    public static class AnalogSensorBuilder {
        private long averageOver = -1L;
    }

    public BoardRequest applyToRequest(BoardRequest r) {
        return r.analogRead(asAnalogRead());
    }

    public AnalogRead asAnalogRead() {
        return new AnalogRead(pin, voltage, resistance);
    }
}
