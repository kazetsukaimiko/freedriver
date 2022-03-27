package io.freedriver.victron;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class VEDirectColumnValue {
    private final VEDirectColumn column;
    private final String stringRepresentation;

    public VEDirectColumnValue(VEDirectColumn column, String stringRepresentation) {
        this.column = column;
        this.stringRepresentation = stringRepresentation;
    }

    public Object getValue() {
        return getColumn().getDefinition().parser().apply(stringRepresentation);
    }

    public VEDirectColumn getColumn() {
        return column;
    }

    public String getStringRepresentation() {
        return stringRepresentation;
    }

    public void apply(VEDirectMessage message) {
        getColumn().getDefinition()
                .apply(message, getStringRepresentation());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VEDirectColumnValue value = (VEDirectColumnValue) o;
        return column == value.column &&
                Objects.equals(stringRepresentation, value.stringRepresentation);
    }

    @Override
    public String toString() {
        return "VEDirectColumnValue{" +
                "column=" + column +
                ", stringRepresentation='" + stringRepresentation + '\'' +
                '}';
    }
}
