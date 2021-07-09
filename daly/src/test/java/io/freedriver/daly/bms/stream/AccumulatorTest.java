package io.freedriver.daly.bms.stream;

import io.freedriver.daly.bms.ExampleResponses;
import io.freedriver.daly.bms.Flag;
import io.freedriver.daly.bms.Response;
import io.freedriver.serial.stream.api.SerialStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccumulatorTest {

    @Test
    public void testStreamAccumulator() throws IOException {
        testUsingStream(ExampleResponses.SOC_1);
        testUsingStream(ExampleResponses.SOC_2);
        testUsingStream(ExampleResponses.SOC_3);
    }

    public void testUsingStream(byte[] fullMessage) throws IOException {
        SerialStream fake = new FakeSerialStream(fullMessage);
        ResponseAccumlator accumlator = new ResponseAccumlator();
        testPacket(fullMessage, accumlator.apply(fake));
    }

    @Test
    public void testOfFullMessage() {
        testPacket(ExampleResponses.SOC_1);
        testPacket(ExampleResponses.SOC_2);
        testPacket(ExampleResponses.SOC_3);
    }

    public void testPacket(byte[] fullMessage) {
        testPacket(fullMessage, ResponseAccumlator.ofFullMessage(fullMessage));
    }

    public void testPacket(byte[] fullMessage, Response r) {
        assertEquals(Flag.START.getValue(), fullMessage[0]);

        byte[] inReverse = r.toFullMessage();
        assertArrayEquals(fullMessage, inReverse);

        assertEquals(fullMessage[0], r.getStartFlag().getValue());
        assertEquals(fullMessage[1], r.getAddress().getValue());
        assertEquals(fullMessage[2], r.getCommandId().getValue());
        assertEquals(fullMessage[3], r.getDataLength());
        assertEquals(fullMessage.length, 4 + r.getDataLength() + 1);
        assertTrue(r.validates());
    }



}
