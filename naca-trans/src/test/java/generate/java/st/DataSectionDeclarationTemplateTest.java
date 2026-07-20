package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.CJavaAttribute;
import generate.java.CJavaDataSection;
import generate.java.CJavaStructure;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

class DataSectionDeclarationTemplateTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String normalize(String source)
    {
        return source.replaceAll("\\s+", " ").strip();
    }

    @Test
    void rendersAGroupDeclarationLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaStructure structure =
            new CJavaStructure(1, "WS-GROUP", catalog(), exporter, "01");

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("declare.level(1).var()"), rendered);
    }

    @Test
    void propagatesDeclarationRoleThroughAFileSectionTree()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaDataSection section =
            new CJavaDataSection(1, "FileSection", catalog, exporter);
        CJavaStructure structure =
            new CJavaStructure(2, "FILE-RECORD", catalog, exporter, "01");
        CJavaAttribute attribute =
            new CJavaAttribute(3, "FILE-CHAR", catalog, exporter);
        attribute.SetLevel("05");
        attribute.SetTypeString(8);
        section.AddChild(structure);
        structure.AddChild(attribute);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(section, JavaTemplateRole.DECLARATION);
        section.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("DataSection FileSection = declare.fileSection()"), rendered);
        assertTrue(rendered.contains("Var FILE_RECORD = declare.level(1)"), rendered);
        assertTrue(rendered.contains("Var FILE_CHAR = declare.level(05).picX(8)"), rendered);
    }
}
