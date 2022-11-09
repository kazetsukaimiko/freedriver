package io.freedriver.base.util.serial;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DeviceFiles implements Supplier<List<Path>> {
    VE_DIRECT_CABLE {
        @Override
        public List<Path> get() {
            return serialDevices()
                    .stream()
                    .filter(DeviceFiles::isVEDirectFile)
                    //.map(DeviceFiles::followSymlink)
                    .collect(Collectors.toList());
        }
    },
    ARDUINO {
        @Override
        public List<Path> get() {
            return serialDevices()
                    .stream()
                    .filter(DeviceFiles::isArduinoFile)
                    //.map(DeviceFiles::followSymlink)
                    .collect(Collectors.toList());
        }
    };


    static boolean isArduinoFile(Path path) {
        return path.getFileName().toString().startsWith("usb-Arduino");
    }

    static boolean isVEDirectFile(Path path) {
        return path.getFileName().toString().startsWith("usb-VictronEnergy_BV_VE_Direct_cable");
    }

    static List<Path> serialDevices() {
        try (Stream<Path> links = Files.list(devicesByIdPath())) {
            return links.collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Path followSymlink(Path path) {
        try {
            if (Files.isSymbolicLink(path)) {
                Path read = Files.readSymbolicLink(path);
                return read.isAbsolute() ? read : path.relativize(path);
            }
            return path;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Path devicesByIdPath() {
        return Paths.get("/","dev", "serial", "by-id");
    }
}
