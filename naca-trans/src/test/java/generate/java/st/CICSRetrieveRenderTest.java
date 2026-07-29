package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSRetrieve;

class CICSRetrieveRenderTest
{
    private static String render(CEntityCICSRetrieve retrieve)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(retrieve, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    @DisplayName("RETRIEVE INTO renders the data-copy call")
    void retrieveInto()
    {
        CEntityCICSRetrieve retrieve = new CEntityCICSRetrieve(1, null, false);
        retrieve.SetRetrieve(new MockDataEntity(2, "TARGET"), null);
        assertEquals("CESM.retrieveInto(TARGET) ;", render(retrieve));
    }

    @Test
    @DisplayName("RETRIEVE SET LENGTH retains pointer mode and length")
    void retrieveSetWithLength()
    {
        CEntityCICSRetrieve retrieve = new CEntityCICSRetrieve(1, null, true);
        retrieve.SetRetrieve(new MockDataEntity(2, "POINTER"),
            new MockDataEntity(3, "DATA-LENGTH"));
        assertTrue(retrieve.isPointer());
        assertEquals("CESM.retrieveSet(POINTER, DATA-LENGTH) ;", render(retrieve));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure RETRIEVE semantic entity")
    void factoryReturnsSemanticEntity()
    {
        assertInstanceOf(CEntityCICSRetrieve.class,
            new CJavaEntityFactoryST(null, null).NewEntityCICSRetreive(1, false));
    }
}
