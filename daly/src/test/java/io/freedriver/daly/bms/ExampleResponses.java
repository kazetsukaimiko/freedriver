package io.freedriver.daly.bms;

public class ExampleResponses {

    /*
        "test_responses": [
            b"\xa5\x01\x90\x08\x02\x10\x00\x00uo\x03\xbc\xf3",
            b"\xa5\x01\x90\x08\x02\x14\x00\x00uE\x03x\x89",
            b"\xa5\x01\x90\x08\x01\t\x00\x00u\xcf\x03\n\x99",
        ],
     */
    public static final byte[] SOC_1 = new byte[]{
            (byte) 0xa5, // Start flag
            (byte) 0x1,
            (byte) 0x90,
            (byte) 0x8,
            (byte) 0x2,
            (byte) 0x10,
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x75,
            (byte) 0x6f,
            (byte) 0x3,
            (byte) 0xbc,
            (byte) 0xf3
    };
    public static final byte[] SOC_2 = new byte[]{
            (byte) 0xa5,
            (byte) 0x1,
            (byte) 0x90,
            (byte) 0x8,
            (byte) 0x2,
            (byte) 0x14,
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x75,
            (byte) 0x45,
            (byte) 0x3,
            (byte) 0x78,
            (byte) 0x89
    };
    public static final byte[] SOC_3 = new byte[]{
            (byte) 0xa5,
            (byte) 0x1,
            (byte) 0x90,
            (byte) 0x8,
            (byte) 0x1,
            (byte) 0x9,
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x75,
            (byte) 0xcf,
            (byte) 0x3,
            (byte) 0xa,
            (byte) 0x99
    };
}
