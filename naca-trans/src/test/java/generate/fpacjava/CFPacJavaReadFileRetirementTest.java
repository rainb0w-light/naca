package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockBloc;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityGoto;
import semantic.Verbs.CEntityReadFile;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the FPac GET/read backend. */
class CFPacJavaReadFileRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static CEntityFileDescriptor descriptor()
    {
        return new CEntityFileDescriptor(1, "INPUT-FILE", null)
        {
            @Override protected void RegisterMySelfToCatalog() { }
        };
    }

    @Test
    void factoryReturnsPureSemanticReadAndRendersDescriptorCall()
    {
        CEntityReadFile read = new CJavaFPacEntityFactory(catalog, null).NewEntityReadFile(1);
        read.setFileDescriptor(descriptor(), null);

        assertEquals(CEntityReadFile.class, read.getClass());
        assertEquals("INPUT_FILE.read() ;", render(read));
    }

    @Test
    void atEndBlockKeepsFpacControlRole()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityReadFile read = factory.NewEntityReadFile(1);
        read.setFileDescriptor(descriptor(), null);
        MockBloc atEnd = new MockBloc(2);
        CEntityGoto goEnd = factory.NewEntityGoto(3, "END", null);
        atEnd.addChild(goEnd);
        read.SetAtEndBloc(atEnd);

        assertEquals("return END ;", TemplateLoader.getRecursiveAssembler()
            .renderRoot(atEnd, JavaTemplateRole.FPAC_REFERENCE).trim());
        assertEquals("if (INPUT_FILE.read().atEnd()) {\nreturn END ;\n}", render(read));
    }

    private static String render(CEntityReadFile read)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(read, JavaTemplateRole.FPAC_REFERENCE).trim();
    }
}
