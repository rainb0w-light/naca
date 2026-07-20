package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.CJavaAttribute;
import generate.java.CJavaDataSection;
import generate.java.CJavaNamedCondition;
import generate.java.CJavaStructure;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import parser.Cobol.elements.CWorkingEntry.CWorkingSignType;
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

    @Test
    void rendersALevel88ConditionLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaNamedCondition condition =
            new CJavaNamedCondition(4, "WS-COND", catalog, exporter);
        condition.AddValue(new MockDataEntity(4, catalog, exporter, "1"));
        condition.AddValue(new MockDataEntity(4, catalog, exporter, "2"));
        condition.AddInterval(
            new MockDataEntity(4, catalog, exporter, "3"),
            new MockDataEntity(4, catalog, exporter, "9"));

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.DECLARATION);
        condition.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Cond WS_COND = declare.condition()"), rendered);
        assertTrue(rendered.contains(".value(1)"), rendered);
        assertTrue(rendered.contains(".value(2)"), rendered);
        assertTrue(rendered.contains(".value(3, 9)"), rendered);
        assertTrue(rendered.contains(".var() ;"), rendered);
    }

    @Test
    void rendersAVariableLengthTableLikeTheDirectGeneratorWithoutMutatingLength()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaStructure structure =
            new CJavaStructure(2, "WS-TBL", catalog, exporter, "05");
        structure.SetTypeString(3);
        structure.SetTableSizeDepending(
            new MockDataEntity(2, catalog, exporter, "5"), null);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Var WS_TBL = declare.level(5).variableLength().picX(15)"), rendered);
        // Effective length (3 * 5) is a derived value; the semantic length is unchanged.
        assertEquals(3, structure.getLength());
    }

    @Test
    void rendersARedefinesStructureLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaStructure structure =
            new CJavaStructure(2, "WS-REDEF", catalog, exporter, "05");
        structure.SetTypeString(4);
        structure.SetRedefine(new MockDataEntity(2, catalog, exporter, "WS_ORIG"));

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Var WS_REDEF = declare.level(5).redefines(WS_ORIG).picX(4)"), rendered);
    }

    @Test
    void rendersAnOccursStructureLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaStructure structure =
            new CJavaStructure(2, "WS-TAB", catalog, exporter, "05");
        structure.SetTypeString(3);
        structure.SetTableSize(new MockDataEntity(2, catalog, exporter, "10"));

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Var WS_TAB = declare.level(5).occurs(10).picX(3)"), rendered);
    }

    @Test
    void rendersAnOccursDependingStructureLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaStructure structure =
            new CJavaStructure(2, "WS-DEP", catalog, exporter, "05");
        structure.SetTypeString(3);
        structure.SetTableSizeDepending(
            new MockDataEntity(2, catalog, exporter, "20"),
            new MockDataEntity(2, catalog, exporter, "WS_COUNT"));

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Var WS_DEP = declare.level(5).occursDepending(20, WS_COUNT).picX(3)"), rendered);
    }

    @Test
    void rendersAComp3StructureLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaStructure structure =
            new CJavaStructure(2, "WS-PACKED", catalog, exporter, "05");
        structure.SetTypeNum(5, 0);
        structure.SetComp("Comp3");

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Var WS_PACKED = declare.level(5).pic9(5).comp3()"), rendered);
    }

    @Test
    void rendersASignLeadingSeparatedStructureLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaStructure structure =
            new CJavaStructure(2, "WS-SGNL", catalog, exporter, "05");
        structure.SetTypeNum(3, 0);
        structure.SetSignSeparateType(CWorkingSignType.LEADING);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Var WS_SGNL = declare.level(5).pic9(3).signLeadingSeparated()"), rendered);
    }

    @Test
    void rendersASignTrailingSeparatedStructureLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaStructure structure =
            new CJavaStructure(2, "WS-SGNT", catalog, exporter, "05");
        structure.SetTypeNum(3, 0);
        structure.SetSignSeparateType(CWorkingSignType.TRAILING);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Var WS_SGNT = declare.level(5).pic9(3).signTrailingSeparated()"), rendered);
    }
}
