package io.freedriver.jsonlink.config.v2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.ToDoubleBiFunction;

public enum SensorModes implements Factorable {
    LINEAR((in, factor) -> in), // noop
    POWER_OF(SensorModes::power)
    ;

    private final ToDoubleBiFunction<Double, Double> toDoubleBiFunction;

    SensorModes(ToDoubleBiFunction<Double, Double> toDoubleBiFunction) {
        this.toDoubleBiFunction = toDoubleBiFunction;
    }

    @Override
    public double realPercent(double base, double factor) {
        return toDoubleBiFunction.applyAsDouble(base, factor);
    }

    public static double power(double pct, double pow) {
        return power(pct, Double.valueOf(pow).intValue());
    }

    public static double power(double pct, int pow) {
        BigDecimal hundred = BigDecimal.valueOf(100d);
        BigDecimal max = hundred.pow(pow);
        return BigDecimal.valueOf(pct)
                .pow(pow)
                .divide(max, RoundingMode.HALF_UP)
                .multiply(hundred)
                .doubleValue();
    }
}
