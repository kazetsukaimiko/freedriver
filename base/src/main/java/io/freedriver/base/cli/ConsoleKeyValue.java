package io.freedriver.base.cli;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record ConsoleKeyValue(String key, List<String> values) {

    public ConsoleKeyValue {
        if (values == null) {
            values = new ArrayList<>();
        }
    }

    public ConsoleKeyValue(String key) {
        this(key, new ArrayList<>());
    }
}
