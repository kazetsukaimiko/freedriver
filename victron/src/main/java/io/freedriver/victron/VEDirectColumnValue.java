package io.freedriver.victron;

import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record VEDirectColumnValue(VEDirectColumn column, String stringRepresentation) {

    public Object value() {
        return column().getDefinition().parser().apply(stringRepresentation);
    }

    public VEDirectMessage apply(VEDirectMessage message) {
        return column().getDefinition()
                .apply(message, stringRepresentation());
    }

    public static Optional<VEDirectColumnValue> fromSerial(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length == 2) {
            return VEDirectColumn.byColumnName(parts[0])
                    .map(veDirectColumn -> new VEDirectColumnValue(veDirectColumn, parts[1]));
        } else {
            return Optional.empty();
        }
    }

    public String toSerialLine() {
        return column.getColumnName() + "    " + stringRepresentation;
    }
}
