package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityReadFile;

/**
 * READ rendering through the recursive assembler (the production path). READ is
 * rendered as a child of a procedure via the {@code recursiveReadFileEntity}
 * binding; this test drives it directly with lightweight mocks. Replaces the old
 * {@code CJavaReadFileSTTest}, which drove the now-deleted legacy
 * {@code CJavaReadFileST.DoExport} controller.
 */
class ReadFileRenderTest
{
    private static CEntityFileDescriptor fileDescriptor(String name)
    {
        return new CEntityFileDescriptor(1, name, null)
        {
            @Override
            protected void RegisterMySelfToCatalog()
            {
                // No-op for mock.
            }
        };
    }

    private static String render(CEntityFileDescriptor fileDesc, CDataEntity dataInto,
        CBaseLanguageEntity atEndBloc, CBaseLanguageEntity notAtEndBloc)
    {
        CEntityReadFile readFile = new CEntityReadFile(1, null)
        {
            @Override
            public CEntityFileDescriptor getFileDescriptor() { return fileDesc; }
            @Override
            public CDataEntity getDataInto() { return dataInto; }
            @Override
            public CBaseLanguageEntity getAtEndBloc() { return atEndBloc; }
            @Override
            public CBaseLanguageEntity getNotAtEndBloc() { return notAtEndBloc; }
        };
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(readFile, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("Simple READ INTO renders readInto(file, dest)")
    void simpleRead()
    {
        String output = render(fileDescriptor("customerfile"),
            new MockDataEntity(2, "customerRecord"), null, null);
        assertTrue(output.contains("readInto(customerfile, customerRecord) ;"), output);
    }

    @Test
    @DisplayName("READ with AT END renders if (readInto(...).atEnd()) { ... }")
    void readAtEnd()
    {
        MockBloc atEndBloc = new MockBloc(3);
        atEndBloc.addChild(new CEntityBreak(0, null));

        String output = render(fileDescriptor("inputfile"),
            new MockDataEntity(2, "record"), atEndBloc, null);
        assertTrue(output.contains("if (readInto(inputfile, record).atEnd())"), output);
        assertTrue(output.contains("break;"), output);
    }
}
