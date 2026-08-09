package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityWriteFile;
import utils.CObjectCatalog;

/** Retirement proof for the FPac PUT/write backend. */
class CFPacJavaWriteFileRetirementTest
{
    private static CEntityFileDescriptor descriptor(String name)
    {
        return new CEntityFileDescriptor(1, name, null)
        {
            @Override protected void RegisterMySelfToCatalog() { }
        };
    }

    @Test
    void factoryReturnsPureSemanticVerbAndFpacTemplateCallsDescriptorWrite()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityWriteFile write = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityWriteFile(1);
        write.setFileDescriptor(descriptor("OUTPUT-FILE"), null);

        assertEquals(CEntityWriteFile.class, write.getClass());
        assertEquals("OUTPUT_FILE.write() ;", TemplateLoader.getRecursiveAssembler()
            .renderRoot(write, JavaTemplateRole.FPAC_REFERENCE).trim());
    }

    @Test
    void productionProcedureBodyReachesRecursiveFpacBinding()
    {
        MockJavaExporter output = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, output);
        CEntityProcedure procedure = factory.NewEntityProcedure(1, "MAIN", null);
        CEntityWriteFile write = factory.NewEntityWriteFile(2);
        write.setFileDescriptor(descriptor("OUTPUT-FILE"), null);
        procedure.AddChild(write);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(procedure, JavaTemplateRole.FPAC_REFERENCE);
        assertTrue(rendered.contains("OUTPUT_FILE.write() ;"));
    }
}
