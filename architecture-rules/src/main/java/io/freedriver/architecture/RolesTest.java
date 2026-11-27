package io.freedriver.architecture;

/**
 * The architecture-role rules for one module. A repository's {@code ClassRolesTest} extends this
 * and names the same utility list, reactor root and group prefix as its {@link ApiQualityTest}.
 *
 * <p>The hook is {@link ArchitectureHook#utilityArtifacts()}, {@link ArchitectureHook#reactorRoot()}
 * and {@link ArchitectureHook#groupPrefix()}. The three role checks from freedriver-web
 * {@code 6e25531c} join this class in the implementation commits of the same pull request.
 */
public abstract class RolesTest extends ArchitectureHook {
}
