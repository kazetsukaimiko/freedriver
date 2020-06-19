package io.freedriver.victron;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Logger;

public class VEDirectDeviceTest {
    private static final Logger LOGGER = Logger.getLogger(VEDirectDeviceTest.class.getName());

    @BeforeEach
    void initEach(TestInfo testInfo) {
        testInfo.getTestMethod()
                .map(Method::getName)
                .ifPresent(LOGGER::info);
    }

    /**
     * This test will open and read from any connected VE.Direct devices it finds.
     * @throws IOException
     */
    /*
    @Test
    public void testReadLines() throws IOException {
        VEDirectDevice.allVEDirectDevices()
                .peek(VEDirectDeviceTest::log)
                .flatMap(VEDirectDevice::readRawEntries)
                .takeWhile(string -> !string.startsWith(VEDirectColumn.CHECKSUM.getColumnName()))
                .forEach(VEDirectDeviceTest::log);
    }

     */

    /**
     * This test will open and read from any connected VE.Direct devices it finds.
     * @throws IOException
     */
    /*
    @Test
    public void testReadColumns() throws IOException {
        VEDirectDevice.allVEDirectDevices()
                .peek(VEDirectDeviceTest::log)
                .flatMap(VEDirectDevice::readAsColumns)
                .takeWhile(col -> col.getColumn() != VEDirectColumn.CHECKSUM)
                .forEach(VEDirectDeviceTest::log);
    }
     */

    /**
     * This test will open and read from any connected VE.Direct devices it finds.
     * @throws IOException
     */
    /*
    @Test
    public void testReadMessages() throws IOException {
        VEDirectDevice.allVEDirectDevices()
                .peek(VEDirectDeviceTest::log)
                .flatMap(VEDirectDevice::readAsMessages)
                .limit(10)
                .forEach(VEDirectDeviceTest::log);
    }

     */

    static void log(Object o) {
        Optional.ofNullable(o)
                .map(Object::toString)
                .ifPresent(LOGGER::info);
    }

}
