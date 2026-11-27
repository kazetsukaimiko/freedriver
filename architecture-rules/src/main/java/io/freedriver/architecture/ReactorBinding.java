package io.freedriver.architecture;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The consuming Maven build's reactor, read from a root the build or a test subclass supplies.
 *
 * <p>The root is that directory alone. A parent pom that happens to contain the directory is another
 * reactor, and it stays out of this binding unless the root pom lists it as a module.
 */
final class ReactorBinding {

    /** Group prefix that accepts {@code io.freedriver} and {@code io.freedriver.*}. */
    public static final String DEFAULT_GROUP_PREFIX = "io.freedriver";

    /**
     * System property holding the consuming build's reactor root. Surefire leaves it unset. The
     * consumer passes it through Surefire {@code systemPropertyVariables}:
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
     */
    public static final String MAVEN_REACTOR_ROOT = "maven.multiModuleProjectDirectory";

    private final ConsumerGate gate;
    private final List<ReactorModule> modules;

    private ReactorBinding(ConsumerGate gate, List<ReactorModule> modules) {
        this.gate = gate;
        this.modules = modules;
    }

    /**
     * The reactor root the consuming build passed through Surefire {@code systemPropertyVariables}.
     *
     * @throws IllegalStateException when {@value #MAVEN_REACTOR_ROOT} is unset or blank. The
     *         message names the {@code maven-surefire-plugin} {@code systemPropertyVariables}
     *         configuration that passes the property.
     */
    public static Path mavenReactorRoot() {
        String value = System.getProperty(MAVEN_REACTOR_ROOT);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("reactor root cannot be resolved: looked for system property "
                    + MAVEN_REACTOR_ROOT + ". Pass it through maven-surefire-plugin systemPropertyVariables: "
                    + "<maven.multiModuleProjectDirectory>${maven.multiModuleProjectDirectory}"
                    + "</maven.multiModuleProjectDirectory>");
        }
        return Path.of(value);
    }

    /**
     * Whether {@code groupId} equals {@code groupPrefix} or starts with {@code groupPrefix + "."}.
     */
    public static boolean groupAccepted(String groupId, String groupPrefix) {
        if (groupId == null || groupId.isBlank() || groupPrefix == null || groupPrefix.isBlank()) {
            return false;
        }
        return groupId.equals(groupPrefix) || groupId.startsWith(groupPrefix + ".");
    }

    /**
     * Reads {@code reactorRoot}'s pom and the modules it lists.
     *
     * @throws IllegalStateException when the root pom is missing, the root group id falls outside
     *         {@code groupPrefix}, a listed module pom is missing, or the reactor has no jar module
     */
    public static ReactorBinding bind(Path reactorRoot, List<String> utilityArtifacts, String groupPrefix) {
        if (reactorRoot == null) {
            throw new IllegalArgumentException("reactorRoot is required");
        }
        ConsumerGate gate = new ConsumerGate(reactorRoot, utilityArtifacts, groupPrefix);
        Path root = gate.reactorRoot();
        Path pom = root.resolve("pom.xml");
        if (!Files.isRegularFile(pom)) {
            throw new IllegalStateException("reactor root cannot be resolved: looked for " + pom);
        }
        Element project = parse(pom);
        String groupId = groupIdOf(project, pom, new HashSet<>());
        if (!groupAccepted(groupId, gate.groupPrefix())) {
            throw new IllegalStateException("reactor root " + root + " has groupId " + shown(groupId)
                    + ": looked for " + gate.groupPrefix() + " or a groupId that starts with "
                    + gate.groupPrefix() + ".");
        }
        List<ReactorModule> modules = new ArrayList<>();
        walk(pom, gate.groupPrefix(), modules, new HashSet<>());
        List<ReactorModule> jars = modules.stream().filter(ReactorModule::jar).toList();
        if (jars.isEmpty()) {
            throw new IllegalStateException("empty module set at reactor root " + root
                    + ": looked for a jar module whose groupId is " + gate.groupPrefix()
                    + " or starts with " + gate.groupPrefix() + ".");
        }
        return new ReactorBinding(gate, List.copyOf(modules));
    }

    /** The gate this binding was read from. */
    public ConsumerGate gate() {
        return gate;
    }

    /** Modules whose group id the prefix accepts, aggregators included, in walk order. */
    public List<ReactorModule> modules() {
        return modules;
    }

    /** Jar modules. Verification judges these, and an empty list fails in {@link #bind}. */
    public List<ReactorModule> jarModules() {
        return modules.stream().filter(ReactorModule::jar).toList();
    }

    /**
     * Fails when a jar module has no {@code target/classes} directory.
     *
     * @throws IllegalStateException naming the artifact id and the directory that was looked for
     */
    public void requireCompiledOutput(ReactorModule module) {
        if (module == null) {
            throw new IllegalArgumentException("module is required");
        }
        if (!module.jar()) {
            return;
        }
        Path classes = module.directory().resolve("target").resolve("classes");
        if (!Files.isDirectory(classes)) {
            throw new IllegalStateException("module " + module.artifactId() + " at " + module.directory()
                    + " has no compiled output: looked for " + classes);
        }
    }

    private static void walk(Path pom, String groupPrefix, List<ReactorModule> modules, Set<Path> seen) {
        Path normalized = pom.toAbsolutePath().normalize();
        if (!seen.add(normalized)) {
            return;
        }
        if (!Files.isRegularFile(normalized)) {
            throw new IllegalStateException("reactor module cannot be resolved: looked for " + normalized);
        }
        Element project = parse(normalized);
        String artifactId = childText(project, "artifactId");
        if (artifactId == null || artifactId.isBlank()) {
            throw new IllegalStateException("reactor module cannot be resolved: looked for artifactId in " + normalized);
        }
        String groupId = groupIdOf(project, normalized, new HashSet<>());
        String packaging = childText(project, "packaging");
        if (packaging == null || packaging.isBlank()) {
            packaging = "jar";
        }
        if (groupAccepted(groupId, groupPrefix)) {
            modules.add(new ReactorModule(normalized.getParent(), groupId, artifactId, packaging));
        }
        Element listed = child(project, "modules");
        if (listed == null) {
            return;
        }
        for (Element module : children(listed, "module")) {
            String relative = text(module);
            if (relative.isBlank()) {
                continue;
            }
            walk(normalized.getParent().resolve(relative).resolve("pom.xml").normalize(), groupPrefix, modules, seen);
        }
    }

    private static String groupIdOf(Element project, Path pom, Set<Path> visiting) {
        String own = childText(project, "groupId");
        if (own != null && !own.isBlank()) {
            return own.trim();
        }
        Element parent = child(project, "parent");
        if (parent == null) {
            return "";
        }
        String inherited = childText(parent, "groupId");
        if (inherited != null && !inherited.isBlank()) {
            return inherited.trim();
        }
        Path parentPom = parentPom(pom, parent);
        if (parentPom != null && visiting.add(parentPom.normalize()) && Files.isRegularFile(parentPom)) {
            return groupIdOf(parse(parentPom), parentPom, visiting);
        }
        return "";
    }

    private static Path parentPom(Path pom, Element parent) {
        String relative = childText(parent, "relativePath");
        Path base = pom.getParent();
        if (base == null) {
            return null;
        }
        String path = relative == null || relative.isBlank() ? "../pom.xml" : relative;
        return base.resolve(path).normalize();
    }

    private static String shown(String groupId) {
        return groupId == null || groupId.isBlank() ? "<none>" : groupId;
    }

    private static Element parse(Path pom) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newDocumentBuilder().parse(pom.toFile()).getDocumentElement();
        } catch (Exception e) {
            throw new IllegalStateException("reactor pom cannot be read: looked for " + pom.toAbsolutePath(), e);
        }
    }

    private static Element child(Element parent, String name) {
        if (parent == null) {
            return null;
        }
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element element && name.equals(localName(element))) {
                return element;
            }
        }
        return null;
    }

    private static List<Element> children(Element parent, String name) {
        List<Element> found = new ArrayList<>();
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element element && name.equals(localName(element))) {
                found.add(element);
            }
        }
        return found;
    }

    private static String childText(Element parent, String name) {
        Element element = child(parent, name);
        if (element == null) {
            return null;
        }
        return text(element);
    }

    private static String text(Element element) {
        String value = element.getTextContent();
        return value == null ? "" : value.trim();
    }

    private static String localName(Node node) {
        return node.getLocalName() != null ? node.getLocalName() : node.getNodeName();
    }
}
