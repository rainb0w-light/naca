package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CICS.CEntityCICSIgnoreCondition;

/**
 * Embedded CICS IGNORE CONDITION rendering through the recursive assembler (the
 * production path). CICS IGNORE CONDITION is rendered as a REFERENCE-role
 * executable child via the {@code recursiveCICSIgnoreConditionEntity} binding;
 * this test drives it directly with lightweight mocks and asserts byte-for-byte
 * parity with the output shapes the retired
 * {@code CJavaCICSIgnoreCondition.DoExport} direct backend produced:
 * <ul>
 *   <li>a single condition renders
 *       {@code CESM.ignoreCondition("cond") ;}</li>
 *   <li>multiple conditions chain into a single statement terminated by
 *       {@code ;}</li>
 *   <li>all conditions are in parser insertion order</li>
 * </ul>
 * Replaces the legacy {@code generate.java.CICS.CJavaCICSIgnoreCondition}
 * direct backend.
 */
class CICSIgnoreConditionRenderTest
{
    private static CEntityCICSIgnoreCondition entity()
    {
        CEntityCICSIgnoreCondition e = new CEntityCICSIgnoreCondition(1, null);
        generate.LegacyLanguageRenderer.bind(e, new MockJavaExporter());
        return e;
    }

    @Test
    @DisplayName("single condition renders CESM.ignoreCondition(<cond>) ;")
    void singleCondition()
    {
        CEntityCICSIgnoreCondition e = entity();
        e.IgnoreCondition("COND1");
        // Rendering must only read the semantic value prepared above.
        generate.LegacyLanguageRenderer.bind(e, null);
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(e, JavaTemplateRole.REFERENCE);
        assertEquals("CESM.ignoreCondition(\"COND1\") ;", output.trim());
    }

    @Test
    @DisplayName("multiple conditions chain .ignoreCondition on the CESM prefix")
    void multipleConditions()
    {
        CEntityCICSIgnoreCondition e = entity();
        e.IgnoreCondition("COND1");
        e.IgnoreCondition("COND2");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(e, JavaTemplateRole.REFERENCE);
        assertEquals(
            "CESM.ignoreCondition(\"COND1\").ignoreCondition(\"COND2\") ;",
            output.trim());
    }

    @Test
    @DisplayName("three conditions chain correctly")
    void threeConditions()
    {
        CEntityCICSIgnoreCondition e = entity();
        e.IgnoreCondition("DIVERT");
        e.IgnoreCondition("CANCEL");
        e.IgnoreCondition("PURGE");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(e, JavaTemplateRole.REFERENCE);
        assertEquals(
            "CESM.ignoreCondition(\"DIVERT\").ignoreCondition(\"CANCEL\").ignoreCondition(\"PURGE\") ;",
            output.trim());
    }

    @Test
    @DisplayName("empty conditions list produces no output")
    void emptyConditions()
    {
        CEntityCICSIgnoreCondition e = entity();
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(e, JavaTemplateRole.REFERENCE);
        assertTrue(output.isEmpty(), "empty conditions produce no output: " + output);
    }
}
