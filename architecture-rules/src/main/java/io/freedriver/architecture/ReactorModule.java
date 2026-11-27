package io.freedriver.architecture;

import java.nio.file.Path;

/**
 * One module whose group id the consumer's prefix accepts.
 *
 * @param directory the module directory, the parent of its {@code pom.xml}
 * @param groupId the module's group id, inherited from {@code <parent>} when the pom omits it
 * @param artifactId the module's artifact id
 * @param packaging the pom packaging; {@code jar} when the pom omits it
 */
public record ReactorModule(Path directory, String groupId, String artifactId, String packaging) {

    public ReactorModule {
        if (directory == null || artifactId == null || artifactId.isBlank()) {
            throw new IllegalArgumentException("a reactor module needs a directory and an artifactId");
        }
        directory = directory.toAbsolutePath().normalize();
    }

    /** A jar module has compiled output under {@code target/classes}. A {@code pom} module has none. */
    public boolean jar() {
        return !"pom".equals(packaging);
    }
}
