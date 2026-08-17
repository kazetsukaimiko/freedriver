package io.freedriver.base.cli;

import java.util.Optional;
import java.util.function.Function;

import lombok.Builder;

@Builder(toBuilder = true)
public record ConsoleColumn<E, T>(
        Class<E> entityKlazz,
        Class<T> fieldKlazz,
        String columnName,
        Function<E, T> columnFunction) {

    public static <X> ConsoleColumn<X, String> stringColumn(Class<X> entityKlazz, String columnName, Function<X, String> columnFunction) {
        return new ConsoleColumn<>(entityKlazz, String.class, columnName, columnFunction);
    }

    public Optional<T> apply(E entity) {
        return Optional.ofNullable(entity)
                .map(columnFunction);
    }
}
