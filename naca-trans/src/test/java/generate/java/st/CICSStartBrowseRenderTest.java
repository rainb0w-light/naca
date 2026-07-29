package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSStartBrowse;

class CICSStartBrowseRenderTest
{
    @Test
    void rendersEveryLoweredOperand()
    {
        CEntityCICSStartBrowse browse = new CEntityCICSStartBrowse(1, null);
        browse.BrowseDataSet(new MockDataEntity(2, "\"FILE-A\""));
        browse.SetRecIDField(new MockDataEntity(3, "RECORD-ID"));
        browse.SetKeyLength(new MockDataEntity(4, "KEY-LENGTH"));
        browse.SetGTEQ();

        assertEquals("CESM.startBrowseDataSet(\"FILE-A\").recIDField(RECORD-ID)"
            + ".keyLength(KEY-LENGTH).gTEQ() ;",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(browse, JavaTemplateRole.REFERENCE).trim());
    }

    @Test
    void factoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityCICSStartBrowse.class,
            new CJavaEntityFactoryST(null, null).NewEntityCICSStartBrowse(1));
    }
}
