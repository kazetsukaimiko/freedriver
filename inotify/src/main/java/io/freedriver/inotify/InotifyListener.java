package io.freedriver.inotify;

@FunctionalInterface
public interface InotifyListener {
    void onEvent(InotifyEvent event);
}