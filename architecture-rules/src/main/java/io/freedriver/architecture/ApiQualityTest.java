package io.freedriver.architecture;

/**
 * The API quality gate for one module. A repository's architecture test extends this, or extends
 * {@link LayeringTest}, and names the utility modules and, when the Maven reactor root is the wrong
 * tree, the root to read.
 *
 * <p>The hook is {@link ArchitectureHook#utilityArtifacts()}, {@link ArchitectureHook#reactorRoot()}
 * and {@link ArchitectureHook#groupPrefix()}. The rule methods from freedriver-web {@code 6e25531c}
 * join this class in the implementation commits of the same pull request.
 */
public abstract class ApiQualityTest extends ArchitectureHook {
}
