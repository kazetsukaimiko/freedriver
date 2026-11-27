package io.freedriver.architecture;

/**
 * The layering rules for one module. A repository's {@code ArchitectureTest} extends this, names
 * its exception package, and inherits the consumer hook on {@link ApiQualityTest}.
 *
 * <p>The frozen layering checks from freedriver-web {@code 6e25531c} join this class in the
 * implementation commits of the same pull request.
 */
public abstract class LayeringTest extends ApiQualityTest {

    /** The Java feature version the stores under {@code archunit-store} are recorded on. */
    public static final int RECORDED_JAVA = 23;

    /** The one package that holds this module's domain exceptions. */
    protected abstract String exceptionPackage();
}
