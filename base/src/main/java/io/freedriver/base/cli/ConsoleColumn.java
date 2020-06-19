package io.freedriver.base.cli;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ConsoleColumn<E, T> {
    private Class<E> entityKlazz;
    private Class<T> fieldKlazz;
    private String columnName;
    private Function<E, T> columnFunction;

    public ConsoleColumn(Class<E> entityKlazz, Class<T> fieldKlazz, String columnName, Function<E, T> columnFunction) {
        this.entityKlazz = entityKlazz;
        this.fieldKlazz = fieldKlazz;
        this.columnName = columnName;
        this.columnFunction = columnFunction;
    }

    public static <X> ConsoleColumn<X, String> stringColumn(Class<X> entityKlazz, String columnName, Function<X, String> columnFunction) {
        return new ConsoleColumn<>(entityKlazz, String.class, columnName, columnFunction);
    }

    public Class<E> getEntityKlazz() {
        return entityKlazz;
    }

    public void setEntityKlazz(Class<E> entityKlazz) {
        this.entityKlazz = entityKlazz;
    }

    public Class<T> getFieldKlazz() {
        return fieldKlazz;
    }

    public void setFieldKlazz(Class<T> fieldKlazz) {
        this.fieldKlazz = fieldKlazz;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Function<E, T> getColumnFunction() {
        return columnFunction;
    }

    public void setColumnFunction(Function<E, T> columnFunction) {
        this.columnFunction = columnFunction;
    }

    public Optional<T> apply(E entity) {
        return Optional.ofNullable(entity)
                .map(columnFunction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsoleColumn<?, ?> that = (ConsoleColumn<?, ?>) o;
        return Objects.equals(entityKlazz, that.entityKlazz) &&
                Objects.equals(fieldKlazz, that.fieldKlazz) &&
                Objects.equals(columnName, that.columnName) &&
                Objects.equals(columnFunction, that.columnFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityKlazz, fieldKlazz, columnName, columnFunction);
    }
}
