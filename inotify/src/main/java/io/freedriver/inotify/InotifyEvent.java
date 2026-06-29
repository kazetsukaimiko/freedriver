package io.freedriver.inotify;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

public final class InotifyEvent {
    private final Path watchDirectory;
    private final String entryName;
    private final int mask;
    private final int watchDescriptor;
    private final Instant observedAt;

    public InotifyEvent(Path watchDirectory, String entryName, int mask, int watchDescriptor, Instant observedAt) {
        this.watchDirectory = Objects.requireNonNull(watchDirectory, "watchDirectory");
        this.entryName = entryName == null ? "" : entryName;
        this.mask = mask;
        this.watchDescriptor = watchDescriptor;
        this.observedAt = observedAt == null ? Instant.now() : observedAt;
    }

    public Path watchDirectory() {
        return watchDirectory;
    }

    public String entryName() {
        return entryName;
    }

    public int mask() {
        return mask;
    }

    public int watchDescriptor() {
        return watchDescriptor;
    }

    public Instant observedAt() {
        return observedAt;
    }

    public boolean isCreate() {
        return (mask & InotifyMask.CREATE) != 0;
    }

    public boolean isDelete() {
        return (mask & InotifyMask.DELETE) != 0;
    }

    public boolean isMovedTo() {
        return (mask & InotifyMask.MOVED_TO) != 0;
    }

    public boolean isMovedFrom() {
        return (mask & InotifyMask.MOVED_FROM) != 0;
    }

    public Path affectedPath() {
        return entryName.isEmpty() ? watchDirectory : watchDirectory.resolve(entryName);
    }

    @Override
    public String toString() {
        return "InotifyEvent{dir=" + watchDirectory + ", name='" + entryName + "', mask=" + mask + "}";
    }
}