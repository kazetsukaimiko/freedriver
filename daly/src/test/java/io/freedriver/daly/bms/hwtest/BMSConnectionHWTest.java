package io.freedriver.daly.bms.hwtest;

import io.freedriver.daly.bms.Address;
import io.freedriver.daly.bms.CommandId;
import io.freedriver.daly.bms.Flag;
import io.freedriver.daly.bms.Request;
import io.freedriver.daly.bms.Response;
import io.freedriver.daly.bms.stream.ResponseAccumlator;
import io.freedriver.serial.api.params.BaudRates;
import io.freedriver.serial.api.params.SerialParams;
import io.freedriver.serial.stream.JSSCPort;
import io.freedriver.serial.stream.JSSCSerialStream;
import io.freedriver.serial.stream.api.PortReference;
import io.freedriver.serial.stream.api.SerialStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BMSConnectionHWTest {
    String device = "/dev/ttyUSB0";
    SerialParams params = new SerialParams().setBaudRate(BaudRates.BAUDRATE_9600);
    JSSCPort port = JSSCPort.get(PortReference.auto(Paths.get(device)), params);

    @Test
    public void testGetCellVoltages() throws IOException {
        Request request = new Request();
        request.setStartFlag(Flag.START);
        request.setAddress(Address.UPPER);
        request.setCommandId(CommandId.CELL_VOLTAGE);

        testRequestRepliesAccordingly(request);
    }

    public void testRequestRepliesAccordingly(Request request) throws IOException {
        SerialStream stream = new JSSCSerialStream(port);
        stream.getOutputStream()
                .write(request.toFullMessage());
        stream.getOutputStream().flush();

        ResponseAccumlator responseAccumlator = new ResponseAccumlator();
        Response response = responseAccumlator.apply(stream);

        assertEquals(request.getCommandId(), response.getCommandId());

        System.out.println("Finish");
    }

}
