package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityClass;
import semantic.CEntityDataSection;
import semantic.CEntityFileDescriptor;
import semantic.CEntityProcedure;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaDataSection} direct backend.
 *
 * <p>The FPac declaration zone ({@code parser/FPac/elements/CFPacDeclarationZone}) lowers
 * IPF/OPF/UPF file declarations into a {@code "DeclarationSection"} {@link CEntityDataSection}
 * whose children are target-neutral {@code CEntityFileDescriptor} nodes.
 * The retired backend carried no code of its own: its {@code DoExport} was exactly
 * {@code exportChildren(this, false)} — a transparent container that renders its children in
 * place at the class-body block level. {@link CJavaFPacEntityFactory#NewEntityDataSection} now
 * hands back the pure, target-neutral {@code CEntityDataSection} (no {@code generate.fpacjava}
 * subclass), shared with the COBOL pipeline.
 *
 * <p>The FPac override manifest maps the data section to a transparent template and keeps its
 * children in {@code FPAC_REFERENCE}. That prevents the shared COBOL declaration role from
 * producing {@code declare.file(...)} while letting the complete declaration subtree fail closed.
 */
class CFPacJavaDataSectionRetirementTest
{
    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityDataSection.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityDataSection(1, "DeclarationSection").getClass());
    }

    /**
     * End-to-end production lowering: {@code FPacTranscoderEngine.exportFpacProgramRoot} is the
     * exact driver the FPac transcoder runs on the pure {@code CEntityClass} the factory returns.
     * With a real pure {@code CEntityDataSection} holding a pure file descriptor,
     * the bridge renders the transparent container and descriptor through the assembler —
     * emitting the FPac {@code FPacFileDescriptor ... declare.fpacFile(...).file() ;} declaration,
     * NOT dropping it and NOT lowering it to the frozen COBOL {@code FileDescriptor ... declare.file}
     * form the recursive assembler's {@code dataSectionDeclaration} binding would produce.
     */
    @Test
    void productionRootExportFlattensDataSectionIntoFpacFileDescriptors()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CEntityClass programClass = factory.NewEntityClass(1, "PROG");
        CEntityDataSection data = factory.NewEntityDataSection(2, "DeclarationSection");
        CEntityFileDescriptor descriptor = factory.NewEntityFileDescriptor(3, "CUSTFILE");
        data.AddChild(descriptor);
        programClass.AddChild(data);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(programClass, JavaTemplateRole.FPAC_ROOT);
        assertTrue(rendered.contains("public class PROG extends FPacProgram"),
            "production FPac root must declare the UPPERCASE FPacProgram class; got:\n" + rendered);
        assertTrue(rendered.contains(
                "FPacFileDescriptor custfile = declare.fpacFile(\"CUSTFILE\").file() ;"),
            "production FPac root must render the declaration subtree in FPac form; got:\n"
                + rendered);
        assertFalse(rendered.contains("FileDescriptor CUSTFILE = declare.file("),
            "the retired data section must NOT be routed through the assembler's frozen COBOL "
                + "dataSectionDeclaration binding; got:\n" + rendered);
        assertFalse(rendered.contains("declare.workingStorageSection")
                || rendered.contains("declare.linkageSection")
                || rendered.contains("DataSection "),
            "the transparent FPac data section carries no header of its own; got:\n" + rendered);
    }

    /**
     * A still-legacy procedure sibling of the retired data section is unaffected: the bridge
     * drives the procedure by reflection while flattening the data section, so a program holding
     * both emits the FPac file descriptor AND the procedure wrapper in the same class body.
     */
    @Test
    void productionRootExportKeepsProcedureSiblingIntact()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CEntityClass programClass = factory.NewEntityClass(1, "PROG");
        CEntityDataSection data = factory.NewEntityDataSection(2, "DeclarationSection");
        data.AddChild(factory.NewEntityFileDescriptor(3, "CUSTFILE"));
        programClass.AddChild(data);
        CEntityProcedure main = factory.NewEntityProcedure(4, "MAIN", null);
        programClass.AddChild(main);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(programClass, JavaTemplateRole.FPAC_ROOT);
        assertTrue(rendered.contains(
                "FPacFileDescriptor custfile = declare.fpacFile(\"CUSTFILE\").file() ;"),
            "production FPac root must still flatten the data section; got:\n" + rendered);
        assertTrue(rendered.contains("protected int main() {"),
            "production FPac root must still drive the still-legacy procedure by reflection; got:\n"
                + rendered);
    }
}
