package io.freedriver.daly.bms.checksum;

import io.freedriver.base.Tests;
import io.freedriver.daly.bms.ExampleResponses;
import io.freedriver.daly.bms.PythonTest;
import io.freedriver.daly.bms.checksum.CRC8;
import io.freedriver.daly.bms.checksum.CRCSum;
import io.freedriver.daly.bms.checksum.debug.CRC8Steps;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled // TODO : Fix / resolve.
public class CRC8DebugTest {
    private static final Random R = new Random(System.currentTimeMillis());


    @Test
    @Tag(Tests.Integration)
    public void testAgainstPython() {
        testResponse(CRC8::calc, ExampleResponses.SOC_2);
        testWithoutCRC(CRC8::calc, withoutCRC());
    }

    @Test
    public void testExamples() {
        testWithCRC(ExampleResponses.SOC_1);
        testWithCRC(ExampleResponses.SOC_2);
        testWithCRC(ExampleResponses.SOC_3);
    }

    public void testWithCRC(byte[] withCrc) {
        // If last char a newline, ignore.
        int end = withCrc[withCrc.length-1] == 0xa
                ? withCrc.length-2 : withCrc.length-1;

        int crc = CRC8.calc(withCrc, end);
        assertEquals(withCrc[end] & 0xFF, crc);
    }

    public void testWithoutCRC(CRCSum c, byte[] withoutCRC) {
        CRC8Steps steps = new CRC8Steps();
        int crc = c.calc(withoutCRC, withoutCRC.length, steps);
        CRC8Steps crcFromPy = PythonTest.crc8FromPython(withoutCRC);
        assertEquals(crcFromPy, steps);
        assertEquals(crcFromPy.getCrc(), crc);
    }

    public static void testResponse(CRCSum c, byte[] withCRC) {
        CRC8Steps steps = new CRC8Steps();
        int crc = c.calc(withCRC,withCRC.length-1, steps) & 0xFF;
        CRC8Steps crcFromPy = PythonTest.crc8FromPython(Arrays.copyOfRange(withCRC, 0, withCRC.length-1));
        assertEquals(crcFromPy, steps);
        assertEquals(crcFromPy.getCrc(), crc);
    }


    public static byte[] withoutCRC() {
        int bound = 10 + R.nextInt(20);
        byte[] arry = new byte[bound];
        for (int i = 0; i< bound; i++) {
            arry[i] = (byte) (R.nextInt(255) & 0xFF);
        }
        return arry;
    }
}
