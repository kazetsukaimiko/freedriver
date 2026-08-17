package io.freedriver.daly.bms.checksum.debug;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.Builder;

/**
 * Represents a step in calculating the CRC8 Checksum
 * Steps are recorded like this for transpar
 */
@Builder(toBuilder = true)
public record CRC8Step(int start, int component, int end) {
    public CRC8Step {
        start = start & 0xFF;
        component = component & 0xFF;
        end = end & 0xFF;
    }

    @Override
    public String toString() {
        return Stream.of(start, component, end)
                .map(CRC8Step::representation)
                .collect(Collectors.joining(":"));
    }

    public static String representation(int number) {
        String binString = Integer.toBinaryString(number);

        return binString.length() > 8
                ?binString.substring(binString.length()-8)
                : binString.length() < 8
                ? zeroPad(binString) : binString;
    }

    private static String zeroPad(String binString) {
        return IntStream.range(0, 8-binString.length())
                .map(i -> 0)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining()) + binString;
    }

}
