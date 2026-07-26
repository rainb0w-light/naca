package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CEntitySQLCursorSection;

/**
 * Embedded SQL cursor declaration section rendering through the recursive
 * assembler (the production DECLARATION path). The section is rendered via the
 * {@code recursiveSQLCursorSectionEntity} declaration binding; this test drives
 * it directly with lightweight cursor mocks and asserts byte-for-byte parity
 * with the output shapes the retired {@code CJavaSQLCursorSection.DoExport}
 * direct backend produced:
 * <ul>
 *   <li>header -&gt; {@code DataSection SQLCursorSection = declare.cursorSection() ;}</li>
 *   <li>each cursor -&gt; {@code SQLCursor <ref> = declare.cursor() ;}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLCursorSection} direct
 * backend.
 */
class SQLCursorSectionRenderTest
{
    private static CEntitySQLCursorSection sectionWith(String... cursorReferences)
    {
        CEntitySQLCursorSection section = new CEntitySQLCursorSection(null);
        Vector<Object> cursors = new Vector<>();
        int line = 1;
        for (String reference : cursorReferences)
        {
            cursors.add(new MockDataEntity(line++, reference));
        }
        section.SetCursors(cursors);
        return section;
    }

    private static String render(CEntitySQLCursorSection section)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(section, JavaTemplateRole.DECLARATION);
    }

    @Test
    @DisplayName("cursor section header renders declare.cursorSection()")
    void sectionHeader()
    {
        String output = render(sectionWith("CUR1"));
        assertTrue(output.contains("DataSection SQLCursorSection = declare.cursorSection() ;"),
            output);
    }

    @Test
    @DisplayName("a single declared cursor renders SQLCursor <ref> = declare.cursor() ;")
    void singleCursor()
    {
        String output = render(sectionWith("CUR1"));
        assertTrue(output.contains("SQLCursor CUR1 = declare.cursor() ;"), output);
    }

    @Test
    @DisplayName("every declared cursor renders its own declare.cursor() line, in order")
    void multipleCursors()
    {
        String output = render(sectionWith("CUR1", "CUR2", "CUR3"));
        assertTrue(output.contains("SQLCursor CUR1 = declare.cursor() ;"), output);
        assertTrue(output.contains("SQLCursor CUR2 = declare.cursor() ;"), output);
        assertTrue(output.contains("SQLCursor CUR3 = declare.cursor() ;"), output);
        assertTrue(output.indexOf("SQLCursor CUR1") < output.indexOf("SQLCursor CUR2"),
            "cursor declaration order must follow the catalog order:\n" + output);
        assertTrue(output.indexOf("SQLCursor CUR2") < output.indexOf("SQLCursor CUR3"),
            "cursor declaration order must follow the catalog order:\n" + output);
    }
}
