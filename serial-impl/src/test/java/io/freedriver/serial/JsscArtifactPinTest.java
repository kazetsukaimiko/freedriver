package io.freedriver.serial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.Test;

class JsscArtifactPinTest {
    private static final String PIN = "/jssc-2.10.4-pin.txt";
    private static final String JSSC = "io.github.java-native:jssc:2.10.4";
    private static final String LOADER = "org.scijava:native-lib-loader:2.5.0";
    private static final String LOADER_JAR = "native-lib-loader-2.5.0.jar";

    @Test
    void pinnedJarsAndNativesMatchTheResolvedArtifacts() throws Exception {
        Pin pin = readPin();
        assertEquals(Set.of(JSSC, LOADER), pin.jars.keySet());
        List<Path> entries = runtimeEntries();
        assertLoaderVersion(entries);
        Path jsscJar = requireJar(entries, fileName(JSSC), pin.jars.get(JSSC));
        requireJar(entries, fileName(LOADER), pin.jars.get(LOADER));
        assertEquals(pin.natives, nativesIn(jsscJar), "natives/ entries differ from " + PIN);
    }

    private static void assertLoaderVersion(List<Path> entries) {
        for (Path entry : entries) {
            if (Files.isDirectory(entry) || entry.getFileName() == null) {
                continue;
            }
            String name = entry.getFileName().toString();
            if (name.startsWith("native-lib-loader-") && name.endsWith(".jar") && !name.equals(LOADER_JAR)) {
                fail("resolved native-lib-loader version is not 2.5.0: " + name);
            }
        }
    }

    private static Path requireJar(List<Path> entries, String fileName, String expectedHash) throws Exception {
        List<Path> found = new ArrayList<>();
        for (Path entry : entries) {
            Path candidate = Files.isDirectory(entry) ? entry.resolve(fileName) : entry;
            if (candidate.getFileName() != null
                    && candidate.getFileName().toString().equals(fileName)
                    && Files.isRegularFile(candidate)) {
                found.add(candidate);
            }
        }
        if (found.isEmpty()) {
            fail("absent " + fileName + " on jdk.module.path and java.class.path");
        }
        for (Path jar : found) {
            assertEquals(expectedHash, sha256(jar), fileName);
        }
        return found.get(0);
    }

    private static Map<String, String> nativesIn(Path jar) throws Exception {
        Map<String, String> natives = new LinkedHashMap<>();
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.getName().startsWith("natives/")) {
                    continue;
                }
                if (natives.containsKey(entry.getName())) {
                    fail("duplicate natives/ entry " + entry.getName());
                }
                try (InputStream in = zip.getInputStream(entry)) {
                    natives.put(entry.getName(), sha256(in));
                }
            }
        }
        return natives;
    }

    private static List<Path> runtimeEntries() {
        List<Path> entries = new ArrayList<>();
        for (String property : List.of("jdk.module.path", "java.class.path")) {
            String value = System.getProperty(property);
            if (value == null || value.isEmpty()) {
                continue;
            }
            for (String part : value.split(File.pathSeparator, -1)) {
                if (!part.isEmpty()) {
                    entries.add(Path.of(part));
                }
            }
        }
        return entries;
    }

    private static String fileName(String coordinate) {
        String[] parts = coordinate.split(":");
        if (parts.length != 3) {
            fail("pin coordinate " + coordinate);
        }
        return parts[1] + "-" + parts[2] + ".jar";
    }

    private static Pin readPin() throws IOException {
        InputStream resource = JsscArtifactPinTest.class.getResourceAsStream(PIN);
        if (resource == null) {
            fail("missing " + PIN);
        }
        Pin pin = new Pin();
        try (InputStream in = resource) {
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String raw : text.split("\n", -1)) {
                String line = raw.trim();
                if (line.isEmpty()) {
                    continue;
                }
                int space = line.indexOf(' ');
                if (space < 1 || line.indexOf(' ', space + 1) >= 0) {
                    fail("pin line must be one path and one sha256: " + line);
                }
                String name = line.substring(0, space);
                String hash = line.substring(space + 1);
                Map<String, String> target = name.startsWith("natives/") ? pin.natives : pin.jars;
                if (target.put(name, hash) != null) {
                    fail("duplicate pin entry " + name);
                }
            }
        }
        if (!pin.jars.containsKey(JSSC) || !pin.jars.containsKey(LOADER)) {
            fail("pin is missing " + JSSC + " or " + LOADER);
        }
        return pin;
    }

    private static String sha256(Path path) throws Exception {
        try (InputStream in = Files.newInputStream(path)) {
            return sha256(in);
        }
    }

    private static String sha256(InputStream in) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
        }
        byte[] bytes = digest.digest();
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            hex.append(Character.forDigit((value >> 4) & 0xF, 16));
            hex.append(Character.forDigit(value & 0xF, 16));
        }
        return hex.toString();
    }

    private static final class Pin {
        private final Map<String, String> jars = new LinkedHashMap<>();
        private final Map<String, String> natives = new LinkedHashMap<>();
    }
}
