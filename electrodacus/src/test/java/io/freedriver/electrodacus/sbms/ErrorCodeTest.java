package io.freedriver.electrodacus.sbms;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorCodeTest {

    @Test
    public void testErrorCodes() {
        // Some happy path
        double none = 0d;
        int overVoltage = 1;

        assertTrue(ErrorCode.match(overVoltage).contains(ErrorCode.OV),
                "OV has least significant bit");
        assertTrue(ErrorCode.match(none).isEmpty(),
                "Zero should match nothing.");

        // Test that all codes match
        ErrorCode.stream()
                .forEach(errorCode -> assertTrue(ErrorCode.match(errorCode.getBit()).contains(errorCode),
                        asBinary(errorCode.getBit())+ " should match to " + errorCode));

        // Test codes can combine
        ErrorCode.stream()
                // Lets to all combinations of three codes.
                .forEach(first -> ErrorCode.stream()
                .forEach(second -> ErrorCode.stream()
                .forEach(third -> {
                    Set<ErrorCode> expected = Stream.of(first, second, third).collect(Collectors.toSet());

                    // Can we properly encode?
                    int expectedEncoding = first.getBit() | second.getBit() | third.getBit();
                    int resultEncoding = ErrorCode.encode(first, second, third);
                    assertEquals(expectedEncoding, resultEncoding,
                            "Encode collector should produce "
                                    + asBinary(expectedEncoding) + "; produced "
                            +asBinary(resultEncoding) + " instead");

                    Set<ErrorCode> results = ErrorCode.match(expectedEncoding);

                    // Can we properly decode?
                    assertEquals(expected.size(), results.size(),
                            "Same number of elements should return");
                    assertTrue(results.containsAll(expected),
                            asBinary(expectedEncoding)+ " should match to "
                    + expected.stream().map(Enum::name).collect(Collectors.joining(", ")));

                })));
    }

    public static String asBinary(int integer) {
        return Integer.toBinaryString(0xFFFF & integer);
    }
}
