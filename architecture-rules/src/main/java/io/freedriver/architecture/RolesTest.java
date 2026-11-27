package io.freedriver.architecture;

import java.nio.file.Path;
import java.util.List;

/**
 * The architecture-role rules for one module. A repository's {@code ClassRolesTest} extends this
 * and names the same utility list, reactor root and group prefix as its {@link ApiQualityTest}.
 *
 * <p>The three role checks from freedriver-web {@code 6e25531c} join this class in the implementation
 * commits of the same pull request. This commit is the consumer hook those rules read.
 */
public abstract class RolesTest {

    /**
     * Utility artifact ids in the order rule descriptions print them. The same list
     * {@link ApiQualityTest} supplies.
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

    /** The binding the role rules read. Fails with a diagnostic when the root cannot be resolved. */
    protected final ConsumerGate gate() {
        return ReactorBinding.bind(reactorRoot(), utilityArtifacts(), groupPrefix()).gate();
    }
}
