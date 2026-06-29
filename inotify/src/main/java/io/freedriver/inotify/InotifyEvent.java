package io.freedriver.inotify;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

public record InotifyEvent(
        Path watchDirectory,
        String entryName,
        int mask,
        int watchDescriptor,
        Instant observedAt) {

    public InotifyEvent {
        Objects.requireNonNull(watchDirectory, "watchDirectory");
        entryName = entryName == null ? "" : entryName;
        observedAt = observedAt == null ? Instant.now() : observedAt;
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

    public boolean isModify() {
        return (mask & InotifyMask.MODIFY) != 0;
    }

    public Path affectedPath() {
        return entryName.isEmpty() ? watchDirectory : watchDirectory.resolve(entryName);
    }
}