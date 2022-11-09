package io.freedriver.base.util.serial;

import io.freedriver.base.Tests;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Tag(Tests.Integration)
public class DeviceFilesTest {
    @ParameterizedTest
    @EnumSource(DeviceFiles.class)
    public void testFindFile(DeviceFiles deviceFile) {
        List<Path> paths = deviceFile.get();
        assertNotNull(paths);
        assumeTrue(!paths.isEmpty(), "You need to have the serial device plugged in and recognized by the OS.");
        paths.forEach(device -> {
            display(device);
        });
    }

    private void display(Path path) {
        System.out.println(path);
    }
}
