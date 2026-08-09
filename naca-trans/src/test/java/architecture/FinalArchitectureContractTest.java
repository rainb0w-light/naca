package architecture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/** Final, zero-tolerance contract for a target-neutral semantic model. */
@Tag("final-architecture")
class FinalArchitectureContractTest
{
    private static final List<String> SEMANTIC_BACKEND_TOKENS = List.of(
        "generate.",
        "org.stringtemplate",
        "CBaseLanguageExporter",
        "TemplateLoader",
        "CJava",
        "CFPacJava",
        "CGo",
        "CRust");

    private static final List<Pattern> SEMANTIC_OUTPUT_PROTOCOL_PATTERNS = List.of(
        Pattern.compile("\\b(?:Export|DoExport)[A-Za-z0-9_]*\\s*\\("),
        Pattern.compile("\\b(?:WriteLine|WriteWord|WriteEOL|WriteLongString"
            + "|StartOutputBloc|EndOutputBloc|FormatIdentifier)\\s*\\("),
        Pattern.compile("\\bget(?:CodeString|ChildrenCode|ReferenceString|BodyCode)\\s*\\("));

    private static final List<String> TEMPLATE_PRE_RENDERED_HOOKS = List.of(
        "codeString",
        "childrenCode",
        "referenceString",
        "bodyCode",
        ".export",
        ".Export");

    @TestFactory
    Stream<DynamicTest> everySemanticClassSatisfiesTheFinalContract() throws IOException
    {
        return javaFiles(moduleRoot().resolve("src/main/java/semantic")).stream()
            .map(file -> DynamicTest.dynamicTest(relative(file), () -> {
                String source = executableSource(read(file));
                List<String> violations = new ArrayList<>();
                collectPresentTokens(violations, file, source,
                    SEMANTIC_BACKEND_TOKENS, "backend dependency");
                collectMatchingPatterns(violations, file, source,
                    SEMANTIC_OUTPUT_PROTOCOL_PATTERNS, "output protocol");
                assertNoViolations("semantic class contract", violations);
            }));
    }

    @TestFactory
    Stream<DynamicTest> everyTemplateConsumesSemanticValuesInsteadOfPreRenderedSource()
        throws IOException
    {
        List<Path> templates = files(
            moduleRoot().resolve("src/main/resources/templates"),
            path -> path.toString().endsWith(".stg"));
        return templates.stream().map(file -> DynamicTest.dynamicTest(relative(file), () -> {
            List<String> violations = new ArrayList<>();
            collectPresentTokens(violations, file, read(file),
                TEMPLATE_PRE_RENDERED_HOOKS, "pre-rendered template hook");
            assertNoViolations("template contract", violations);
        }));
    }

    @TestFactory
    Stream<DynamicTest> everyRecursiveBackendClassContainsNoTypedRenderer()
        throws IOException
    {
        Path rendererRoot = moduleRoot().resolve(
            "src/main/java/generate/templates/recursive");
        return javaFiles(rendererRoot).stream()
            .map(file -> DynamicTest.dynamicTest(relative(file), () -> assertTrue(
                !read(file).contains("implements JavaTemplateRenderer"),
                () -> relative(file)
                    + ": per-semantic-type Java renderer; move structure/selection to STG")));
    }

    @Test
    void backendHasACompleteDeclarativeSemanticTemplateManifest() throws Exception
    {
        Path manifest = moduleRoot().resolve(
            "src/main/resources/templates/java/semantic-bindings.properties");
        assertTrue(Files.isRegularFile(manifest), () -> relative(manifest)
            + ": missing declarative semantic-type to template manifest");

        Properties bindings = new Properties();
        try (InputStream input = Files.newInputStream(manifest))
        {
            bindings.load(input);
        }

        Set<String> expectedTypes = concreteSemanticEntityTypes();
        Set<String> boundTypes = new TreeSet<>(bindings.stringPropertyNames());
        assertEquals(expectedTypes, boundTypes,
            "Manifest keys must exactly cover every concrete semantic entity type");

        Path runtimeManifest = moduleRoot().resolve(
            "src/main/resources/templates/java/semantic-runtime-bindings.properties");
        assertTrue(Files.isRegularFile(runtimeManifest), () -> relative(runtimeManifest)
            + ": runtime dispatch aliases must be kept in a separate manifest");
        Properties runtimeBindings = new Properties();
        try (InputStream input = Files.newInputStream(runtimeManifest))
        {
            runtimeBindings.load(input);
        }
        Set<String> runtimeTypes = new TreeSet<>(runtimeBindings.stringPropertyNames());
        assertTrue(Collections.disjoint(boundTypes, runtimeTypes),
            "Concrete inventory and runtime alias manifests must not overlap");

        String templateSources = readAllTemplates();
        for (String type : boundTypes)
        {
            String templateName = bindings.getProperty(type).trim();
            assertTrue(!templateName.isEmpty(), type + " has an empty template binding");
            Pattern definition = Pattern.compile(
                "(?m)^\\s*" + Pattern.quote(templateName) + "\\s*\\(");
            assertTrue(definition.matcher(templateSources).find(),
                type + " maps to missing template `" + templateName + "`");
        }
    }


    @Test
    void semanticTypeInventoryLoadsEveryConcreteEntityClass() throws Exception
    {
        assertTrue(!concreteSemanticEntityTypes().isEmpty(),
            "No concrete semantic entity types were discovered");
    }

    @Test
    void legacyTypedRendererInfrastructureIsAbsent() throws IOException
    {
        Path recursiveRoot = moduleRoot().resolve(
            "src/main/java/generate/templates/recursive");
        Path assembler = recursiveRoot.resolve("JavaTemplateAssembler.java");
        String source = read(assembler);
        assertTrue(!source.contains("JavaTemplateRendererRegistry")
                && !source.contains("requireRenderer"),
            "The final assembler must fail on missing declarative bindings, not fall back"
                + " to typed renderers");
        assertTrue(!Files.exists(recursiveRoot.resolve("JavaTemplateRenderer.java"))
                && !Files.exists(recursiveRoot.resolve("JavaTemplateRendererRegistry.java"))
                && !Files.exists(recursiveRoot.resolve(
                    "java/JavaRecursiveRendererRegistry.java")),
            "Legacy typed renderer interfaces and registries must be deleted");
    }

    @TestFactory
    Stream<DynamicTest> everyEntityFactoryAvoidsTargetSpecificEntitySubclasses()
        throws IOException
    {
        Path javaFactoryRoot = moduleRoot().resolve("src/main/java/generate");
        List<Path> factories = files(javaFactoryRoot,
            path -> path.getFileName().toString().contains("EntityFactory")
                && path.toString().endsWith(".java")
                && !path.getFileName().toString().contains("FPac"));
        return factories.stream().map(file -> DynamicTest.dynamicTest(relative(file), () -> {
            List<String> violations = new ArrayList<>();
            collectPresentTokens(violations, file, read(file),
                List.of("new CJava", "new CFPacJava", "new CGo", "new CRust"),
                "target-specific semantic factory construction");
            assertNoViolations("entity factory contract", violations);
        }));
    }

    @TestFactory
    Stream<DynamicTest> everyDirectBackendClassAvoidsSemanticInheritance() throws IOException
    {
        Path directRoot = moduleRoot().resolve("src/main/java/generate/java");
        return javaFiles(directRoot).stream()
            .map(file -> DynamicTest.dynamicTest(relative(file), () -> {
                String source = read(file);
                assertTrue(!source.contains(" extends CEntity")
                        && !source.contains(" extends CBaseActionEntity")
                        && !source.contains(" extends CDataEntity"),
                    () -> relative(file)
                        + ": target backend class subclasses a semantic entity");
            }));
    }

    @Test
    void onlyTheAssemblerRootFlattensTheTemplateTree() throws IOException
    {
        Path recursiveRoot = moduleRoot().resolve(
            "src/main/java/generate/templates/recursive");
        List<String> occurrences = new ArrayList<>();
        for (Path file : javaFiles(recursiveRoot))
        {
            List<String> lines = Files.readAllLines(file, StandardCharsets.ISO_8859_1);
            for (int index = 0; index < lines.size(); index++)
            {
                if (lines.get(index).contains(".render("))
                {
                    occurrences.add(relative(file) + ":" + (index + 1));
                }
            }
        }
        assertEquals(1, occurrences.size(),
            "Exactly one ST tree flattening call is allowed: " + occurrences);
        assertTrue(occurrences.getFirst().startsWith(
            "src/main/java/generate/templates/recursive/JavaTemplateAssembler.java:"),
            "Only JavaTemplateAssembler.renderRoot may flatten the ST tree: " + occurrences);
    }

    private static void collectPresentTokens(
        List<String> violations,
        Path file,
        String source,
        List<String> forbiddenTokens,
        String category)
    {
        for (String token : forbiddenTokens)
        {
            if (source.contains(token))
            {
                violations.add(relative(file) + ": " + category + " `" + token + "`");
            }
        }
    }

    private static void collectMatchingPatterns(
        List<String> violations,
        Path file,
        String source,
        List<Pattern> forbiddenPatterns,
        String category)
    {
        for (Pattern pattern : forbiddenPatterns)
        {
            if (pattern.matcher(source).find())
            {
                violations.add(relative(file) + ": " + category
                    + " matching `" + pattern + "`");
            }
        }
    }

    private static void assertNoViolations(String category, List<String> violations)
    {
        assertTrue(violations.isEmpty(), () -> category + " violations ("
            + violations.size() + "):\n" + String.join("\n", violations));
    }

    private static List<Path> javaFiles(Path root) throws IOException
    {
        return files(root, path -> path.toString().endsWith(".java"));
    }

    private static List<Path> files(Path root, Predicate<Path> predicate) throws IOException
    {
        try (Stream<Path> paths = Files.walk(root))
        {
            return paths.filter(Files::isRegularFile)
                .filter(predicate)
                .sorted(Comparator.comparing(Path::toString))
                .toList();
        }
    }

    private static String read(Path file) throws IOException
    {
        return new String(Files.readAllBytes(file), StandardCharsets.ISO_8859_1);
    }

    private static String executableSource(String source)
    {
        return source
            .replaceAll("(?s)/\\*.*?\\*/", "")
            .replaceAll("(?m)//.*$", "");
    }

    private static Set<String> concreteSemanticEntityTypes() throws Exception
    {
        Path semanticRoot = moduleRoot().resolve("src/main/java/semantic");
        ClassLoader classLoader = FinalArchitectureContractTest.class.getClassLoader();
        Class<?> baseType = Class.forName(
            "semantic.CBaseLanguageEntity", false, classLoader);
        Set<String> types = new TreeSet<>();
        for (Path file : javaFiles(semanticRoot))
        {
            if (isBmsSemanticPath(file))
            {
                continue;
            }
            String suffix = semanticRoot.relativize(file).toString()
                .replace(file.getFileSystem().getSeparator(), ".")
                .replaceFirst("\\.java$", "");
            Class<?> type = Class.forName("semantic." + suffix, false, classLoader);
            if (baseType.isAssignableFrom(type)
                && !type.isInterface()
                && !Modifier.isAbstract(type.getModifiers()))
            {
                types.add(type.getName());
            }
        }
        return types;
    }

    private static String readAllTemplates() throws IOException
    {
        StringBuilder source = new StringBuilder();
        for (Path template : files(moduleRoot().resolve("src/main/resources/templates"),
            path -> path.toString().endsWith(".stg")))
        {
            source.append(read(template)).append('\n');
        }
        return source.toString();
    }

    private static String relative(Path path)
    {
        return moduleRoot().relativize(path).toString();
    }

    /**
     * BMS map/resource generation is a separate artifact pipeline. The final
     * architecture gate in this module covers the COBOL semantic tree plus
     * embedded SQL/CICS only, matching the migration ledger's queue scopes.
     */
    private static boolean isBmsSemanticPath(Path path)
    {
        return "CBaseResourceEntity.java".equals(
                path.getFileName().toString())
            || path.toString().contains(
            path.getFileSystem().getSeparator() + "semantic"
                + path.getFileSystem().getSeparator() + "forms"
                + path.getFileSystem().getSeparator());
    }

    private static Path moduleRoot()
    {
        Path current = Path.of("").toAbsolutePath().normalize();
        if (Files.isDirectory(current.resolve("src/main/java")))
        {
            return current;
        }
        Path module = current.resolve("naca-trans");
        if (Files.isDirectory(module.resolve("src/main/java")))
        {
            return module;
        }
        throw new IllegalStateException("Cannot locate naca-trans from " + current);
    }
}
