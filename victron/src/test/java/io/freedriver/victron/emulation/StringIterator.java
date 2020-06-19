package io.freedriver.victron.emulation;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class StringIterator implements Iterator<String> {
    private final List<String> split;
    private int pos = 0;

    public StringIterator(String string) {
        this.split = string.codePoints()
            .mapToObj(cp -> new StringBuilder().appendCodePoint(cp))
            .map(StringBuilder::toString)
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasNext() {
        return pos < split.size();
    }

    @Override
    public String next() {
        return split.get(pos++);
    }
}
