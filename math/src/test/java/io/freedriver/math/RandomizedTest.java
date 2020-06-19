package io.freedriver.math;

import java.math.BigDecimal;
import java.util.Random;

public class RandomizedTest extends LoggedTest {
    public static final Random RANDOM = new Random(System.currentTimeMillis());


    public BigDecimal randomBigDecimal() {
        return randomBigDecimal(null, null);
    }

    public BigDecimal randomBigDecimal(BigDecimal lower, BigDecimal upper) {
        if (lower != null && upper != null) {
            if (lower.compareTo(upper) < 0) {
                BigDecimal range = upper.subtract(lower);
                if (range.scale()>0) {
                    BigDecimal scale = new BigDecimal(range.scale());
                    BigDecimal bound = range.multiply(scale);
                    int intValue = RANDOM.nextInt(bound.toBigInteger().intValue());
                    return lower.add(new BigDecimal(intValue).multiply(scale));
                } else {
                    int intValue = RANDOM.nextInt(range.toBigInteger().intValue());
                    return lower.add(new BigDecimal(intValue));
                }
            } else if (lower.compareTo(upper) == 0) {
                return upper;
            } else {
                return randomBigDecimal(upper, lower);
            }
        }
        return randomBigDecimal(
                lower != null ? lower : BigDecimal.ZERO,
                upper != null ? upper : new BigDecimal(String.valueOf(Integer.MAX_VALUE)));
    }

    public int randomInt() {
        return RANDOM.nextInt();
    }

    public long randomLong() {
        return RANDOM.nextLong();
    }

    public double randomDouble() {
        return RANDOM.nextDouble();
    }
}
