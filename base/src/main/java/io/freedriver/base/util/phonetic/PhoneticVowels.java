package io.freedriver.base.util.phonetic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PhoneticVowels implements PhoneticFlow {
    A,
    E,
    I,
    O,
    U;

    @Override
    public String getValue() {
        return name();
    }

    @Override
    public Set<PhoneticFlow> nextCandidates(List<PhoneticFlow> buffer) {
        return Stream.concat(Stream.of(PhoneticVowels.values()), Stream.of(PhoneticConsonants.values()))
                .map(PhoneticFlow.class::cast)
                .collect(Collectors.toSet());
    }
}
