package io.freedriver.electrodacus.sbms;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// Lets decode E1rrorCodes!
public enum ErrorCode {
    OV  (0b0000000000000001),
    OVLK(0b0000000000000010),
    UV  (0b0000000000000100),
    UVLK(0b0000000000001000),
    IOT (0b0000000000010000),
    COC (0b0000000000100000),
    DOC (0b0000000001000000),
    DSC (0b0000000010000000),
    CELF(0b0000000100000000),
    OPEN(0b0000001000000000),
    LVC (0b0000010000000000),
    ECCF(0b0000100000000000),
    CFET(0b0001000000000000),
    EOC (0b0010000000000000),
    DFET(0b0100000000000000);

    private final int bit;

    ErrorCode(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }

    // Predicate method to match this enun against error code numeric representation
    public boolean matches(Number allErrors) {
        return (allErrors.intValue() & bit) == bit;
    }

    /*
     * Encode a collection/array/iterable of errorCodes into their numeric representation.
     */
    public static int encode(ErrorCode... errorCodes) {
        return Stream.of(errorCodes).collect(ErrorCode.encoding());
    }

    public static int encode(Iterable<ErrorCode> errorCodes) {
        return StreamSupport.stream(errorCodes.spliterator(), false)
                .collect(ErrorCode.encoding());
    }

    public static int encode(Collection<ErrorCode> errorCodes) {
        return errorCodes.stream()
                .collect(ErrorCode.encoding());
    }

    // Stream collector to turn a Stream<ErrorCode> into its numeric representation.
    public static Collector<ErrorCode, int[], Integer> encoding() {
        return new Collector<ErrorCode, int[], Integer>() {
            //private int value = 0;
            @Override
            public Supplier<int[]> supplier() {
                return () -> new int[1];
            }

            @Override
            public BiConsumer<int[], ErrorCode> accumulator() {
                return (a, t) -> {
                    a[0] |= t.getBit();
                };
            }

            @Override
            public BinaryOperator<int[]> combiner() {
                return (a, b) -> {
                    a[0] |= b[0];
                    return a;
                };
            }

            @Override
            public Function<int[], Integer> finisher() {
                return (i) -> i[0];
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

    /*
     * Decode a numeric errorcode representation into a Set of ErrorCode enum values.
     */
    public static Set<ErrorCode> match(Number allErrors) {
        return stream()
                .filter(errorCode -> errorCode.matches(allErrors.intValue()))
                .collect(Collectors.toSet());
    }

    public static Stream<ErrorCode> stream() {
        return Stream.of(values());
    }
}
