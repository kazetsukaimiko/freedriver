package io.freedriver.serial.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SynchronizedListBacked {
    private final List<Byte> written = new ArrayList<>();
    protected synchronized <T> T doWith(Function<List<Byte>, T> listFunction) {
        return listFunction.apply(written);
    }
    protected int size() {
        return doWith(List::size);
    }
    protected boolean isEmpty() {
        return doWith(List::isEmpty);
    }
}
