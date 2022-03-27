package io.freedriver.daly.bms.checksum.debug;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a step in calculating the CRC8 Checksum
 * Steps are recorded like this for transpar
 */
public class CRC8Step {
    private int start;
    private int component;
    private int end;

    public CRC8Step() {
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start & 0xFF;
    }

    public int getComponent() {
        return component;
    }

    public void setComponent(int component) {
        this.component = component  & 0xFF;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end & 0xFF;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CRC8Step crc8Step = (CRC8Step) o;
        return start == crc8Step.start && component == crc8Step.component && end == crc8Step.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, component, end);
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
