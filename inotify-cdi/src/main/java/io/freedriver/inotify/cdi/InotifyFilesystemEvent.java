package io.freedriver.inotify.cdi;

import java.nio.file.Path;
import java.time.Instant;

import io.freedriver.inotify.InotifyEvent;

public final class InotifyFilesystemEvent {
    private final Path watchDirectory;
    private final String entryName;
    private final int mask;
    private final int watchDescriptor;
    private final Instant observedAt;

    public InotifyFilesystemEvent(
            Path watchDirectory,
            String entryName,
            int mask,
            int watchDescriptor,
            Instant observedAt) {
        this.watchDirectory = watchDirectory;
        this.entryName = entryName;
        this.mask = mask;
        this.watchDescriptor = watchDescriptor;
        this.observedAt = observedAt;
    }

    public static InotifyFilesystemEvent from(InotifyEvent event) {
        return new InotifyFilesystemEvent(
                event.watchDirectory(),
                event.entryName(),
                event.mask(),
                event.watchDescriptor(),
                event.observedAt());
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

    public Path affectedPath() {
        return entryName == null || entryName.isEmpty()
                ? watchDirectory
                : watchDirectory.resolve(entryName);
    }
}