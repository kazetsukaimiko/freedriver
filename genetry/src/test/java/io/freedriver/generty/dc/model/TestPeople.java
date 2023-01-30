package io.freedriver.generty.dc.model;

import org.junit.jupiter.api.Test;

public class TestPeople {

    @Test
    public void example() {
        System.out.println("People are " + Person.getRace());

        Person fred = new Person("Fred", 36);

        System.out.println("Lets introduce fred:" + fred.introduceYourself());

    }
}
