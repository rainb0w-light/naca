package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityOpenFile;
import semantic.Verbs.CEntityOpenFile.OpenMode;
import utils.CObjectCatalog;

/** Retirement proof for the final FPac direct backend: OPEN. */
class CFpacJavaOpenFileRetirementTest
{
    private static CEntityFileDescriptor descriptor(OpenMode mode, boolean variable)
    {
        CEntityFileDescriptor descriptor = new CEntityFileDescriptor(1, "DATA-FILE", null)
        {
            @Override protected void RegisterMySelfToCatalog() { }
        };
        descriptor.setFileAccessType(mode);
        descriptor.setRecordSizeVariable(variable);
        return descriptor;
    }

    private static String render(OpenMode mode, boolean variable, boolean explicitMode)
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityOpenFile open = new CJavaFPacEntityFactory(catalog, null).NewEntityOpenFile(1);
        CEntityFileDescriptor descriptor = descriptor(mode, variable);
        open.setFileDescriptor(descriptor, explicitMode ? mode : null);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(open, JavaTemplateRole.FPAC_REFERENCE).trim();
    }

    @Test
    void factoryReturnsPureSemanticEntityAndPreservesEveryMode()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityOpenFile.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityOpenFile(1).getClass());
        assertEquals("DATA_FILE.openInput() ;", render(OpenMode.INPUT, false, true));
        assertEquals("DATA_FILE.openOutput() ;", render(OpenMode.OUTPUT, false, true));
        assertEquals("DATA_FILE.openInputOutput() ;",
            render(OpenMode.INPUT_OUTPUT, false, true));
        assertEquals("DATA_FILE.openExtend() ;", render(OpenMode.APPEND, false, true));
    }

    @Test
    void variableLengthChainAndDescriptorModeFallbackRemainReachable()
    {
        assertEquals("DATA_FILE.openInput().variableLength() ;",
            render(OpenMode.INPUT, true, true));
        assertEquals("DATA_FILE.openExtend().variableLength() ;",
            render(OpenMode.APPEND, true, false));
    }
}
