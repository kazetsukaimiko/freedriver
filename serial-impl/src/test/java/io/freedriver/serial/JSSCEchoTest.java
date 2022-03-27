package io.freedriver.serial;

import io.freedriver.serial.api.params.BaudRates;
import io.freedriver.serial.api.params.SerialParams;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSSCEchoTest extends EchoTestZ<JSSCSerialResource> {
    // Path to an Arduino that echoes all input.
    public static final Path DEFAULT = Paths.get("/dev/ttyACM0");

    public void testDevice(Path device)  {

        testResource(construct());
    }

    @Override
    public JSSCSerialResource construct() {
        if (!Files.exists(DEFAULT)) {
            System.out.println("WARNING: Skipping echo test, " + DEFAULT + " does not exist. ");
            return null;
        }
        return new JSSCSerialResource(DEFAULT, new SerialParams()
                .setBaudRate(BaudRates.BAUDRATE_115200)
        );
    }

}
