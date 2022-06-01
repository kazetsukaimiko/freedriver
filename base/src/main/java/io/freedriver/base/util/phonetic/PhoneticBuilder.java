package io.freedriver.base.util.phonetic;

import java.util.List;
import java.util.stream.Collectors;

// Class to build fake names and words. Useful for testing.
public class PhoneticBuilder {
    private final int desiredLength;
    private final List<PhoneticFlow> buffer;

    public PhoneticBuilder(int desiredLength, List<PhoneticFlow> buffer) {
        this.desiredLength = desiredLength;
        this.buffer = buffer;
    }

    public int getDesiredLength() {
        return desiredLength;
    }

    public List<PhoneticFlow> getBuffer() {
        return buffer;
    }

    public static String build(List<PhoneticFlow> flows) {
        return flows != null
            ? flows.stream().map(PhoneticFlow::getValue).collect(Collectors.joining(""))
            : null;
    }
}
