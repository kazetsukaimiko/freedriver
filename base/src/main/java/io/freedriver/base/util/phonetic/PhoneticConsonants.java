package io.freedriver.base.util.phonetic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PhoneticConsonants implements PhoneticFlow {
    B,
    C,
    D,
    F,
    G,
    H,
    J,
    K,
    L,
    M,
    N,
    P,
    Q,
    R,
    S,
    T,
    V,
    W,
    X,
    Y,
    Z;


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
