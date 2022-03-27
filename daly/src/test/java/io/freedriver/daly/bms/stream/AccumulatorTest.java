package io.freedriver.daly.bms.stream;

import io.freedriver.daly.bms.DalyCommand;
import io.freedriver.daly.bms.ExampleResponses;
import io.freedriver.daly.bms.Flag;
import io.freedriver.daly.bms.Response;
import io.freedriver.serial.stream.api.SerialEntityStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccumulatorTest {

    @Test
    @Disabled
    public void testStreamAccumulator() throws IOException {
        testUsingStream(ExampleResponses.SOC_1);
        testUsingStream(ExampleResponses.SOC_2);
        testUsingStream(ExampleResponses.SOC_3);
    }

    public void testUsingStream(byte[] fullMessage) throws IOException {
        //SerialEntityStream fake = new FakeSerialEntityStream(fullMessage);
        //ResponseAccumlator accumlator = new ResponseAccumlator();
        //testPacket(fullMessage, accumlator.apply(fake));
    }

    @Test
    @Disabled
    public void testOfFullMessage() {
        testPacket(ExampleResponses.SOC_1);
        testPacket(ExampleResponses.SOC_2);
        testPacket(ExampleResponses.SOC_3);
    }

    public void testPacket(byte[] fullMessage) {
        testPacket(fullMessage, ResponseAccumlator.ofFullMessage(fullMessage));
    }

    public void testPacket(byte[] fullMessage, Response r) {
        assertEquals(DalyCommand.READ.getValue(), fullMessage[0]);

        byte[] inReverse = null; // r.asByteArray();
        assertArrayEquals(fullMessage, inReverse);

        assertEquals(fullMessage[0], Flag.START.getValue());
        assertEquals(fullMessage[1], r.getAddress().getValue());
        assertEquals(fullMessage[2], r.getQueryId().getValue());
        assertEquals(fullMessage[3], r.getDataLength());
        assertEquals(fullMessage.length, 4 + r.getDataLength() + 1);
        assertTrue(r.validates());
    }



}
