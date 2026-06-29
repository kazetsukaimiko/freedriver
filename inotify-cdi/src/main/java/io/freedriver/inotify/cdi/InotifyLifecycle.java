package io.freedriver.inotify.cdi;

import java.nio.file.Path;
import java.nio.file.Paths;
import io.freedriver.inotify.InotifyMask;
import lombok.extern.java.Log;
import io.freedriver.inotify.InotifyWatch;
import io.freedriver.inotify.InotifyWatcher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Log
@ApplicationScoped
public class InotifyLifecycle {
    private static final Path SERIAL_BY_ID = Paths.get("/dev/serial/by-id");

    @Inject
    InotifyWatcher watcher;

    @Inject
    InotifyCdiBridge cdiBridge;

    private InotifyWatch serialWatch;

    @PostConstruct
    void start() {
        if (!watcher.isSupported()) {
            log.warning("inotify is not supported; filesystem hotplug events disabled");
            return;
        }
        watcher.start();
        serialWatch = watcher.watch(SERIAL_BY_ID, InotifyMask.SERIAL_HOTPLUG, cdiBridge);
        log.info("Watching serial hotplug path: " + SERIAL_BY_ID);
    }

    @PreDestroy
    void stop() {
        if (serialWatch != null) {
            serialWatch.close();
        }
    }
}