package io.freedriver.architecture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class ConsumerHookTest {

    private static final List<String> WEB_UTILITIES = List.of(
            "stereotypes", "mcp-support", "hit-window", "freedriver-mqtt");

    @Test
    public void api_quality_test_reads_the_subclass_list_and_root() throws IOException {
        Path root = copy("example-root");
        String previous = System.getProperty(ReactorBinding.MAVEN_REACTOR_ROOT);
        System.setProperty(ReactorBinding.MAVEN_REACTOR_ROOT, root.resolve("missing").toString());
        try {
            ApiQualityTest test = new ApiQualityTest() {
                @Override
                protected List<String> utilityArtifacts() {
                    return WEB_UTILITIES;
                }

                @Override
                protected Path reactorRoot() {
                    return root;
                }
            };
            ConsumerGate gate = test.gate();
            assertEquals(WEB_UTILITIES, gate.utilityArtifacts());
            assertEquals(root.toAbsolutePath().normalize(), gate.reactorRoot());
            assertEquals(ReactorBinding.DEFAULT_GROUP_PREFIX, gate.groupPrefix());
        } finally {
            restore(previous);
        }
    }

    @Test
    public void roles_test_uses_the_same_hook() throws IOException {
        Path root = copy("example-root");
        RolesTest test = new RolesTest() {
            @Override
            protected List<String> utilityArtifacts() {
                return List.of("stereotypes");
            }

            @Override
            protected Path reactorRoot() {
                return root;
            }
        };
        assertEquals(List.of("stereotypes"), test.gate().utilityArtifacts());
    }

    @Test
    public void layering_test_inherits_the_hook_and_names_its_exception_package() throws IOException {
        Path root = copy("example-root");
        LayeringTest test = new LayeringTest() {
            @Override
            protected List<String> utilityArtifacts() {
                return WEB_UTILITIES;
            }

            @Override
            protected Path reactorRoot() {
                return root;
            }

            @Override
            protected String exceptionPackage() {
                return "io.freedriver.example.api";
            }
        };
        assertEquals("io.freedriver.example.api", test.exceptionPackage());
        assertEquals(WEB_UTILITIES, test.gate().utilityArtifacts());
        assertEquals(23, LayeringTest.RECORDED_JAVA);
    }

    @Test
    public void the_default_root_is_the_maven_property() throws IOException {
        Path root = copy("example-root");
        String previous = System.getProperty(ReactorBinding.MAVEN_REACTOR_ROOT);
        try {
            System.clearProperty(ReactorBinding.MAVEN_REACTOR_ROOT);
            ApiQualityTest missing = new ApiQualityTest() {
                @Override
                protected List<String> utilityArtifacts() {
                    return WEB_UTILITIES;
                }
            };
            IllegalStateException failure = assertThrows(IllegalStateException.class, missing::gate);
            assertTrue(failure.getMessage().contains(ReactorBinding.MAVEN_REACTOR_ROOT));
            System.setProperty(ReactorBinding.MAVEN_REACTOR_ROOT, root.toString());
            assertEquals(root.toAbsolutePath().normalize(), missing.gate().reactorRoot());
        } finally {
            restore(previous);
        }
    }

    private static void restore(String previous) {
        if (previous == null) {
            System.clearProperty(ReactorBinding.MAVEN_REACTOR_ROOT);
        } else {
            System.setProperty(ReactorBinding.MAVEN_REACTOR_ROOT, previous);
        }
    }

    private static Path copy(String name) throws IOException {
        Path source = Path.of("src", "test", "resources", "reactors", name);
        Path dest = Files.createTempDirectory("architecture-rules-");
        try (Stream<Path> walk = Files.walk(source)) {
            for (Path path : walk.toList()) {
                Path target = dest.resolve(source.relativize(path).toString());
                if (Files.isDirectory(path)) {
                    Files.createDirectories(target);
                } else {
                    Files.copy(path, target);
                }
            }
        }
        return dest;
    }
}
