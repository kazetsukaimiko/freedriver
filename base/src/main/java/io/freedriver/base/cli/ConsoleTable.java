package io.freedriver.base.cli;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ConsoleTable<E> {
    private Class<E> entityKlazz;
    private List<ConsoleColumn<E, String>> columns = new ArrayList<>();

    public ConsoleTable(Class<E> entityKlazz) {
        this.entityKlazz = entityKlazz;
    }

    public ConsoleTable<E> addColumn(String columnName, Function<E, String> function) {
        columns.add(ConsoleColumn.stringColumn(entityKlazz, columnName, e -> Optional.ofNullable(e).map(function).orElse("")));
        return this;
    }

    public <T> ConsoleTable<E> addObjectColumn(String columnName, Function<E, T> function) {
        return addColumn(columnName, e -> Optional.ofNullable(e).map(function).map(String::valueOf).orElse(null));
    }

    public void renderKeyValue(List<E> entities, String idColumn) {
        ConsoleTable<ConsoleKeyValue> pivot = new ConsoleTable<>(ConsoleKeyValue.class);
        pivot.addObjectColumn(idColumn, ConsoleKeyValue::getKey);

        Map<ConsoleColumn<E, String>, ConsoleKeyValue> kvm = new LinkedHashMap<>();

        for (int i=0; i<entities.size(); i++) { // Make a new columnedef
            entityColumn(pivot, idColumnOf(idColumn), i, entities.get(i));
            for (ConsoleColumn<E, String> column : columns) {
                if (!kvm.containsKey(column)) {
                    kvm.put(column, new ConsoleKeyValue(column.getColumnName()));
                }
                kvm.get(column).getValues().add(column.getColumnFunction().apply(entities.get(i)));
            }
        }

        pivot.render(kvm.values().stream().filter(consoleKeyValue -> !Objects.equals(idColumn, consoleKeyValue.getKey())).collect(Collectors.toList()));
    }

    private Function<E, String> idColumnOf(String columnName) {
        return columns
                .stream()
                .filter(column -> Objects.equals(columnName, column.getColumnName()))
                .findFirst()
                .map(ConsoleColumn::getColumnFunction)
                .orElse(t -> "");
    }

    private static <T> void entityColumn(final ConsoleTable<ConsoleKeyValue> pivot, Function<T, String> columnFunction, final int index, T entity) {
        pivot.addObjectColumn(columnFunction.apply(entity), consoleKeyValue -> consoleKeyValue.getValues().get(index));
    }

    public void render(List<E> entities) {
        render(entities, columns.size());
    }

    public List<List<ConsoleColumn<E, String>>> fromLimit(int columnLimit) {
        List<List<ConsoleColumn<E, String>>> parts = new ArrayList<>();
        if (columnLimit>=columns.size()) {
            parts.add(columns);
        } else {
            List<ConsoleColumn<E, String>> container = new ArrayList<>();
            for(ConsoleColumn<E, String> column : columns) {
                if (container.size()>=columnLimit) {
                    parts.add(container);
                    container = new ArrayList<>();
                }
                container.add(column);
            }
            if (!container.isEmpty()) {
                parts.add(container);
            }
        }
        return parts;
    }

    public void render(List<E> entities, int columnLimit) {
        for (List<ConsoleColumn<E, String>> container : fromLimit(columnLimit)) {
            Map<ConsoleColumn<E, String>, Integer> maxColSize = new LinkedHashMap<>();
            entities.forEach(entity -> setMaxes(container, maxColSize, entity));

            List<String> rows = new ArrayList<>();
            rows.add(header(maxColSize));
            entities.forEach(entity -> {
                rows.add(row(maxColSize, entity));
            });
            clear();

            rows.stream()
                    .flatMap(line -> Stream.of(line.split("\n")))
                    .mapToInt(String::length)
                    .max()
                    .ifPresent(maxLineLength -> {
                        String divider = divider(maxColSize, maxLineLength);
                        System.out.println(divider + String.join(divider, rows) + divider);
                    });
        }
    }

    public int lineSize(List<String> parts) {
        return parts.stream().mapToInt(String::length).sum();
    }

    public String line(int index, List<String> parts, String delimiter) {
        int maxSize = lineSize(parts);
        int j = 0;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<index; i++) {
            sb = new StringBuilder();
            while (sb.length()<=maxSize && j<parts.size()) {
                sb.append(delimiter);
                sb.append(parts.get(j++));
            }
        }
        return sb.toString();
    }

    public String cap(String inputLine, String delimiter, int maxLength) {
        StringBuilder sb = new StringBuilder(inputLine);
        while (sb.length()<maxLength) {
            sb.append(" ");
        }
        sb.append(delimiter);
        return sb.toString();
    }

    public String join(List<String> parts, String delimiter) {
        return "|" + String.join(delimiter, parts) + "|";

    }
    public String row(Map<ConsoleColumn<E, String>, Integer> sizes, E entity) {
        List<String> parts = new ArrayList<>();
        sizes.forEach((col, size) -> parts.add(valueFormatted(size, col.apply(entity).orElse(""))));
        return join(parts, "|");
    }

    public String header(Map<ConsoleColumn<E, String>, Integer> sizes) {
        List<String> parts = new ArrayList<>();
        sizes.forEach((col, size) -> parts.add(valueFormatted(size, col.getColumnName())));
        return join(parts, "|");
    }

    public String divider(Map<ConsoleColumn<E, String>, Integer> sizes, int maxLineLength) {
        return "\n"+sizes.values().stream()
                .map(this::dividerFormatter)
                .reduce(new StringBuilder(), (sb, p) -> {
                    if (sb.length()+1<maxLineLength) {
                        sb.append("+");
                        sb.append(p);
                    }
                    return sb;
                }, (a, b) -> b).toString()+"+\n";
    }

    public String dividerFormatter(Integer size) {
        return IntStream.range(0, size+2)
                .mapToObj(i -> "-")
                .collect(Collectors.joining(""));
    }

    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public String valueFormatted(Integer size, String value) {
        return String.format(" %-"+size+"s ", value);
    }

    private void setMaxes(List<ConsoleColumn<E, String>> columns, Map<ConsoleColumn<E, String>, Integer> maxColSize, E entity) {
        columns.forEach(column -> {
                   if (!maxColSize.containsKey(column)) {
                       maxColSize.put(column, column.getColumnName().length());
                   }
                   String value = column.apply(entity).orElse("");
                   if (maxColSize.get(column) < value.length()) {
                       maxColSize.put(column, value.length());
                   }
                });
    }

}
