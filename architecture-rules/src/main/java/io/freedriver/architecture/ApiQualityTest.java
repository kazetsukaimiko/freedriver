package io.freedriver.architecture;

import java.nio.file.Path;
import java.util.List;

/**
 * The API quality gate for one module. A repository's architecture test extends this, or extends
 * {@link LayeringTest}, and names the utility modules and, when the Maven reactor root is the wrong
 * tree, the root to read.
 *
 * <p>The rule methods from freedriver-web {@code 6e25531c} join this class in the implementation
 * commits of the same pull request. This commit is the consumer hook those rules read.
 */
public abstract class ApiQualityTest {

    /**
     * Utility artifact ids in the order rule descriptions print them. Freedriver-web's list is
     * {@code stereotypes}, {@code mcp-support}, {@code hit-window}, {@code freedriver-mqtt}.
     */
    protected abstract List<String> utilityArtifacts();

    /**
     * Reactor root of the consuming build. The default is {@value ReactorBinding#MAVEN_REACTOR_ROOT}.
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

    /** The binding the rules read. Fails with a diagnostic when the root cannot be resolved. */
    protected final ConsumerGate gate() {
        return ReactorBinding.bind(reactorRoot(), utilityArtifacts(), groupPrefix()).gate();
    }
}
