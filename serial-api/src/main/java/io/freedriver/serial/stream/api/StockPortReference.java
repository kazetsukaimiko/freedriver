package io.freedriver.serial.stream.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StockPortReference implements PortReference {
    private final Path canonical;
    private final List<Path> links = new ArrayList<>();
    private Instant createdOn;

    public StockPortReference(Path auto) throws IOException {
        this.canonical = resolveCanonical(auto, this.links, this::setLastModified);
        System.out.println(this);
    }

    @Override
    public Path getCanonical() {
        return canonical;
    }

    @Override
    public Set<Path> getAll() {
        return Stream.concat(
                Stream.of(canonical),
                links.stream()
        ).collect(Collectors.toSet());
    }

    @Override
    public Instant getCreatedDate() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public void setLastModified(FileTime fileTime) {
        setCreatedOn(fileTime.toInstant());
    }

    @Override
    public boolean isFile() {
        return Files.isRegularFile(getCanonical());
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(getCanonical());
    }

    @Override
    public boolean overlaps(PortReference other) {
        return Objects.equals(isFile(), other.isFile()) &&
                Objects.equals(isDirectory(), other.isDirectory()) &&
                Objects.equals(isInvalid() , other.isInvalid()) &&
                (Objects.equals(getCanonical(), other.getCanonical()) ||
                                getLinks().stream()
                                    .anyMatch(myLink -> other.getLinks().stream()
                                        .anyMatch(otherLink -> Objects.equals(myLink, otherLink))));
    }

    private static String describe(Path auto, List<Path> links) {
        return links.stream().map(Path::toString).collect(Collectors.joining(" -> ")) + " -> " + auto;
    }

    /**
     * Go down the rabbit hole.
     * @param auto
     * @param links
     * @return
     * @throws IOException
     */
    private static Path resolveCanonical(Path auto, List<Path> links, Consumer<FileTime> fileTimeConsumer) throws IOException {
        if (! Files.exists(auto)) {
            throw new IllegalArgumentException("Bad FileReference, does not exist: " + describe(auto, links));
        }
        if (Files.isSymbolicLink(auto)) {
            links.add(auto);
            return resolveCanonical(auto.toRealPath(), links, fileTimeConsumer);
        }
        if (Files.isDirectory(auto) || Files.isRegularFile(auto) || Files.isReadable(auto)) {
            if (fileTimeConsumer != null && Files.isReadable(auto)) {
                BasicFileAttributes attrs = Files.readAttributes(auto, BasicFileAttributes.class);
                fileTimeConsumer.accept(attrs.creationTime());
            }
            return auto;
        }
        throw new IllegalArgumentException("Bad FileReference- unrecognized: " + describe(auto, links));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPortReference that = (StockPortReference) o;
        return Objects.equals(canonical, that.canonical);
    }

    @Override
    public int hashCode() {
        return Objects.hash(canonical);
    }

    @Override
    public String toString() {
        return String.valueOf(canonical + ":" + createdOn);
    }
}
