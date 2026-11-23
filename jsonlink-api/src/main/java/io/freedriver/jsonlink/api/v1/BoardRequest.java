package io.freedriver.jsonlink.api.v1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * One request to a board. JSON property names are the component names. Each helper returns a
 * new request and leaves this one unchanged.
 */
public record BoardRequest(
        UUID uuid,
        UUID requestId,
        Map<Identifier, Mode> mode,
        ReadRequest read,
        WriteRequest write,
        Boolean boardInfo,
        List<Identifier> turn_off,
        List<Identifier> turn_on) {

    public BoardRequest {
        mode = mode == null ? Map.of() : Map.copyOf(mode);
        turn_off = turn_off == null ? List.of() : List.copyOf(turn_off);
        turn_on = turn_on == null ? List.of() : List.copyOf(turn_on);
    }

    public static BoardRequest empty() {
        return new BoardRequest(null, null, null, null, null, null, null, null);
    }

    public BoardRequest analogRead(AnalogRead... analogReads) {
        return analogRead(Stream.of(analogReads));
    }

    public BoardRequest analogRead(Stream<AnalogRead> analogReads) {
        ReadRequest next = read == null ? ReadRequest.empty() : read;
        for (AnalogRead analogRead : analogReads.toList()) {
            next = next.readAnalog(analogRead);
        }
        return withRead(next);
    }

    public BoardRequest modeSet(ModeSet... modes) {
        return modeSet(Stream.of(modes));
    }

    public BoardRequest modeSet(Stream<ModeSet> modes) {
        Map<Identifier, Mode> next = new LinkedHashMap<>(mode);
        modes.forEach(modeSet -> next.put(modeSet.pinNumber(), modeSet.mode()));
        return new BoardRequest(uuid, requestId, next, read, write, boardInfo, turn_off, turn_on);
    }

    public BoardRequest digitalRead(Identifier... pins) {
        return digitalRead(Stream.of(pins));
    }

    public BoardRequest digitalRead(Stream<Identifier> pins) {
        ReadRequest next = read == null ? ReadRequest.empty() : read;
        for (Identifier pin : pins.toList()) {
            next = next.readDigital(pin);
        }
        return withRead(next);
    }

    public BoardRequest digitalWrite(DigitalWrite... pinWrites) {
        return digitalWrite(Stream.of(pinWrites));
    }

    public BoardRequest digitalWrite(Stream<DigitalWrite> pinWrites) {
        WriteRequest next = write == null ? WriteRequest.empty() : write;
        for (DigitalWrite digitalWrite : pinWrites.toList()) {
            next = next.writeDigital(digitalWrite);
        }
        return new BoardRequest(uuid, requestId, mode, read, next, boardInfo, turn_off, turn_on);
    }

    public BoardRequest turnOn(Stream<Identifier> pins) {
        List<Identifier> next = new ArrayList<>(turn_on);
        pins.forEach(next::add);
        return new BoardRequest(uuid, requestId, mode, read, write, boardInfo, turn_off, next);
    }

    public BoardRequest turnOff(Stream<Identifier> pins) {
        List<Identifier> next = new ArrayList<>(turn_off);
        pins.forEach(next::add);
        return new BoardRequest(uuid, requestId, mode, read, write, boardInfo, next, turn_on);
    }

    public BoardRequest newUuid() {
        return new BoardRequest(UUID.randomUUID(), requestId, mode, read, write, boardInfo, turn_off, turn_on);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return (read == null || read.isEmpty())
                && (write == null || write.isEmpty())
                && mode.isEmpty()
                && turn_on.isEmpty()
                && turn_off.isEmpty();
    }

    private BoardRequest withRead(ReadRequest next) {
        return new BoardRequest(uuid, requestId, mode, next, write, boardInfo, turn_off, turn_on);
    }
}
