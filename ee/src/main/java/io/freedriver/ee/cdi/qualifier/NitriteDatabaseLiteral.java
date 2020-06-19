package io.freedriver.ee.cdi.qualifier;

import javax.enterprise.util.AnnotationLiteral;

public class NitriteDatabaseLiteral extends AnnotationLiteral<NitriteDatabase> implements NitriteDatabase {
    private final String deployment;
    private final Class<?> database;

    public NitriteDatabaseLiteral() {
        this("", Object.class);
    }

    public NitriteDatabaseLiteral(String deployment, Class<?> database) {
        this.deployment = deployment;
        this.database = database;
    }

    @Override
    public String deployment() {
        return deployment;
    }

    @Override
    public Class<?> database() {
        return database;
    }
}
