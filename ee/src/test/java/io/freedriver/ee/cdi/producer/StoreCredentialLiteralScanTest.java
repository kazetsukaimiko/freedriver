package io.freedriver.ee.cdi.producer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class StoreCredentialLiteralScanTest {
    @Test
    void mainSourcesPassNoLiteralStoreCredentials() throws IOException {
        Path root = Path.of("src/main/java");
        assertTrue(Files.isDirectory(root), "expected ee/src/main/java as the working directory");
        List<String> problems = new ArrayList<>();
        try (Stream<Path> files = Files.walk(root)) {
            files.filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                try {
                    problems.addAll(problemsIn(path.toString(), Files.readString(path)));
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void scannerFlagsLiteralsNoArgOpenAndConstructors() {
        assertThrows(AssertionError.class, () -> requireClean("openOrCreate(\"nitrite\", \"nitrite\")"));
        assertThrows(AssertionError.class, () -> requireClean("db.openOrCreate()"));
        assertThrows(AssertionError.class, () -> requireClean("new NitriteCredentials(\"user\", \"pass\")"));
        requireClean("db.openOrCreate(credentials.userId(), credentials.password())");
        requireClean("// db.openOrCreate(\"nitrite\", \"nitrite\")\nopenOrCreate(credentials.userId(), credentials.password());");
    }

    private static void requireClean(String source) {
        List<String> problems = problemsIn("fixture", source);
        if (!problems.isEmpty()) {
            throw new AssertionError(String.join("\n", problems));
        }
    }

    static List<String> problemsIn(String name, String source) {
        String stripped = stripComments(source);
        List<String> problems = new ArrayList<>();
        scanCalls(name, stripped, "openOrCreate", problems);
        scanConstructors(name, stripped, "NitriteCredentials", problems);
        return problems;
    }

    private static void scanCalls(String name, String source, String method, List<String> problems) {
        int from = 0;
        while (from < source.length()) {
            int index = source.indexOf(method, from);
            if (index < 0) {
                return;
            }
            if (!isCall(source, index, method)) {
                from = index + method.length();
                continue;
            }
            int paren = source.indexOf('(', index + method.length());
            String args = arguments(source, paren);
            if (args.trim().isEmpty()) {
                problems.add(name + " calls " + method + " with no arguments");
            } else if (args.indexOf('"') >= 0) {
                problems.add(name + " passes a string literal to " + method);
            }
            from = paren + 1;
        }
    }

    private static void scanConstructors(String name, String source, String type, List<String> problems) {
        String needle = "new " + type;
        int from = 0;
        while (from < source.length()) {
            int index = source.indexOf(needle, from);
            if (index < 0) {
                return;
            }
            int paren = source.indexOf('(', index + needle.length());
            if (paren < 0) {
                return;
            }
            String args = arguments(source, paren);
            if (args.indexOf('"') >= 0) {
                problems.add(name + " passes a string literal to " + type);
            }
            from = paren + 1;
        }
    }

    private static boolean isCall(String source, int index, String method) {
        if (index > 0) {
            char previous = source.charAt(index - 1);
            if (Character.isLetterOrDigit(previous) || previous == '_') {
                return false;
            }
        }
        int after = index + method.length();
        int cursor = after;
        while (cursor < source.length() && Character.isWhitespace(source.charAt(cursor))) {
            cursor++;
        }
        return cursor < source.length() && source.charAt(cursor) == '(';
    }

    private static String arguments(String source, int openParen) {
        int depth = 0;
        for (int i = openParen; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    return source.substring(openParen + 1, i);
                }
            }
        }
        return source.substring(openParen + 1);
    }

    private static String stripComments(String source) {
        StringBuilder out = new StringBuilder();
        int n = source.length();
        for (int i = 0; i < n; i++) {
            char c = source.charAt(i);
            if (c == '"') {
                int j = i + 1;
                while (j < n) {
                    char d = source.charAt(j);
                    if (d == '\\') {
                        j += 2;
                        continue;
                    }
                    j++;
                    if (d == '"') {
                        break;
                    }
                }
                out.append(source, i, Math.min(j, n));
                i = j - 1;
                continue;
            }
            if (c == '/' && i + 1 < n && source.charAt(i + 1) == '/') {
                while (i < n && source.charAt(i) != '\n') {
                    i++;
                }
                out.append('\n');
                continue;
            }
            if (c == '/' && i + 1 < n && source.charAt(i + 1) == '*') {
                i += 2;
                while (i + 1 < n && !(source.charAt(i) == '*' && source.charAt(i + 1) == '/')) {
                    i++;
                }
                i++;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }
}
