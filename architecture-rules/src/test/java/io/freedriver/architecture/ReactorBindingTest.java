package io.freedriver.architecture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class ReactorBindingTest {

    private static final List<String> WEB_UTILITIES = List.of(
            "stereotypes", "mcp-support", "hit-window", "freedriver-mqtt");

    @Test
    public void example_root_maps_a_directory_name_to_its_artifact_id() throws IOException {
        Path root = copy("example-root");
        Files.createDirectories(root.resolve("dir-not-artifact").resolve("target").resolve("classes"));
        ReactorBinding binding = ReactorBinding.bind(root, WEB_UTILITIES, ReactorBinding.DEFAULT_GROUP_PREFIX);
        assertEquals(WEB_UTILITIES, binding.gate().utilityArtifacts());
        assertEquals("io.freedriver.example", binding.jarModules().get(0).groupId());
        assertEquals("widget-api", binding.jarModules().get(0).artifactId());
        assertEquals("dir-not-artifact", binding.jarModules().get(0).directory().getFileName().toString());
        binding.requireCompiledOutput(binding.jarModules().get(0));
    }

    @Test
    public void utility_list_keeps_its_order_and_rejects_later_edits() throws IOException {
        Path root = copy("example-root");
        List<String> utilities = new java.util.ArrayList<>(WEB_UTILITIES);
        ConsumerGate gate = ReactorBinding.bind(root, utilities, ReactorBinding.DEFAULT_GROUP_PREFIX).gate();
        utilities.set(0, "changed");
        assertEquals(WEB_UTILITIES, gate.utilityArtifacts());
        assertThrows(UnsupportedOperationException.class, () -> gate.utilityArtifacts().set(0, "changed"));
    }

    @Test
    public void nested_consumer_stays_inside_its_own_pom() throws IOException {
        Path parent = copy("unrelated-parent");
        Path nested = parent.resolve("nested-consumer");
        Files.createDirectories(nested.resolve("widget-dir").resolve("target").resolve("classes"));
        ReactorBinding binding = ReactorBinding.bind(nested, WEB_UTILITIES, ReactorBinding.DEFAULT_GROUP_PREFIX);
        List<String> artifacts = binding.modules().stream().map(ReactorModule::artifactId).toList();
        assertEquals(List.of("nested-consumer", "widget-api"), artifacts);
        assertEquals("widget-dir", binding.jarModules().get(0).directory().getFileName().toString());
        assertFalse(artifacts.contains("unrelated-parent"));
        assertFalse(artifacts.contains("sibling-api"));
    }

    @Test
    public void exact_io_freedriver_group_is_accepted() throws IOException {
        Path root = copy("unrelated-parent");
        Files.createDirectories(root.resolve("sibling").resolve("target").resolve("classes"));
        ReactorBinding binding = ReactorBinding.bind(root, List.of(), ReactorBinding.DEFAULT_GROUP_PREFIX);
        assertEquals("sibling-api", binding.jarModules().get(0).artifactId());
        assertEquals("io.freedriver", binding.jarModules().get(0).groupId());
    }

    @Test
    public void group_id_comes_from_the_parent_pom_when_the_parent_element_omits_it() throws IOException {
        Path root = copy("inherited-group");
        ReactorModule child = ReactorBinding.bind(root, List.of(), ReactorBinding.DEFAULT_GROUP_PREFIX)
                .jarModules().get(0);
        assertEquals("child-api", child.artifactId());
        assertEquals("io.freedriver.example", child.groupId());
    }

    @Test
    public void a_prefix_the_subclass_supplies_replaces_the_default() throws IOException {
        Path root = Files.createTempDirectory("architecture-rules-prefix");
        Files.writeString(root.resolve("pom.xml"), """
                <?xml version="1.0" encoding="UTF-8"?>
                <project>
                    <groupId>com.acme.demo</groupId>
                    <artifactId>demo-api</artifactId>
                    <version>1.0.0</version>
                    <packaging>jar</packaging>
                </project>
                """);
        ReactorBinding accepted = ReactorBinding.bind(root, List.of("util"), "com.acme");
        assertEquals("demo-api", accepted.jarModules().get(0).artifactId());
        IllegalStateException rejected = assertThrows(IllegalStateException.class,
                () -> ReactorBinding.bind(root, List.of(), ReactorBinding.DEFAULT_GROUP_PREFIX));
        assertTrue(rejected.getMessage().contains("com.acme.demo"));
        assertTrue(rejected.getMessage().contains("looked for " + ReactorBinding.DEFAULT_GROUP_PREFIX));
    }

    @Test
    public void io_freedriver_prefix_rejects_a_group_id_that_only_shares_the_letters() {
        assertTrue(ReactorBinding.groupAccepted("io.freedriver", ReactorBinding.DEFAULT_GROUP_PREFIX));
        assertTrue(ReactorBinding.groupAccepted("io.freedriver.example", ReactorBinding.DEFAULT_GROUP_PREFIX));
        assertFalse(ReactorBinding.groupAccepted("io.freedriverx", ReactorBinding.DEFAULT_GROUP_PREFIX));
        assertFalse(ReactorBinding.groupAccepted("com.example", ReactorBinding.DEFAULT_GROUP_PREFIX));
    }

    @Test
    public void missing_root_pom_names_the_path_that_was_looked_for() throws IOException {
        Path root = Files.createTempDirectory("architecture-rules-missing");
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> ReactorBinding.bind(root, WEB_UTILITIES, ReactorBinding.DEFAULT_GROUP_PREFIX));
        assertTrue(failure.getMessage().contains(root.resolve("pom.xml").toString()));
        assertTrue(failure.getMessage().contains("looked for"));
    }

    @Test
    public void foreign_root_names_the_group_id_and_the_prefix() throws IOException {
        Path root = copy("foreign-root");
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> ReactorBinding.bind(root, WEB_UTILITIES, ReactorBinding.DEFAULT_GROUP_PREFIX));
        assertTrue(failure.getMessage().contains("com.example"));
        assertTrue(failure.getMessage().contains("looked for io.freedriver"));
    }

    @Test
    public void empty_module_set_fails_and_names_the_root() throws IOException {
        Path root = copy("empty-set");
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> ReactorBinding.bind(root, WEB_UTILITIES, ReactorBinding.DEFAULT_GROUP_PREFIX));
        assertTrue(failure.getMessage().contains("empty module set"));
        assertTrue(failure.getMessage().contains(root.toRealPath().toString())
                || failure.getMessage().contains(root.toAbsolutePath().normalize().toString()));
        assertTrue(failure.getMessage().contains("looked for"));
    }

    @Test
    public void missing_compiled_output_names_the_module_and_the_classes_directory() throws IOException {
        Path root = copy("missing-output");
        ReactorBinding binding = ReactorBinding.bind(root, WEB_UTILITIES, ReactorBinding.DEFAULT_GROUP_PREFIX);
        ReactorModule bare = binding.jarModules().get(0);
        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> binding.requireCompiledOutput(bare));
        assertTrue(failure.getMessage().contains("bare-api"));
        assertTrue(failure.getMessage().contains("target" + bare.directory().getFileSystem().getSeparator() + "classes")
                || failure.getMessage().contains("target/classes")
                || failure.getMessage().contains("target\\classes"));
        assertTrue(failure.getMessage().contains("looked for"));
    }

    @Test
    public void maven_reactor_root_reads_the_pom_surefire_was_given() throws IOException {
        Path root = ReactorBinding.mavenReactorRoot().toAbsolutePath().normalize();
        String pom = Files.readString(root.resolve("pom.xml"));
        assertTrue(pom.contains("<module>architecture-rules</module>"), root.toString());
    }

    @Test
    public void binding_checks_compiled_output_for_the_module_under_test() {
        ApiQualityTest test = new ApiQualityTest() {
            @Override
            protected List<String> utilityArtifacts() {
                return List.of();
            }
        };
        ReactorBinding binding = test.binding();
        assertSame(binding, test.binding());
        Path here = Path.of("").toAbsolutePath().normalize();
        ReactorModule underTest = binding.modules().stream()
                .filter(module -> here.equals(module.directory()))
                .findFirst()
                .orElseThrow();
        assertEquals("architecture-rules", underTest.artifactId());
        binding.requireCompiledOutput(underTest);
    }

    @Test
    public void maven_reactor_root_reads_the_surefire_property() {
        String previous = System.getProperty(ReactorBinding.MAVEN_REACTOR_ROOT);
        Path root = Path.of("src").toAbsolutePath();
        try {
            System.setProperty(ReactorBinding.MAVEN_REACTOR_ROOT, root.toString());
            assertEquals(root, ReactorBinding.mavenReactorRoot().toAbsolutePath().normalize());
            System.clearProperty(ReactorBinding.MAVEN_REACTOR_ROOT);
            IllegalStateException failure = assertThrows(IllegalStateException.class, ReactorBinding::mavenReactorRoot);
            assertTrue(failure.getMessage().contains(ReactorBinding.MAVEN_REACTOR_ROOT));
            assertTrue(failure.getMessage().contains("systemPropertyVariables"));
            assertTrue(failure.getMessage().contains("maven-surefire-plugin"));
            assertTrue(failure.getMessage().contains("looked for"));
        } finally {
            if (previous == null) {
                System.clearProperty(ReactorBinding.MAVEN_REACTOR_ROOT);
            } else {
                System.setProperty(ReactorBinding.MAVEN_REACTOR_ROOT, previous);
            }
        }
    }

    @Test
    public void compiled_classes_omit_freedriver_web_reactor_names() throws IOException {
        Path classes = Path.of("target", "classes");
        try (Stream<Path> walk = Files.walk(classes)) {
            List<Path> files = walk.filter(path -> path.toString().endsWith(".class")).toList();
            assertFalse(files.isEmpty());
            for (Path file : files) {
                String text = Files.readString(file, StandardCharsets.ISO_8859_1);
                assertFalse(text.contains("freedriver-web"), file.toString());
                assertFalse(text.contains("mqtt-contract"), file.toString());
            }
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
