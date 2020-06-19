package io.freedriver.electrodacus.sbms;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Decompresses/decodes field data from the SBMS. Results are all Strings.
 */
public enum SBMSField {
    YEAR(0, 1),
    MONTH(1, 1),
    DAY(2, 1),
    HOUR(3, 1),
    MINUTE(4, 1),
    SECOND(5, 1),
    SOC(6, 2),
    C1(8, 2),
    C2(10, 2),
    C3(12, 2),
    C4(14, 2),
    C5(16, 2),
    C6(18, 2),
    C7(20, 2),
    C8(22, 2),
    IT(24, 2, SBMSField::temperatureConvert),
    ET(26, 2, SBMSField::temperatureConvert),
    CHARGING(28, 1, false),
    CURRENT_mA(29, 3),
    PV1(32,3),
    PV2(35, 3),
    EXT_LOAD_CURRENT(38, 3),
    AD2(41, 3),
    AD3(44, 3),
    HT1(47, 3),
    HT2(50, 3),
    ERR(53, 3)
    ;

    // Where in the response string our data is
    private final int offset;
    // How many characters starting with offset is a part of the data
    private final int length;
    // Do we need to decode? One-off for CHARGING.
    private final boolean decode;
    // How the values need to be modified to decode.
    private final Function<String, String> transform;

    SBMSField(int offset, int length, boolean decode, Function<String, String> transform) {
        this.offset = offset;
        this.length = length;
        this.decode = decode;
        this.transform = transform;
    }

    SBMSField(int offset, int length, boolean decode) {
        this(offset, length, decode, Function.identity());
    }
    SBMSField(int offset, int length) {
        this(offset, length, true);
    }
    SBMSField(int offset, int length, Function<String, String> transform) {
        this(offset, length, true, transform);
    }

    // Returns, optionally, an SBMSFieldValue (enum member + string representation) of the given value.
    public Optional<SBMSFieldValue> decodeToString(byte[] ba) {
        if (SBMSField.valid(ba)) {
            return Optional.of(new SBMSFieldValue(this, transform.apply(SBMSField.decodeProp(ba, offset, length, decode))));
        }
        return Optional.empty();
    }

    // Determines if data from SBMS0 is valid
    // Stupid, as it just checks the length of the data.
    public static boolean valid(byte[] ba) {
        return ba.length == 60;
    }

    public static String decodeProp(byte[] data, int offset, int length, boolean decode) {
        String rep = new String(data, StandardCharsets.US_ASCII);
        return String.valueOf(decode(offset, length, decode, rep));
    }

    /**
     * Decompression algorithm ripped off from JavaScript Code.
     * @param offsetInString The offset of chars to start reading at
     * @param numCharsToRead The number of digits to read
     * @param targetString The Input string
     * @returns String representation of a double value.
     */
    public static String decode(int offsetInString, int numCharsToRead, boolean decode, String targetString) {
        if (decode) {
            double xx = 0L;
            for (int z = 0; z < numCharsToRead; z++) {
                xx = xx + (
                        (
                                Character.codePointAt(targetString, (offsetInString + numCharsToRead - 1) - z) - 35
                        )
                                * Math.pow(91, z));
            }
            return String.valueOf(xx);
        }
        return targetString.substring(offsetInString, offsetInString+numCharsToRead);
    }

    public static Stream<SBMSField> stream() {
        return Stream.of(values());
    }

    // Value here needs to be converted as it is numerically offset/scaled.
    public static String temperatureConvert(String input) {
        return String.valueOf((Double.parseDouble(input)-450)/10);
    }
}
