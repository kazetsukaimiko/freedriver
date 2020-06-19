package io.freedriver.math.number;

import io.freedriver.math.UnitPrefix;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScaledNumberTest {

    @Test
    public void testRounding() {
        stream()
                .forEach(value -> UnitPrefix.stream().map(unit -> new ScaledNumber(value, unit))
                    .forEach(scaledNumber -> {
                        assertEquals(value, scaledNumber.getValue(),
                                "Should preserve original value");
                        assertEquals(value.setScale(scaledNumber.getScale(), RoundingMode.HALF_UP)
                                .stripTrailingZeros(), scaledNumber.round().getValue(),
                                "Should round to scale half up.");
                        assertEquals(scaledNumber, scaledNumber.round(),
                                "Rounding mode doesn't effect equivalence");
                        assertEquals(0, scaledNumber.compareTo(scaledNumber),
                                "Rounding doesn't effect comparison");
                }));
    }




    public static Stream<BigDecimal> stream() {
        return Stream.of(
                BigDecimal.ZERO,
                BigDecimal.ONE,
                BigDecimal.TEN,
                new BigDecimal("0.34453"),
                new BigDecimal("10293123.131131")
        );
    }
}
