package io.freedriver.inotify.linux;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.freedriver.inotify.InotifyEvent;
import io.freedriver.inotify.InotifyListener;
import io.freedriver.inotify.InotifyWatch;
import io.freedriver.inotify.InotifyWatcher;

public final class JnrLinuxInotifyWatcher implements InotifyWatcher {
    private static final Logger LOGGER = Logger.getLogger(JnrLinuxInotifyWatcher.class.getName());
    private static final int BUFFER_SIZE = 16 * 1024;

    private final LinuxCLib libc = LinuxCLib.INSTANCE;
    private final Map<Integer, WatchedDirectory> watches = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean();
    private final boolean supported;

    private int inotifyFd = -1;
    private ExecutorService executor;

    public JnrLinuxInotifyWatcher() {
        boolean available = false;
        try {
            int fd = libc.inotify_init();
            if (fd >= 0) {
                libc.close(fd);
                available = true;
            }
        } catch (UnsatisfiedLinkError | Exception e) {
            LOGGER.log(Level.FINE, "inotify unavailable on this platform", e);
        }
        supported = available;
    }

    public static JnrLinuxInotifyWatcher createStarted() {
        JnrLinuxInotifyWatcher watcher = new JnrLinuxInotifyWatcher();
        watcher.start();
        return watcher;
    }

    @Override
    public boolean isSupported() {
        return supported;
    }

    @Override
    public InotifyWatch watch(Path directory, int mask, InotifyListener listener) {
        if (!supported) {
            throw new UnsupportedOperationException("inotify is not supported on this platform");
        }
        Path normalized = directory.toAbsolutePath().normalize();
        if (!Files.isDirectory(normalized)) {
            throw new IllegalArgumentException("Not a directory: " + normalized);
        }
        ensureStarted();
        int wd = libc.inotify_add_watch(inotifyFd, normalized.toString(), mask);
        if (wd < 0) {
            throw new IllegalStateException("inotify_add_watch failed for " + normalized);
        }
        watches.put(wd, new WatchedDirectory(normalized, mask, listener));
        int watchDescriptor = wd;
        return new InotifyWatch() {
            @Override
            public Path directory() {
                return normalized;
            }

            @Override
            public int mask() {
                return mask;
            }

            @Override
            public void close() {
                removeWatch(watchDescriptor);
            }
        };
    }

    private void removeWatch(int wd) {
        WatchedDirectory removed = watches.remove(wd);
        if (removed != null && inotifyFd >= 0) {
            libc.inotify_rm_watch(inotifyFd, wd);
        }
    }

    @Override
    public synchronized void start() {
        if (!supported || running.get()) {
            return;
        }
        inotifyFd = libc.inotify_init();
        if (inotifyFd < 0) {
            throw new IllegalStateException("inotify_init failed");
        }
        running.set(true);
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "freedriver-inotify");
            thread.setDaemon(true);
            return thread;
        });
        executor.submit(this::eventLoop);
        LOGGER.info("Linux inotify watcher started");
    }

    private void ensureStarted() {
        if (!running.get()) {
            start();
        }
    }

    @Override
    public synchronized void stop() {
        running.set(false);
        if (executor != null) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            executor = null;
        }
        if (inotifyFd >= 0) {
            libc.close(inotifyFd);
            inotifyFd = -1;
        }
        watches.clear();
    }

    @Override
    public void close() {
        stop();
    }

    private void eventLoop() {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (running.get()) {
            try {
                long bytesRead = libc.read(inotifyFd, buffer, buffer.length);
                if (bytesRead <= 0) {
                    sleepBriefly();
                    continue;
                }
                dispatch(buffer, (int) bytesRead);
            } catch (Exception e) {
                if (running.get()) {
                    LOGGER.log(Level.WARNING, "inotify read loop error", e);
                    sleepBriefly();
                }
            }
        }
    }

    private void dispatch(byte[] buffer, int bytesRead) {
        List<InotifyEventParser.ParsedInotifyEvent> parsed = InotifyEventParser.parse(buffer, bytesRead);
        for (InotifyEventParser.ParsedInotifyEvent raw : parsed) {
            WatchedDirectory watched = watches.get(raw.watchDescriptor());
            if (watched == null) {
                continue;
            }
            InotifyEvent event = new InotifyEvent(
                    watched.directory(),
                    raw.name(),
                    raw.mask(),
                    raw.watchDescriptor(),
                    Instant.now());
            for (InotifyListener listener : watched.listeners()) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Inotify listener failed for " + event, e);
                }
            }
        }
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static final class WatchedDirectory {
        private final Path directory;
        private final List<InotifyListener> listeners = new CopyOnWriteArrayList<>();

        private WatchedDirectory(Path directory, int mask, InotifyListener listener) {
            this.directory = directory;
            listeners.add(listener);
        }

        private Path directory() {
            return directory;
        }

        private List<InotifyListener> listeners() {
            return listeners;
        }
    }
}