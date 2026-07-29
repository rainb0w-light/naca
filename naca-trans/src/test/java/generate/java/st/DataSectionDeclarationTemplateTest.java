package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.fixtures.LegacyAttributeFixture;
import generate.fixtures.LegacyDataSectionFixture;
import generate.fixtures.LegacyExternalDataStructureFixture;
import generate.fixtures.LegacyFileDescriptorFixture;
import generate.fixtures.LegacyInlineFixture;
import generate.fixtures.LegacyNamedConditionFixture;
import generate.fixtures.LegacyStructureFixture;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import parser.Cobol.elements.CWorkingEntry.CWorkingSignType;
import semantic.CEntityFileSelect;
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
        LegacyStructureFixture structure =
            new LegacyStructureFixture(1, "WS-GROUP", catalog(), exporter, "01");

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
        LegacyDataSectionFixture section =
            new LegacyDataSectionFixture(1, "FileSection", catalog, exporter);
        LegacyStructureFixture structure =
            new LegacyStructureFixture(2, "FILE-RECORD", catalog, exporter, "01");
        LegacyAttributeFixture attribute =
            new LegacyAttributeFixture(3, "FILE-CHAR", catalog, exporter);
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
        LegacyNamedConditionFixture condition =
            new LegacyNamedConditionFixture(4, "WS-COND", catalog, exporter);
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
        LegacyStructureFixture structure =
            new LegacyStructureFixture(2, "WS-TBL", catalog, exporter, "05");
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
        LegacyStructureFixture structure =
            new LegacyStructureFixture(2, "WS-REDEF", catalog, exporter, "05");
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
        LegacyStructureFixture structure =
            new LegacyStructureFixture(2, "WS-TAB", catalog, exporter, "05");
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
        LegacyStructureFixture structure =
            new LegacyStructureFixture(2, "WS-DEP", catalog, exporter, "05");
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
        LegacyStructureFixture structure =
            new LegacyStructureFixture(2, "WS-PACKED", catalog, exporter, "05");
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
        LegacyStructureFixture structure =
            new LegacyStructureFixture(2, "WS-SGNL", catalog, exporter, "05");
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
        LegacyStructureFixture structure =
            new LegacyStructureFixture(2, "WS-SGNT", catalog, exporter, "05");
        structure.SetTypeNum(3, 0);
        structure.SetSignSeparateType(CWorkingSignType.TRAILING);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.DECLARATION);
        structure.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("Var WS_SGNT = declare.level(5).pic9(3).signTrailingSeparated()"), rendered);
    }

    @Test
    void rendersAGroupFillerLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        // Empty name => group FILLER; the default name is assigned at construction
        // (semantic phase), so neither the direct generator nor the template
        // mutates the tree during export.
        LegacyStructureFixture filler =
            new LegacyStructureFixture(1, "", catalog, exporter, "01");

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(filler, JavaTemplateRole.DECLARATION);
        filler.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("declare.level(1).filler() ;"), rendered);
        assertTrue(filler.isFiller(), "empty-name structure must be a filler");
        assertFalse(filler.GetName().isEmpty(), "filler name assigned at construction");
    }

    @Test
    void rendersAFileDescriptorLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        // SELECT ... ASSIGN TO with no FILE STATUS: the file select carries no
        // explicit name/status entity, so the declaration falls back to the
        // quoted display name (exactly like BATCH1's FILEIN/FILEOUT).
        CEntityFileSelect select = new CEntityFileSelect("FILEIN", catalog);
        catalog.RegisterFileSelect(select);
        // File section -> FD -> record group -> field. The FD is parented under
        // the file section so the record group is "inside a file section" and
        // exports all its children, matching the direct generator.
        LegacyDataSectionFixture section =
            new LegacyDataSectionFixture(1, "FileSection", catalog, exporter);
        LegacyFileDescriptorFixture fd =
            new LegacyFileDescriptorFixture(2, "FILEIN", catalog, exporter);
        LegacyStructureFixture record =
            new LegacyStructureFixture(3, "FILEIN-Z", catalog, exporter, "01");
        LegacyAttributeFixture field = new LegacyAttributeFixture(4, "FILEIN-CODE", catalog, exporter);
        field.SetLevel("05");
        field.SetTypeString(1);
        section.AddChild(fd);
        fd.AddChild(record);
        record.AddChild(field);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(fd, JavaTemplateRole.DECLARATION);
        fd.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("FileDescriptor FILEIN = declare.file(\"FILEIN\") ;"), rendered);
        assertTrue(rendered.contains("Var FILEIN_Z = declare.level(1)"), rendered);
        assertTrue(rendered.contains("Var FILEIN_CODE = declare.level(05).picX(1)"), rendered);
    }

    @Test
    void rendersAFileDescriptorWithFileStatusLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CEntityFileSelect select = new CEntityFileSelect("FILEOUT", catalog);
        select.setFileStatus(new MockDataEntity(1, catalog, exporter, "WS_STATUS"));
        catalog.RegisterFileSelect(select);
        LegacyFileDescriptorFixture fd =
            new LegacyFileDescriptorFixture(2, "FILEOUT", catalog, exporter);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(fd, JavaTemplateRole.DECLARATION);
        fd.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("FileDescriptor FILEOUT = declare.file(\"FILEOUT\").status(WS_STATUS) ;"), rendered);
    }

    @Test
    void rendersACopybookInstanceDeclarationLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        // COPY MSGZONE: a non-inline copybook declared as an instance in the
        // program class (Type ref = Type.Copy(this)), as in BATCH1.
        LegacyExternalDataStructureFixture copybook =
            new LegacyExternalDataStructureFixture(1, "Msgzone", catalog, exporter);
        LegacyInlineFixture inline =
            new LegacyInlineFixture(2, catalog, exporter, copybook);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(inline, JavaTemplateRole.DECLARATION);
        inline.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("= Msgzone.Copy(this) ;"), rendered);
    }

    @Test
    void setInlineActuallyStoresTheFlag()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        LegacyExternalDataStructureFixture copybook =
            new LegacyExternalDataStructureFixture(1, "Msgzone", catalog, exporter);
        assertFalse(copybook.isInlined());
        assertTrue(copybook.IsNeedDeclarationInClass());
        copybook.SetInline(true);
        assertTrue(copybook.isInlined(), "SetInline must store the flag (was self-assignment)");
        assertFalse(copybook.IsNeedDeclarationInClass());
    }

    @Test
    void rendersACopybookClassArtifactLikeTheDirectGenerator()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        // ROOT role: the copybook as its own Java class extending Copy.
        LegacyExternalDataStructureFixture copybook =
            new LegacyExternalDataStructureFixture(1, "Msgzone", catalog, exporter);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(copybook, JavaTemplateRole.ROOT);
        copybook.StartExport();

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("public class Msgzone extends Copy {"), rendered);
        assertTrue(rendered.contains("return new Msgzone(program, null);"), rendered);
        assertTrue(rendered.contains("import nacaLib.basePrgEnv.* ;"), rendered);
    }
}
