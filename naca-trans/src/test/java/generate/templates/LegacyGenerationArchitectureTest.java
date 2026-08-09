package generate.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Prevents the measured legacy generation surface from growing during migration. */
class LegacyGenerationArchitectureTest
{
    private static final int BASE_FACTORY_ENTRY_BASELINE = 183;
    private static final int ST_FACTORY_ENTRY_BASELINE = 41;
    private static final int DIRECT_GENERATOR_FILE_BASELINE = 199;
    private static final int SEMANTIC_EXPORTER_IMPORT_BASELINE = 0;
    private static final int ST_CONTROLLER_RENDER_FILE_BASELINE = 0;
    private static final int TEMPLATE_CODE_STRING_BASELINE = 51;
    private static final int TEMPLATE_CHILDREN_CODE_BASELINE = 15;
    private static final int TEMPLATE_REFERENCE_STRING_BASELINE = 39;
    private static final int SEMANTIC_FINAL_CONTRACT_DEBT_BASELINE = 171;
    private static final int TYPED_JAVA_RENDERER_BASELINE = 3;
    private static final int DIRECT_SEMANTIC_SUBCLASS_BASELINE = 207;

    private static final Pattern FACTORY_METHOD = Pattern.compile("NewEntity[A-Za-z0-9_]+");
    private static final Pattern SEMANTIC_FINAL_CONTRACT_DEBT = Pattern.compile(
        "generate\\.|org\\.stringtemplate|CBaseLanguageExporter|TemplateLoader"
            + "|CJava|CFPacJava|CGo|CRust"
            + "|\\b(?:Export|DoExport)[A-Za-z0-9_]*\\s*\\("
            + "|\\b(?:WriteLine|WriteWord|WriteEOL|WriteLongString"
            + "|StartOutputBloc|EndOutputBloc|FormatIdentifier)\\s*\\("
            + "|\\bget(?:CodeString|ChildrenCode|ReferenceString|BodyCode)\\s*\\(");
    private static final Pattern DIRECT_SEMANTIC_SUBCLASS = Pattern.compile(
        " extends (?:CEntity|CBaseActionEntity|CDataEntity)");

    @Test
    void factoryCoverageCannotDecreaseAndTheUnmigratedSurfaceCannotGrow() throws IOException
    {
        Path root = moduleRoot();
        Set<String> baseEntries = matches(
            root.resolve("src/main/java/semantic/CBaseEntityFactory.java"), FACTORY_METHOD);
        Set<String> stEntries = matches(
            root.resolve("src/main/java/generate/CJavaEntityFactory.java"), FACTORY_METHOD);

        assertEquals(BASE_FACTORY_ENTRY_BASELINE, baseEntries.size(),
            "Update the migration manifest when the semantic factory surface changes");
        assertTrue(stEntries.size() >= ST_FACTORY_ENTRY_BASELINE,
            "ST factory coverage decreased from " + ST_FACTORY_ENTRY_BASELINE + " to " + stEntries.size());
        assertTrue(baseEntries.size() - stEntries.size()
                <= BASE_FACTORY_ENTRY_BASELINE - ST_FACTORY_ENTRY_BASELINE,
            "The number of factory entries without ST coverage increased");
    }

    @Test
    void measuredLegacyGenerationDebtCannotGrow() throws IOException
    {
        Path root = moduleRoot();
        Path semanticRoot = root.resolve("src/main/java/semantic");
        Path directRoot = root.resolve("src/main/java/generate/java");
        Path stRoot = directRoot.resolve("st");
        Path recursiveJavaRoot = root.resolve(
            "src/main/java/generate/templates/recursive/java");
        String javaTemplates = read(root.resolve("src/main/resources/templates/java/java.stg"));

        assertAtMost(
            countJavaFilesExcluding(directRoot, stRoot),
            DIRECT_GENERATOR_FILE_BASELINE,
            "direct generator Java files");
        assertAtMost(
            countJavaFilesContaining(semanticRoot, "import generate.CBaseLanguageExporter;"),
            SEMANTIC_EXPORTER_IMPORT_BASELINE,
            "semantic classes importing CBaseLanguageExporter");
        assertAtMost(
            countJavaFilesContaining(stRoot, ".render("),
            ST_CONTROLLER_RENDER_FILE_BASELINE,
            "ST controller files calling render()");
        assertAtMost(
            occurrences(javaTemplates, "codeString"),
            TEMPLATE_CODE_STRING_BASELINE,
            "template codeString references");
        assertAtMost(
            occurrences(javaTemplates, "childrenCode"),
            TEMPLATE_CHILDREN_CODE_BASELINE,
            "template childrenCode references");
        assertAtMost(
            occurrences(javaTemplates, "referenceString"),
            TEMPLATE_REFERENCE_STRING_BASELINE,
            "template referenceString references");
        assertAtMost(
            countJavaFilesMatching(semanticRoot, SEMANTIC_FINAL_CONTRACT_DEBT),
            SEMANTIC_FINAL_CONTRACT_DEBT_BASELINE,
            "semantic classes failing the final contract");
        assertAtMost(
            countJavaFilesContaining(recursiveJavaRoot, "implements JavaTemplateRenderer"),
            TYPED_JAVA_RENDERER_BASELINE,
            "per-semantic-type Java renderers");
        assertAtMost(
            countJavaFilesMatching(directRoot, DIRECT_SEMANTIC_SUBCLASS),
            DIRECT_SEMANTIC_SUBCLASS_BASELINE,
            "direct backend classes subclassing semantic entities");
    }

    @Test
    void recursiveAssemblerHasExactlyOneTextFlatteningPoint() throws IOException
    {
        Path recursiveRoot = moduleRoot().resolve(
            "src/main/java/generate/templates/recursive");

        assertEquals(1, countOccurrencesInJavaFiles(recursiveRoot, ".render("),
            "Only JavaTemplateAssembler.renderRoot may flatten an ST tree");
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

    private static Set<String> matches(Path file, Pattern pattern) throws IOException
    {
        Set<String> values = new HashSet<>();
        Matcher matcher = pattern.matcher(read(file));
        while (matcher.find())
        {
            values.add(matcher.group());
        }
        return values;
    }

    private static long countJavaFilesExcluding(Path root, Path excludedRoot) throws IOException
    {
        if (!Files.exists(root))
        {
            return 0;
        }
        try (Stream<Path> files = Files.walk(root))
        {
            return files.filter(path -> path.toString().endsWith(".java"))
                .filter(path -> !path.startsWith(excludedRoot))
                .count();
        }
    }

    private static long countJavaFilesContaining(Path root, String needle) throws IOException
    {
        if (!Files.exists(root))
        {
            return 0;
        }
        try (Stream<Path> files = Files.walk(root))
        {
            return files.filter(path -> path.toString().endsWith(".java"))
                .filter(path -> contains(path, needle))
                .count();
        }
    }

    private static long countJavaFilesMatching(Path root, Pattern pattern) throws IOException
    {
        if (!Files.exists(root))
        {
            return 0;
        }
        try (Stream<Path> files = Files.walk(root))
        {
            return files.filter(path -> path.toString().endsWith(".java"))
                .filter(path -> pattern.matcher(readUnchecked(path)).find())
                .count();
        }
    }

    private static long countOccurrencesInJavaFiles(Path root, String needle) throws IOException
    {
        if (!Files.exists(root))
        {
            return 0;
        }
        try (Stream<Path> files = Files.walk(root))
        {
            return files.filter(path -> path.toString().endsWith(".java"))
                .mapToLong(path -> occurrences(readUnchecked(path), needle))
                .sum();
        }
    }

    private static boolean contains(Path path, String needle)
    {
        return readUnchecked(path).contains(needle);
    }

    private static String readUnchecked(Path path)
    {
        try
        {
            return read(path);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Cannot read " + path, e);
        }
    }

    private static String read(Path path) throws IOException
    {
        return new String(Files.readAllBytes(path), StandardCharsets.ISO_8859_1);
    }

    private static int occurrences(String value, String needle)
    {
        int count = 0;
        int offset = 0;
        while ((offset = value.indexOf(needle, offset)) >= 0)
        {
            count++;
            offset += needle.length();
        }
        return count;
    }

    private static void assertAtMost(long actual, long baseline, String description)
    {
        assertTrue(actual <= baseline,
            description + " grew from baseline " + baseline + " to " + actual);
    }
}
