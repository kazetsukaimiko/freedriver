package io.freedriver.serial.api;

import java.nio.file.Path;

import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.params.SerialParams;

@FunctionalInterface
public interface SerialResourceFactory {
    SerialResource create(Path path, SerialParams params);

    final class Holder {
        private static volatile SerialResourceFactory factory;

        private Holder() {
        }

        public static void install(SerialResourceFactory factory) {
            Holder.factory = factory;
        }

        public static SerialResource create(Path path, SerialParams params) {
            SerialResourceFactory current = factory;
            if (current == null) {
                throw new SerialResourceException(
                        "No SerialResourceFactory installed. Add io.freedriver:serial-impl to the classpath.");
            }
            return current.create(path, params);
        }
    }
}