package io.freedriver.ee.cdi.qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface NitriteDatabase {
    @Nonbinding
    String deployment() default "";
    @Nonbinding
    Class<?> database() default Object.class;

    final class Literal extends AnnotationLiteral<NitriteDatabase> implements NitriteDatabase {
        private final String deployment;
        private final Class<?> database;

        public Literal(String deployment, Class<?> database) {
            this.deployment = deployment;
            this.database = database;
        }

        public String deployment() {
            return deployment;
        }

        public Class<?> database() {
            return database;
        }
    }
}
