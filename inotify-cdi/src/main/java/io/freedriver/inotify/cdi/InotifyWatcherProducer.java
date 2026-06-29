package io.freedriver.inotify.cdi;

import io.freedriver.inotify.InotifyWatcher;
import io.freedriver.inotify.InotifyWatchers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class InotifyWatcherProducer {

    @Produces
    @ApplicationScoped
    public InotifyWatcher inotifyWatcher() {
        return InotifyWatchers.linux();
    }

    void dispose(@Disposes InotifyWatcher watcher) {
        watcher.close();
    }
}