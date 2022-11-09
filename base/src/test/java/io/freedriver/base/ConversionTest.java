package io.freedriver.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConversionTest {


    @Test
    public void testImplicitVersusExplicitConversion() {
        long sourceValue = 123L; // An original value
        // The C-equivalent of an implicit conversion that causes a warning.
        int implicit = (int) sourceValue;
        // The C-equivalent of an explicit conversion that does not.
        int explicit = Long.valueOf(sourceValue).intValue();
        // Fails if the conversions are not identical.
        assertEquals(implicit, explicit);
    }
}
