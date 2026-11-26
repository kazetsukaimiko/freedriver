package io.freedriver.jsonlink.api.v1;

import java.util.List;

/** The digital and analog pins a board reports. */
public record BoardInfo(List<Identifier> digitals, List<Identifier> analogs) {
    public BoardInfo {
        digitals = digitals == null ? List.of() : List.copyOf(digitals);
        analogs = analogs == null ? List.of() : List.copyOf(analogs);
    }
}
