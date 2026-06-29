package io.freedriver.inotify;

import io.freedriver.inotify.linux.JnrLinuxInotifyWatcher;

public final class InotifyWatchers {
    private InotifyWatchers() {
    }

    public static InotifyWatcher linux() {
        return new JnrLinuxInotifyWatcher();
    }
}