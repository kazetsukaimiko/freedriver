package io.freedriver.inotify;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InotifyListenerTest {
    @Test
    void discoversRegisteredProviders() {
        assertTrue(
                InotifyListener.load().anyMatch(TestInotifyListener.class::isInstance),
                "expected TestInotifyListener from META-INF/services");
    }
}