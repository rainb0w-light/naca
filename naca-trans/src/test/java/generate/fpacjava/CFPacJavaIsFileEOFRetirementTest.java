package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityFileDescriptor;
import semantic.expression.CEntityCondNot;
import semantic.expression.CEntityIsFileEOF;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the FPac EOF condition backend. */
class CFPacJavaIsFileEOFRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static CEntityFileDescriptor descriptor()
    {
        return new CEntityFileDescriptor(1, "CUSTOMER-FILE", null)
        {
            @Override protected void RegisterMySelfToCatalog() { }
        };
    }

    @Test
    void factoryReturnsPureSemanticConditionAndRendersEof()
    {
        CEntityIsFileEOF eof = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityIsFileEOF(descriptor());

        assertEquals(CEntityIsFileEOF.class, eof.getClass());
        assertEquals("isEof(CUSTOMER_FILE)", render(eof));
        assertEquals(7, eof.GetPriorityLevel());
    }

    @Test
    void semanticOppositeRendersThroughTheSameRecursiveTree()
    {
        CEntityIsFileEOF eof = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityIsFileEOF(descriptor());

        assertEquals(CEntityCondNot.class, eof.GetOppositeCondition().getClass());
        assertEquals("!(isEof(CUSTOMER_FILE))", render(eof.GetOppositeCondition()));
    }

    private static String render(Object value)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(value, JavaTemplateRole.FPAC_REFERENCE).trim();
    }
}
