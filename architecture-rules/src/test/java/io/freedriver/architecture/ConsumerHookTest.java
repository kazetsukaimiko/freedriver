package io.freedriver.architecture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
            ConsumerGate gate = test.binding().gate();
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
        assertEquals(List.of("stereotypes"), test.binding().gate().utilityArtifacts());
        assertSame(test.binding(), test.binding());
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
        assertEquals(WEB_UTILITIES, test.binding().gate().utilityArtifacts());
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
            IllegalStateException failure = assertThrows(IllegalStateException.class, missing::binding);
            assertTrue(failure.getMessage().contains(ReactorBinding.MAVEN_REACTOR_ROOT));
            assertTrue(failure.getMessage().contains("systemPropertyVariables"));
            assertTrue(failure.getMessage().contains("maven-surefire-plugin"));
            System.setProperty(ReactorBinding.MAVEN_REACTOR_ROOT, root.toString());
            assertEquals(root.toAbsolutePath().normalize(), missing.binding().gate().reactorRoot());
        } finally {
            restore(previous);
        }
    }

    @Test
    public void published_surface_is_the_hook() throws NoSuchMethodException, NoSuchFieldException {
        assertTrue(Modifier.isPublic(ApiQualityTest.class.getModifiers()));
        assertTrue(Modifier.isPublic(RolesTest.class.getModifiers()));
        assertTrue(Modifier.isPublic(LayeringTest.class.getModifiers()));
        assertFalse(Modifier.isPublic(ArchitectureHook.class.getModifiers()));
        assertFalse(Modifier.isPublic(ReactorBinding.class.getModifiers()));
        assertFalse(Modifier.isPublic(ReactorModule.class.getModifiers()));
        assertFalse(Modifier.isPublic(ConsumerGate.class.getModifiers()));
        Method binding = ArchitectureHook.class.getDeclaredMethod("binding");
        assertFalse(Modifier.isPublic(binding.getModifiers()));
        assertFalse(Modifier.isProtected(binding.getModifiers()));
        assertTrue(Modifier.isProtected(ArchitectureHook.class.getDeclaredMethod("utilityArtifacts").getModifiers()));
        assertTrue(Modifier.isProtected(ArchitectureHook.class.getDeclaredMethod("reactorRoot").getModifiers()));
        assertTrue(Modifier.isProtected(ArchitectureHook.class.getDeclaredMethod("groupPrefix").getModifiers()));
        assertTrue(Modifier.isProtected(LayeringTest.class.getDeclaredMethod("exceptionPackage").getModifiers()));
        assertTrue(Modifier.isPublic(LayeringTest.class.getField("RECORDED_JAVA").getModifiers()));
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
