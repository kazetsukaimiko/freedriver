package io.freedriver.base.cli;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsoleTableTest {
    Person tom = new Person(24, "Tom Janker", "44 Fourty Drive");
    Person jack = new Person(43, "Jack Japal", "22 Toothvale Road");
    Person tim = new Person(10, "Tim Tank", "09 Safety pin bowling ball Alley");

    @Test
    public void testCKAlg() {
        int limit = 1000;
        List<Integer> collection = IntStream.range(0, limit)
                .mapToObj(i -> ((Number) Math.floor(i/2d)).intValue())
                .collect(Collectors.toList());

        assertTrue(collection.size() == limit);
        assertEquals(new HashSet<>(collection).size(), limit/2);
        assertTrue(collection.stream().noneMatch(i -> i > limit/2));

        collection.forEach(System.out::println);

    }

    @Test
    public void testTableRender() {
        List<Person> people = Arrays.asList(tom, jack, tim);
        ConsoleTable<Person> table = new ConsoleTable<>(Person.class)
                .addObjectColumn("Age", Person::getAge)
                .addObjectColumn("Name", Person::getName)
                .addObjectColumn("Address", Person::getAddress)
                .addObjectColumn("Class", Person::getClass);
        table.render(people, 2);

        table.renderKeyValue(people, "Name");
    }
}
