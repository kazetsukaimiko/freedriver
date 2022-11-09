package io.freedriver.daly.bms;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.freedriver.base.Tests;
import io.freedriver.daly.bms.checksum.debug.CRC8Steps;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

@Tag(Tests.Integration)
public class PythonTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void testGetHelpers() {
        System.out.println(getHelpers());
    }

    @Test
    public void testRunScript() throws IOException, InterruptedException {
        System.out.println(runPythonWithHelpers(""));
    }

    public static CRC8Steps crc8FromPython(byte[] array) {
        try {
            String result = runPythonWithHelpers(crc8FromPythonCmd(array)).trim();
            System.out.println("pythonResult("+result+")");
            return OBJECT_MAPPER.readValue(result, CRC8Steps.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String crc8FromPythonCmd(byte[] array) {
        return "jsonprint(crc8([" +
                Arrays.stream(intArray(array))
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(", ")) +
                "]))";
    }

    private static int[] intArray(byte[] array) {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++ ) {
            result[i] = array[i];
        }
        return result;
    }

    public static String runPythonWithHelpers(String additions) throws IOException, InterruptedException {
        String script = getHelpers() + "\n\n" + additions;
        ProcessBuilder pb = new ProcessBuilder("python");
        Process p = pb.start();
        try (OutputStream os = p.getOutputStream()) {
            os.write(script.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
        p.waitFor();
        return new String(p.getInputStream()
                .readAllBytes(), StandardCharsets.UTF_8);
    }

    public static String getResource(String name) {
        return new Scanner(
                Objects.requireNonNull(
                    PythonTest.class.getClassLoader().getResourceAsStream(name)),
                    StandardCharsets.UTF_8)
                        .useDelimiter("\\A")
                        .next();
    }

    public static String getHelpers() {
        return getResource("helpers.py");
    }

}
