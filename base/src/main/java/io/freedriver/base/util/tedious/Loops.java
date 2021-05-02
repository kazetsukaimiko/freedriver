package io.freedriver.base.util.tedious;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Wanted a place to handle smelly loops in a more generic way
 */
public class Loops {

    public static void takeUntil(Supplier<Boolean> supplier, Duration duration, Function<Throwable, Boolean> onThrow) {
        boolean keepGoing = true;
        while (keepGoing) {
            try {
                keepGoing = supplier.get();
                wait(duration);
            } catch (Throwable t) {
                keepGoing = onThrow.apply(t);
            }
        }
    }

    public static void wait(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
