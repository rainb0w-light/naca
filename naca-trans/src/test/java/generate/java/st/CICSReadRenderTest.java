package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSRead;
import semantic.CICS.CEntityCICSRead.CEntityCICSReadMode;

class CICSReadRenderTest
{
    private static String render(CEntityCICSRead read)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(read, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    @DisplayName("normal READ DATASET renders every lowered fluent option")
    void normalReadDataSet()
    {
        CEntityCICSRead read = new CEntityCICSRead(1, null, CEntityCICSReadMode.NORMAL);
        read.ReadDataSet(new MockDataEntity(2, "\"CUSTOMER\""));
        read.SetDataInto(new MockDataEntity(3, "TARGET"), new MockDataEntity(4, "LENGTH-VAR"));
        read.SetRecIDField(new MockDataEntity(5, "RECORD-ID"));
        read.SetKeyLength(new MockDataEntity(6, "KEY-LENGTH"));
        read.SetEqual();
        read.SetUpdate();
        read.SetResponses(new MockDataEntity(7, "RESP-CODE"),
            new MockDataEntity(8, "RESP2-CODE"));

        assertEquals(
            "CESM.readDataSet(\"CUSTOMER\").into(TARGET).length(LENGTH-VAR)"
                + ".recIDField(RECORD-ID).keyLength(KEY-LENGTH).equal().update()"
                + ".resp(RESP-CODE).resp2(RESP2-CODE).execute() ;",
            render(read));
    }

    @Test
    @DisplayName("READPREV FILE retains both mode and file name")
    void previousReadFile()
    {
        CEntityCICSRead read = new CEntityCICSRead(1, null, CEntityCICSReadMode.PREVIOUS);
        MockDataEntity name = new MockDataEntity(2, "FILE-NAME");
        read.ReadFile(name);
        read.SetDataInto(new MockDataEntity(3, "TARGET"), null);

        assertTrue(read.isPrevious());
        assertTrue(read.isReadToFile());
        assertFalse(read.isReadToDataSet());
        assertEquals(name, read.getName());
        assertEquals("CESM.readPreviousFile(FILE-NAME).into(TARGET).execute() ;", render(read));
    }

    @Test
    @DisplayName("READNEXT DATASET renders its distinct runtime entry point")
    void nextReadDataSet()
    {
        CEntityCICSRead read = new CEntityCICSRead(1, null, CEntityCICSReadMode.NEXT);
        read.ReadDataSet(new MockDataEntity(2, "DATASET-NAME"));
        read.SetDataInto(new MockDataEntity(3, "TARGET"), null);
        assertEquals("CESM.readNextDataSet(DATASET-NAME).into(TARGET).execute() ;", render(read));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure READ semantic entity")
    void factoryReturnsSemanticEntity()
    {
        assertInstanceOf(CEntityCICSRead.class,
            new CJavaEntityFactory(null, null)
                .NewEntityCICSRead(1, CEntityCICSReadMode.NORMAL));
    }
}
