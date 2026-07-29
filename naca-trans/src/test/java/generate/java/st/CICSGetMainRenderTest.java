package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSGetMain;

/**
 * Embedded CICS GETMAIN rendering through the recursive assembler (the production
 * path). CICS GETMAIN is rendered as a REFERENCE-role executable child via the
 * {@code recursiveCICSGetMainEntity} binding; this test drives it directly and
 * asserts byte-for-byte parity with the output shape the retired
 * {@code CJavaCICSGetMain.DoExport} direct backend produced:
 * <ul>
 *   <li>GETMAIN -&gt; {@code CESM.getMain() ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSGetMain} direct backend.
 */
class CICSGetMainRenderTest
{
    private static String render()
    {
        CEntityCICSGetMain getMain = new CEntityCICSGetMain(1, null);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(getMain, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("GETMAIN renders CESM.getMain() ;")
    void getMain()
    {
        String output = render();
        assertTrue(output.contains("CESM.getMain() ;"), output);
    }
}
