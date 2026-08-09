package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSWrite;

class CICSWriteRenderTest
{
    private static String render(CEntityCICSWrite write)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(write, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void dataSetRetainsEveryLoweredOperand()
    {
        CEntityCICSWrite write = new CEntityCICSWrite(1, null);
        MockDataEntity name = new MockDataEntity(2, "DATASET-NAME");
        MockDataEntity source = new MockDataEntity(3, "RECORD-DATA");
        MockDataEntity length = new MockDataEntity(4, "RECORD-LENGTH");
        write.WriteDataSet(name);
        write.SetDataFrom(source, length);
        write.SetRecIDField(new MockDataEntity(5, "RECORD-ID"));
        write.SetKeyLength(new MockDataEntity(6, "KEY-LENGTH"));

        assertSame(name, write.getName());
        assertEquals("CESM.writeDataSet(DATASET-NAME)"
            + ".from(RECORD-DATA, RECORD-LENGTH).recIDField(RECORD-ID)"
            + ".keyLength(KEY-LENGTH) ;", render(write));
    }

    @Test
    void fileLiteralUsesFileRuntimeFamily()
    {
        CEntityCICSWrite write = new CEntityCICSWrite(1, null);
        write.WriteFile(new MockDataEntity(2, "\"FILE-A\""));
        write.SetDataFrom(new MockDataEntity(3, "RECORD-DATA"), null);

        assertEquals("CESM.writeFile(\"FILE-A\").from(RECORD-DATA) ;",
            render(write));
    }

    @Test
    void factoryReturnsPureSemanticEntity()
    {
        assertInstanceOf(CEntityCICSWrite.class,
            new CJavaEntityFactory(null, null).NewEntityCICSWrite(1));
    }
}
