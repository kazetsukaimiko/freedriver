package io.freedriver.inotify.linux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.freedriver.base.Tests;
import io.freedriver.inotify.InotifyEvent;
import io.freedriver.inotify.InotifyListener;
import io.freedriver.inotify.InotifyMask;
import io.freedriver.inotify.InotifyWatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

@Tag(Tests.Integration)
@EnabledOnOs(OS.LINUX)
class JnrLinuxInotifyWatcherIT {
    private static final int WATCH_MASK =
            InotifyMask.CREATE | InotifyMask.DELETE | InotifyMask.MODIFY
                    | InotifyMask.MOVED_FROM | InotifyMask.MOVED_TO;

    private Path tempDir;
    private InotifyWatcher watcher;
    private final List<InotifyEvent> events = new CopyOnWriteArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("freedriver-inotify-it-");
        watcher = new JnrLinuxInotifyWatcher();
        assumeTrue(watcher.isSupported(), "inotify not supported on this platform");
        watcher.start();
        InotifyListener collector = events::add;
        watcher.watch(tempDir, WATCH_MASK, collector);
        drainEvents();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (watcher != null) {
            watcher.close();
        }
        if (tempDir != null) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                            // best effort cleanup
                        }
                    });
        }
    }

    @Test
    void observesFileCreation() throws Exception {
        Path file = tempDir.resolve("created.txt");
        Files.writeString(file, "hello");

        InotifyEvent event = awaitEventMatching(e -> e.isCreate() && "created.txt".equals(e.entryName()));

        assertEquals(tempDir, event.watchDirectory());
        assertEquals(file, event.affectedPath());
    }

    @Test
    void observesFileDeletion() throws Exception {
        Path file = tempDir.resolve("deleted.txt");
        Files.writeString(file, "bye");
        awaitEventMatching(e -> e.isCreate() && "deleted.txt".equals(e.entryName()));
        drainEvents();

        Files.delete(file);

        InotifyEvent event = awaitEventMatching(e -> e.isDelete() && "deleted.txt".equals(e.entryName()));
        assertEquals(file, event.affectedPath());
    }

    @Test
    void observesFileModification() throws Exception {
        Path file = tempDir.resolve("modified.txt");
        Files.writeString(file, "v1");
        awaitEventMatching(e -> e.isCreate() && "modified.txt".equals(e.entryName()));
        drainEvents();

        Files.writeString(file, "v2");

        InotifyEvent event = awaitEventMatching(e -> e.isModify() && "modified.txt".equals(e.entryName()));
        assertEquals(file, event.affectedPath());
    }

    @Test
    void observesFileRenameWithinDirectory() throws Exception {
        Path source = tempDir.resolve("old-name.txt");
        Path target = tempDir.resolve("new-name.txt");
        Files.writeString(source, "rename-me");
        awaitEventMatching(e -> e.isCreate() && "old-name.txt".equals(e.entryName()));
        drainEvents();

        Files.move(source, target);

        InotifyEvent movedFrom =
                awaitEventMatching(e -> e.isMovedFrom() && "old-name.txt".equals(e.entryName()));
        InotifyEvent movedTo = awaitEventMatching(e -> e.isMovedTo() && "new-name.txt".equals(e.entryName()));

        assertEquals(source, movedFrom.affectedPath());
        assertEquals(target, movedTo.affectedPath());
        assertTrue(movedFrom.watchDescriptor() == movedTo.watchDescriptor());
    }

    private void drainEvents() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
        events.clear();
    }

    private InotifyEvent awaitEventMatching(java.util.function.Predicate<InotifyEvent> predicate)
            throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        while (System.nanoTime() < deadline) {
            for (InotifyEvent event : events) {
                if (predicate.test(event)) {
                    return event;
                }
            }
            TimeUnit.MILLISECONDS.sleep(25);
        }
        throw new AssertionError("Timed out waiting for inotify event matching " + predicate
                + "; saw: " + events);
    }
}