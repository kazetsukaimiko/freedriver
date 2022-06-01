package io.freedriver.base.util.phonetic;

import java.util.List;
import java.util.Set;

public interface PhoneticFlow {
    /**
     * Gets a string representation of the current phonetic.
     * @return The phonetic.
     */
    String getValue();

    /**
     * These letters can follow this one, given the current PhoneticBuilder buffer.
     * @param buffer The current buffer of phonetics.
     * @return A set of phonetics that can be placed after this.
     */
    Set<PhoneticFlow> nextCandidates(List<PhoneticFlow> buffer);
}
