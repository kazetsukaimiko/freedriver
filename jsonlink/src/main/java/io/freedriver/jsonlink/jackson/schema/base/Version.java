package io.freedriver.jsonlink.jackson.schema.base;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;

@Builder(toBuilder = true)
public record Version(int major, int minor, int micro) {
    public static final String DELIMITER = "\\.";
    public static final int LENGTH_PARTS = 3;

    @JsonCreator
    public Version(int[] parts) {
        this(parts[0], parts[1], parts[2]);
    }

    @JsonValue
    public int[] jsonValue() {
        return new int[] {major, minor, micro};
    }

    @Override
    public String toString() {
        return Stream.of(major, minor, micro)
                .map(String::valueOf)
                .collect(Collectors.joining("."));
    }
}
