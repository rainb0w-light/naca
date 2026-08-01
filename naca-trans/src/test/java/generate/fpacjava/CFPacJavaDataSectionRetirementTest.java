package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockJavaExporter;
import semantic.CEntityClass;
import semantic.CEntityDataSection;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;
import utils.FPacTranscoder.FPacTranscoderEngine;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaDataSection} direct backend.
 *
 * <p>The FPac declaration zone ({@code parser/FPac/elements/CFPacDeclarationZone}) lowers
 * IPF/OPF/UPF file declarations into a {@code "DeclarationSection"} {@link CEntityDataSection}
 * whose only children are still-legacy FPac file descriptors ({@code CFPacJavaFileDescriptor}).
 * The retired backend carried no code of its own: its {@code DoExport} was exactly
 * {@code exportChildren(this, false)} — a transparent container that renders its children in
 * place at the class-body block level. {@link CJavaFPacEntityFactory#NewEntityDataSection} now
 * hands back the pure, target-neutral {@code CEntityDataSection} (no {@code generate.fpacjava}
 * subclass), shared with the COBOL pipeline.
 *
 * <p>The data section is DELIBERATELY NOT routed through the recursive assembler. The shared
 * {@code semantic.CEntityDataSection=dataSectionDeclaration} binding lowers its file-descriptor
 * children under the frozen COBOL declaration binding ({@code FileDescriptor NAME =
 * declare.file(...)}), which does not compile against {@code nacaLib.fpacPrgEnv.FPacProgram}
 * (FPac emits {@code FPacFileDescriptor NAME = declare.fpacFile("NAME").file() ;}). Instead the
 * FPac program-root bridge ({@link FPacTranscoderEngine#exportFpacProgramRoot}) flattens the
 * backend-less container: {@code exportFpacRootChildren} drives the still-legacy file-descriptor
 * children by reflection, byte-for-byte as the deleted backend did. This converges to
 * {@code renderRoot(eSem, FPAC_ROOT)} once the FPac file-descriptor backends retire.
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
     * With a real pure {@code CEntityDataSection} holding a still-legacy {@code CFPacJavaFileDescriptor},
     * the bridge flattens the transparent container and drives the file descriptor by reflection —
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

        FPacTranscoderEngine.exportFpacProgramRoot(programClass);

        String rendered = out.getCapturedOutput();
        assertTrue(rendered.contains("public class PROG extends FPacProgram"),
            "production FPac root must declare the UPPERCASE FPacProgram class; got:\n" + rendered);
        assertTrue(rendered.contains(
                "FPacFileDescriptor CUSTFILE = declare.fpacFile(\"CUSTFILE\").file() ;"),
            "production FPac root must flatten the retired data section and drive its still-legacy "
                + "file descriptor by reflection (FPac form); got:\n" + rendered);
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
        programClass.AddChild(new CFPacJavaProcedure(4, "MAIN", catalog, out, null));

        FPacTranscoderEngine.exportFpacProgramRoot(programClass);

        String rendered = out.getCapturedOutput();
        assertTrue(rendered.contains(
                "FPacFileDescriptor CUSTFILE = declare.fpacFile(\"CUSTFILE\").file() ;"),
            "production FPac root must still flatten the data section; got:\n" + rendered);
        assertTrue(rendered.contains("protected int MAIN() {"),
            "production FPac root must still drive the still-legacy procedure by reflection; got:\n"
                + rendered);
    }
}
