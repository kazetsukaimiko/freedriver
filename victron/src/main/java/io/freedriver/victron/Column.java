package io.freedriver.victron;

import io.freedriver.math.measurement.types.Measurement;
import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Energy;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.electrical.Power;
import io.freedriver.math.number.ScaledNumber;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Interface to assist in mapping VE.Direct lines to VEDirectColumnValues.
 * @param <T> The type of the field on VEDirectMessage.
 */
public interface Column<T> {
    /**
     * Method used to apply the String value to the VEDirectMessage, transforming it to a T using the parser
     * and applying the value with mapper.
     * @param message The object to modify.
     * @param value The value to apply, string form, pre-transform.
     */
    default void apply(VEDirectMessage message, String value) {
        mapper().accept(message, parser().apply(value));
    }

    Function<String, T> parser();
    BiConsumer<VEDirectMessage, T> mapper();
    Function<VEDirectMessage, T> getter();

    /**
     * No-op Column. For known fields that are not yet supported, or, unknown fields.
     * @return A Column that does nothing.
     */
    static Column<?> doNothing() {
        return Column.of(o -> null, (msg, o) -> {}, msg -> null);
    }

    /**
     * Column that applies a String field.
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known String value.
     */
    static Column<String> string(BiConsumer<VEDirectMessage, String> mapper, Function<VEDirectMessage, String> getter) {
        return of(Function.identity(), mapper, getter);
    }

    /**
     * Energy Column.
     * @param scaler The Logic to transform a String into Energy.
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known Energy value.
     */
    static Column<Energy> energy(Function<ScaledNumber, Energy> scaler, BiConsumer<VEDirectMessage, Energy> mapper, Function<VEDirectMessage, Energy> getter) {
        return normalized(scaler, mapper, getter);
    }

    /**
     * Wattage Column.
     * @param scaler The Logic to transform a String into Power.
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known Power value.
     */
    static Column<Power> wattage(Function<ScaledNumber, Power> scaler, BiConsumer<VEDirectMessage, Power> mapper, Function<VEDirectMessage, Power> getter) {
        return normalized(scaler, mapper, getter);
    }

    /**
     * Current Column.
     * @param scaler The Logic to transform a String into Current.
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known Current value.
     */
    static Column<Current> amperage(Function<ScaledNumber, Current> scaler, BiConsumer<VEDirectMessage, Current> mapper, Function<VEDirectMessage, Current> getter) {
        return normalized(scaler, mapper, getter);
    }

    /**
     * Potential Column.
     * @param scaler The Logic to transform a String into Potential.
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known Potential value.
     */
    static Column<Potential> voltage(Function<ScaledNumber, Potential> scaler, BiConsumer<VEDirectMessage, Potential> mapper, Function<VEDirectMessage, Potential> getter) {
        return normalized(scaler, mapper, getter);
    }

    /**
     * Power Column.
     * @param scaler The Logic to transform a String into Power.
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known Power value.
     */
    static Column<Power> power(Function<ScaledNumber, Power> scaler, BiConsumer<VEDirectMessage, Power> mapper, Function<VEDirectMessage, Power> getter) {
        return normalized(scaler, mapper, getter);
    }

    /**
     * Normalized Measurement Column.
     * @param scaler The Logic to transform a String into Normalized Measurement.
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known Measurement (normalized) value.
     */

    static <M extends Measurement<M>> Column<M> normalized(Function<ScaledNumber, M> scaler, BiConsumer<VEDirectMessage, M> mapper, Function<VEDirectMessage, M> getter) {
        return decimal(scaler, mapper, getter);
    }

    /**
     * Enum Column.
     * @param enumFinder The Optional mapper for String -> Enum
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known StateOfOperation value.
     */
    static <E extends Enum<E>> Column<E> enumOptional(Function<String, Optional<E>> enumFinder, BiConsumer<VEDirectMessage, E> mapper, Function<VEDirectMessage, E> getter) {
        return optional(enumFinder, mapper, getter);
    }

    /**
     * Enum Column.
     * @param enumFinder The Optional mapper for String -> Enum
     * @param mapper The Setter on VEDirectMessage.
     * @return A Column that maps a known StateOfOperation value.
     */
    static <E extends Enum<E>> Column<E> enumByCodeOptional(Function<Integer, Optional<E>> enumFinder, BiConsumer<VEDirectMessage, E> mapper, Function<VEDirectMessage, E> getter) {
        return optional(s -> enumFinder.apply(s.startsWith("0x") ?  Integer.parseInt(s.substring(2), 16) : Integer.parseInt(s, 16)), mapper, getter);
    }

    /**
     * Optional Column.
     * @param parser The Logic to transform a String into T.
     * @param mapper The Setter on VEDirectMessage.
     * @param <T> The type of the field on VEDirectMessage.
     * @return
     */
    static <T> Column<T> optional(Function<String, Optional<T>> parser, BiConsumer<VEDirectMessage, T> mapper, Function<VEDirectMessage, T> getter) {
        return of(s -> parser.apply(s).orElse(null), mapper, getter);
    }

    /**
     * Decimal Column.
     * @param parser The Logic to transform a String into T.
     * @param mapper The Setter on VEDirectMessage.
     * @param <T> The type of the field on VEDirectMessage.
     * @return
     */
    static <T> Column<T> decimal(Function<ScaledNumber, T> parser, BiConsumer<VEDirectMessage, T> mapper, Function<VEDirectMessage, T> getter) {
        return of(s -> parser.apply(ScaledNumber.of(new BigDecimal(s))), mapper, getter);
    }

    /**
     * Custom Column.
     * @param parser The Logic to transform a String into T.
     * @param mapper The Setter on VEDirectMessage.
     * @param <T> The type of the field on VEDirectMessage.
     * @return
     */
    static <T> Column<T> of(Function<String, T> parser, BiConsumer<VEDirectMessage, T> mapper, Function<VEDirectMessage, T> getter) {
        return new Column<>() {
            @Override
            public Function<String, T> parser() {
                return parser;
            }

            @Override
            public BiConsumer<VEDirectMessage, T> mapper() {
                return mapper;
            }

            @Override
            public Function<VEDirectMessage, T> getter() {
                return getter;
            }
        };
    }
}
