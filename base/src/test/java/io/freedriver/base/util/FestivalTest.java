package io.freedriver.base.util;

import io.freedriver.base.Tests;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tests.Integration)
public class FestivalTest {
    @Test
    public void testFestival() {
        Festival.speak("Hello friend");
    }
}
