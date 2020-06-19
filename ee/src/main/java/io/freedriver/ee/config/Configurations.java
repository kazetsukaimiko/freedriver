package io.freedriver.ee.config;

public abstract class Configurations<C extends Configuration> {
    abstract String deployment();
    abstract Class<C> configKlazz();
}
