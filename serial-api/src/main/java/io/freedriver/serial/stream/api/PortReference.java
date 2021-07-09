package io.freedriver.serial.stream.api;

import io.freedriver.serial.api.exception.SerialResourceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public interface PortReference {
    Path getCanonical();
    Set<Path> getAll();
    boolean isFile();
    boolean isDirectory();
    boolean overlaps(PortReference portReference);
    Instant getCreatedDate();

    default PortReference update() {
        return getAll()
                .stream()
                .findFirst()
                .map(PortReference::auto)
                .orElseThrow(() -> new IllegalArgumentException("Can't update bad PortReference"));
    }

    default PortReference newest(PortReference other) {
        return getCreatedDate().compareTo(other.getCreatedDate()) >= 0
                ? this
                : other;
    }

    default Set<Path> getLinks() {
        return getAll()
                .stream()
                .filter(Files::isSymbolicLink)
                .collect(Collectors.toSet());
    }

    default boolean isInvalid() {
        return getAll()
            .stream()
            .anyMatch(Files::notExists);
    }

    static PortReference auto(Path path) {
        try {
            return new StockPortReference(path);
        } catch (IOException e) {
            throw new SerialResourceException("Unable to get FileReference: ", e);
        }
    }


}
