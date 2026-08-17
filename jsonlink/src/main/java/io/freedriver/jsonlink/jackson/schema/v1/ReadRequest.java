package io.freedriver.jsonlink.jackson.schema.v1;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Builder;

@Builder(toBuilder = true)
public record ReadRequest(Set<Identifier> digital, Set<AnalogRead> analog) {
    public ReadRequest {
        digital = digital == null ? Set.of() : Set.copyOf(digital);
        analog = analog == null ? Set.of() : Set.copyOf(analog);
    }

    public static ReadRequest empty() {
        return new ReadRequest(Set.of(), Set.of());
    }

    public ReadRequest readDigital(Identifier pinNumber) {
        Set<Identifier> next = new LinkedHashSet<>(digital);
        next.add(pinNumber);
        return toBuilder().digital(next).build();
    }

    public ReadRequest readAnalog(Identifier pinNumber, float voltage, float resistance) {
        return readAnalog(new AnalogRead(pinNumber, voltage, resistance));
    }

    public ReadRequest readAnalog(AnalogRead analogRead) {
        Set<AnalogRead> next = new LinkedHashSet<>(analog);
        next.add(analogRead);
        return toBuilder().analog(next).build();
    }

    public boolean isEmpty() {
        return digital.isEmpty() && analog.isEmpty();
    }
}
