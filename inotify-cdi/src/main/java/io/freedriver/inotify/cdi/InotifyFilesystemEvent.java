package io.freedriver.inotify.cdi;

import java.nio.file.Path;
import java.time.Instant;

import io.freedriver.inotify.InotifyEvent;

public record InotifyFilesystemEvent(
        Path watchDirectory,
        String entryName,
        int mask,
        int watchDescriptor,
        Instant observedAt) {

    public static InotifyFilesystemEvent from(InotifyEvent event) {
        return new InotifyFilesystemEvent(
                event.watchDirectory(),
                event.entryName(),
                event.mask(),
                event.watchDescriptor(),
                event.observedAt());
    }

    public Path affectedPath() {
        return entryName == null || entryName.isEmpty()
                ? watchDirectory
                : watchDirectory.resolve(entryName);
    }
}