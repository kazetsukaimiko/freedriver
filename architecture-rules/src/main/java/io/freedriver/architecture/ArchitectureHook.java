package io.freedriver.architecture;

import java.nio.file.Path;
import java.util.List;

/**
 * The consumer hook for {@link ApiQualityTest} and {@link RolesTest}, declared once so the two
 * bases stay on the same root, utility list and group prefix.
 */
abstract class ArchitectureHook {

    private ReactorBinding bound;

    /**
     * Utility artifact ids in the order rule descriptions print them. Freedriver-web's list is
     * {@code stereotypes}, {@code mcp-support}, {@code hit-window}, {@code freedriver-mqtt}.
     */
    protected abstract List<String> utilityArtifacts();

    /**
     * Reactor root of the consuming build. The default reads
     * {@value ReactorBinding#MAVEN_REACTOR_ROOT} after the consumer passes that property through
     * Surefire {@code systemPropertyVariables}:
     *
     * <pre>
     * &lt;plugin&gt;
     *   &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
     *   &lt;configuration&gt;
     *     &lt;systemPropertyVariables&gt;
     *       &lt;maven.multiModuleProjectDirectory&gt;${maven.multiModuleProjectDirectory}&lt;/maven.multiModuleProjectDirectory&gt;
     *     &lt;/systemPropertyVariables&gt;
     *   &lt;/configuration&gt;
     * &lt;/plugin&gt;
     * </pre>
     *
     * A test overrides this to hand the gate a fixture root.
     */
    protected Path reactorRoot() {
        return ReactorBinding.mavenReactorRoot();
    }

    /**
     * Group id the reactor root must equal, or a prefix it must start with followed by a dot.
     * The default accepts {@code io.freedriver} and {@code io.freedriver.*}.
     */
    protected String groupPrefix() {
        return ReactorBinding.DEFAULT_GROUP_PREFIX;
    }

    /**
     * The reactor read for this test instance. The first call binds the root and requires compiled
     * output for the module whose directory is the working directory, when that module is in the
     * reactor. Later calls return the same binding.
     */
    final ReactorBinding binding() {
        if (bound == null) {
            bound = ReactorBinding.bind(reactorRoot(), utilityArtifacts(), groupPrefix());
            requireModuleUnderTest(bound);
        }
        return bound;
    }

    /** Sibling modules stay unchecked. A single-module build leaves the others unbuilt. */
    private static void requireModuleUnderTest(ReactorBinding binding) {
        Path here = Path.of("").toAbsolutePath().normalize();
        for (ReactorModule module : binding.modules()) {
            if (here.equals(module.directory())) {
                binding.requireCompiledOutput(module);
                return;
            }
        }
    }
}
