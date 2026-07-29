package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.fixtures.LegacyFileDescriptorLengthDependencyFixture;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.CEntityFileDescriptorLengthDependency;
import utils.CObjectCatalog;

class CEntityFileDescriptorLengthDependencyRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityFileDescriptorLengthDependency.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityFileDescriptorLengthDependency("FILE-IN-dependency").getClass());
        assertEquals(CEntityFileDescriptorLengthDependency.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityFileDescriptorLengthDependency("FILE-IN-dependency").getClass());
    }

    @Test
    void declarationMatchesTheRetiredBackend()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityFileDescriptor file =
            new CEntityFileDescriptor(1, "FILE-IN", catalog);
        MockDataEntity length = new MockDataEntity(1, catalog, exporter, "RECORD_LENGTH");

        CEntityFileDescriptorLengthDependency semantic =
            new CEntityFileDescriptorLengthDependency("FILE-IN-dependency", catalog);
        semantic.setDependency(file, length);

        LegacyFileDescriptorLengthDependencyFixture legacy =
            new LegacyFileDescriptorLengthDependencyFixture(
                "FILE-IN-dependency", catalog, exporter);
        legacy.setDependency(file, length);
        legacy.StartExport();

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(semantic, JavaTemplateRole.DECLARATION);
        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertEquals(
            "FileDescriptorDepending FILE_IN_dependency = "
                + "declare.fileDescriptorDepending( FILE_IN, RECORD_LENGTH) ;",
            normalize(rendered));
    }

    private static String normalize(String source)
    {
        return source.replaceAll("\\s+", " ").strip();
    }
}
