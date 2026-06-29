package io.freedriver.inotify.cdi;

import io.freedriver.inotify.InotifyEvent;
import io.freedriver.inotify.InotifyListener;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class InotifyCdiBridge implements InotifyListener {
    @Inject
    Event<InotifyFilesystemEvent> filesystemEvents;

    @Override
    public void onEvent(InotifyEvent event) {
        filesystemEvents.fire(InotifyFilesystemEvent.from(event));
    }
}