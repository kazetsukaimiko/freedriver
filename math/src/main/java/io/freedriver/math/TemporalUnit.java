package io.freedriver.math;

import io.freedriver.math.number.Scaled;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public enum TemporalUnit implements Scaled<TemporalUnit, Duration> {
    NANOS("Ns", "Nanoseconds", ChronoUnit.NANOS),
    MILLIS("ms", "Milliseconds", ChronoUnit.MILLIS),
    SECONDS("s", "Seconds", ChronoUnit.SECONDS),
    MINUTES("m", "Minutes", ChronoUnit.MINUTES),
    HOURS("h", "Hours", ChronoUnit.HOURS);

    private static Comparator<TemporalUnit> temporalUnitComparator;

    private final String suffix;
    private final String name;
    private final ChronoUnit chronoUnit;

    TemporalUnit(String suffix, String name, ChronoUnit chronoUnit) {
        this.suffix = suffix;
        this.name = name;
        this.chronoUnit = chronoUnit;
    }

    @Override
    public Duration getValue() {
        return chronoUnit.getDuration();
    }

    @Override
    public int scaleCompareTo(TemporalUnit temporalUnit) {
        return Comparator.comparing(tU -> temporalUnit
                .getChronoUnit()
                .getDuration()
                .toNanos())
                .compare(this, temporalUnit);
    }

    public String getSuffix() {
        return suffix;
    }

    public String getName() {
        return name;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }

    public static Stream<TemporalUnit> stream() {
        return Stream.of(values());
    }

    public static Optional<TemporalUnit> bySuffix(String suffix) {
        return stream()
                .filter(temporalUnit -> Objects.equals(suffix, temporalUnit.suffix))
                .findFirst();
    }

    @Override
    public TemporalUnit me() {
        return this;
    }

    @Override
    public TemporalUnit up() {
        return hasUp()
                ? stream()
                    .filter(other -> other.isHigherScaleThan(this))
                    .min(TemporalUnit::scaleCompareTo)
                    .orElse(this)
                : this;
    }

    @Override
    public TemporalUnit down() {
        return hasUp()
                ? stream()
                .filter(other -> other.isLowerScaleThan(this))
                .max(TemporalUnit::scaleCompareTo)
                .orElse(this)
                : this;
    }

    @Override
    public boolean isLowerScaleThan(TemporalUnit temporalUnit) {
        return scaleCompareTo(temporalUnit) < 0;
    }

    @Override
    public boolean isHigherScaleThan(TemporalUnit temporalUnit) {
        return scaleCompareTo(temporalUnit) > 0;
    }

    @Override
    public boolean hasUp() {
        return stream()
                .anyMatch(other -> other.isHigherScaleThan(this));
    }

    @Override
    public boolean hasDown() {
        return stream()
                .anyMatch(other -> other.isLowerScaleThan(this));
    }

    public static TemporalUnit largest() {
        return Stream.of(values())
                .max(TemporalUnit::scaleCompareTo)
                .orElseThrow(() -> new RuntimeException("No max!"));
    }

    public static TemporalUnit smallest() {
        return Stream.of(values())
                .max(TemporalUnit::scaleCompareTo)
                .orElseThrow(() -> new RuntimeException("No min!"));
    }

    // 1h -> 3600s = target * 3600
    public BigDecimal scale(BigDecimal value, TemporalUnit target) {
        BigDecimal nanos = BigDecimal.valueOf(getChronoUnit().getDuration().toNanos());
        BigDecimal targetNanos = BigDecimal.valueOf(target.getChronoUnit().getDuration().toNanos());
        BigDecimal expanded = value.multiply(nanos);
        BigDecimal divided = expanded.divide(targetNanos, 100, RoundingMode.HALF_UP);
        BigDecimal scaled = divided.setScale(10, RoundingMode.HALF_UP);
        BigDecimal result = new BigDecimal(scaled.stripTrailingZeros().toPlainString());
        return result;
    }


}
