package io.freedriver.jsonlink.jackson.schema.v1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.freedriver.jsonlink.Connector;
import lombok.Builder;

@Builder(toBuilder = true)
public record Request(
        UUID uuid,
        UUID requestId,
        Map<Identifier, Mode> mode,
        ReadRequest read,
        WriteRequest write,
        Boolean boardInfo,
        List<Identifier> turn_off,
        List<Identifier> turn_on) {

    public Request {
        mode = mode == null ? Map.of() : Map.copyOf(mode);
        turn_off = turn_off == null ? List.of() : List.copyOf(turn_off);
        turn_on = turn_on == null ? List.of() : List.copyOf(turn_on);
    }

    public static Request empty() {
        return Request.builder().build();
    }

    public Request analogRead(AnalogRead... analogReads) {
        return analogRead(Stream.of(analogReads));
    }

    public Request analogRead(Stream<AnalogRead> analogReads) {
        ReadRequest next = read == null ? ReadRequest.empty() : read;
        for (AnalogRead analogRead : analogReads.toList()) {
            next = next.readAnalog(analogRead);
        }
        return toBuilder().read(next).build();
    }

    public Request modeSet(ModeSet... modes) {
        return modeSet(Stream.of(modes));
    }

    public Request modeSet(Stream<ModeSet> modes) {
        Map<Identifier, Mode> next = new LinkedHashMap<>(mode);
        modes.forEach(modeSet -> next.put(modeSet.pinNumber(), modeSet.mode()));
        return toBuilder().mode(next).build();
    }

    public Request digitalRead(Identifier... pins) {
        return digitalRead(Stream.of(pins));
    }

    public Request digitalRead(Stream<Identifier> pins) {
        ReadRequest next = read == null ? ReadRequest.empty() : read;
        for (Identifier pin : pins.toList()) {
            next = next.readDigital(pin);
        }
        return toBuilder().read(next).build();
    }

    public Request digitalWrite(DigitalWrite... pinWrites) {
        return digitalWrite(Stream.of(pinWrites));
    }

    public Request digitalWrite(Stream<DigitalWrite> pinWrite) {
        WriteRequest next = write == null ? WriteRequest.empty() : write;
        for (DigitalWrite digitalWrite : pinWrite.toList()) {
            next = next.writeDigital(digitalWrite);
        }
        return toBuilder().write(next).build();
    }

    public Request turnOn(Stream<Identifier> pins) {
        List<Identifier> next = new ArrayList<>(turn_on);
        pins.forEach(next::add);
        return toBuilder().turn_on(next).build();
    }

    public Request turnOff(Stream<Identifier> pins) {
        List<Identifier> next = new ArrayList<>(turn_off);
        pins.forEach(next::add);
        return toBuilder().turn_off(next).build();
    }

    public Request newUuid() {
        return toBuilder().uuid(UUID.randomUUID()).build();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return (read == null || read.isEmpty())
                && (write == null || write.isEmpty())
                && mode.isEmpty()
                && turn_on.isEmpty()
                && turn_off.isEmpty();
    }

    public Response invoke(Connector connector) {
        return connector.send(this);
    }
}
