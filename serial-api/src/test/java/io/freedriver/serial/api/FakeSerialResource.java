package io.freedriver.serial.api;

import java.time.Duration;

/**
 * You can read from this resource anything you write in.
 */
public class FakeSerialResource extends SynchronizedListBacked implements SerialResource {
    private static final Duration DEFAULT_DURATION = Duration.ofMillis(20);
    int index = 0;

    @Override
    public void clear() {
        doWith(l -> l.removeIf(b -> true));
    }

    @Override
    public void write(byte[] array) {
        doWith(l -> {
            for (byte b : array) {
                l.add(b);
            }
            return null;
        });
    }

    @Override
    public String getName() {
        return "FakeSerialResource ";
    }

    @Override
    public boolean isOpened() {
        return true;
    }

    @Override
    public byte[] read(int size) {
        byte[] full = new byte[size];
        for (int idx = 0; idx < size; idx++) {
            while (isEmpty()) {
                waitFor(DEFAULT_DURATION);
            }
            full[idx] = doWith(l -> l.remove(0));
        }
        return full;
    }

    @Override
    public void close() throws Exception {
        clear();
    }
    
    public void waitFor(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
