package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSReWrite;

class CICSReWriteRenderTest
{
    private static String render(CEntityCICSReWrite rewrite)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(rewrite, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    @DisplayName("REWRITE DATASET renders the bare dataset operation")
    void rewriteDataSet()
    {
        CEntityCICSReWrite rewrite = new CEntityCICSReWrite(1, null);
        rewrite.WriteDataSet(new MockDataEntity(2, "\"CUSTOMER\""));
        assertEquals("CESM.reWriteDataSet(\"CUSTOMER\") ;", render(rewrite));
    }

    @Test
    @DisplayName("REWRITE FILE FROM LENGTH preserves the complete fluent shape")
    void rewriteFileFromWithLength()
    {
        CEntityCICSReWrite rewrite = new CEntityCICSReWrite(1, null);
        rewrite.WriteFile(new MockDataEntity(2, "FILE-NAME"));
        rewrite.SetDataFrom(new MockDataEntity(3, "RECORD-BUFFER"),
            new MockDataEntity(4, "RECORD-LENGTH"));
        assertEquals(
            "CESM.reWriteFile(FILE-NAME).from(RECORD-BUFFER, RECORD-LENGTH) ;",
            render(rewrite));
    }

    @Test
    @DisplayName("REWRITE FILE FROM without LENGTH preserves the one-argument form")
    void rewriteFileFrom()
    {
        CEntityCICSReWrite rewrite = new CEntityCICSReWrite(1, null);
        rewrite.WriteFile(new MockDataEntity(2, "FILE-NAME"));
        rewrite.SetDataFrom(new MockDataEntity(3, "RECORD-BUFFER"), null);
        assertEquals("CESM.reWriteFile(FILE-NAME).from(RECORD-BUFFER) ;", render(rewrite));
    }

    @Test
    @DisplayName("the ST4 factory lowers REWRITE to the pure semantic entity")
    void factoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityCICSReWrite.class,
            new CJavaEntityFactoryST(null, null).NewEntityCICSReWrite(1));
    }
}
