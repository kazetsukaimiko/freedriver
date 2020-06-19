package io.freedriver.base.cli;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ConsoleTableTest {
    Person tom = new Person(24, "Tom Janker", "44 Fourty Drive");
    Person jack = new Person(43, "Jack Japal", "22 Toothvale Road");
    Person tim = new Person(10, "Tim Tank", "09 Safety pin bowling ball Alley");

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
