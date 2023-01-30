package io.freedriver.generty.dc.model;

public class Person {
    public static final String RACE = "Human";
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String  introduceYourself() {
        return "Hello, my name is " + name + ", I am " + age + " years old.";
    }

    public static String getRace() {
        return RACE;
    }




}
