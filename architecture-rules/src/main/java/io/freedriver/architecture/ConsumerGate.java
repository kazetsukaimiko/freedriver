package io.freedriver.architecture;

import java.nio.file.Path;
import java.util.List;

/**
 * What a consuming repository names for one run of the gate: the reactor root, the ordered utility
 * artifact ids, and the group id prefix the root must match.
 *
 * <p>The utility list is ordered because rule descriptions print it. Two lists with the same ids
 * in a different order are different gates.
 *
 * @param reactorRoot the consuming build's reactor root
 * @param utilityArtifacts artifact ids of this repository's utility modules, in description order
 * @param groupPrefix a group id the root must equal, or a prefix it must start with followed by a dot
 */
record ConsumerGate(Path reactorRoot, List<String> utilityArtifacts, String groupPrefix) {

    public ConsumerGate {
        if (reactorRoot == null) {
            throw new IllegalArgumentException("reactorRoot is required");
        }
        if (utilityArtifacts == null) {
            throw new IllegalArgumentException("utilityArtifacts is required");
        }
        if (groupPrefix == null || groupPrefix.isBlank()) {
            throw new IllegalArgumentException("groupPrefix is required");
        }
        reactorRoot = reactorRoot.toAbsolutePath().normalize();
        utilityArtifacts = List.copyOf(utilityArtifacts);
        groupPrefix = groupPrefix.trim();
    }
}
