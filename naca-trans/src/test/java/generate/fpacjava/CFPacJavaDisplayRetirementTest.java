package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityStructure;
import semantic.CSubStringAttributReference;
import semantic.Verbs.CEntityConvertReference;
import semantic.Verbs.CEntityDisplay;
import semantic.Verbs.CEntityDisplay.Upon;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaDisplay} direct backend.
 * {@link CJavaFPacEntityFactory#NewEntityDisplay(int, Upon)} must hand back the pure,
 * target-neutral {@link CEntityDisplay} (no {@code generate.fpacjava} subclass); the FPac
 * factory injects the {@link LegacyDataRenderer#renderReference} bridge so the entity's
 * {@code getDisplayReferences()} reproduces the retired backend's per-operand reference text,
 * and rendering reaches the recursive ST4 assembler through the FPAC_REFERENCE override binding
 * {@code semantic.Verbs.CEntityDisplay -> recursiveFPacDisplayEntity}.
 *
 * <p>The retired backend wrote, for each operand, {@code "wto.display(" +
 * LegacyDataRenderer.renderReference(item, getLine()) + ") ;"} ("wto" is the protected
 * {@code nacaLib.varEx.Console} field on {@code nacaLib.fpacPrgEnv.FPacProgram}). The assertions
 * below render the verb through the assembler ({@code renderRoot(disp, FPAC_REFERENCE)}) and pin
 * that exact formula for EVERY operand shape the FPac parser ({@code CFPacWTO}) actually builds:
 * <ul>
 *   <li>a positioned substring wrapping a conversion buffer ({@code bufferX(<ref>, start, len)},
 *       the classic buffer WTO) — the shape the previous patch broke into unbalanced
 *       parentheses by re-routing it to the shared COBOL substring template;</li>
 *   <li>a numeric literal — raw {@code csValue} ({@code 00050}) and {@code hexa("...")} for a
 *       {@code 0x} value, NOT the normalized javaNumber form the COBOL template would emit;</li>
 *   <li>a string literal — the quoted/escaped form;</li>
 *   <li>an undefined reference — the legacy {@code [UNDEFINED]} marker, NOT the empty string
 *       (which matches no {@code Console.display} overload);</li>
 *   <li>a plain field reference.</li>
 * </ul>
 * Each expected value is computed from the same {@code LegacyDataRenderer.renderReference} call
 * the deleted backend used, so a green test proves byte-parity by construction; the concrete
 * literal assertions additionally pin the real emitted text so the suite is not a tautology.
 */
class CFPacJavaDisplayRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityDisplay display)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(display, JavaTemplateRole.FPAC_REFERENCE).trim();
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityDisplay.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityDisplay(1, Upon.CONSOLE).getClass());
    }

    /**
     * The FPAC_REFERENCE override binding routes the shared {@code CEntityDisplay} to the FPac
     * {@code recursiveFPacDisplayEntity} template ("wto.display(...) ;"), never the frozen COBOL
     * {@code display} template (which would emit {@code console().display(...)} / {@code display(...)}
     * joining operands with " + "). This is the production path the legacy traversal takes once the
     * verb container lowers under FPAC_REFERENCE.
     */
    @Test
    void fpacDisplayUsesTheFpacWtoTemplateNotTheCobolDisplayTemplate()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(factory.NewEntityNumber("00050"));

        String rendered = render(display);
        assertTrue(rendered.startsWith("wto.display("),
            "FPac display must lower to the wto console statement: " + rendered);
        assertFalse(rendered.contains("console()"),
            "FPac display must not reuse the COBOL console().display template: " + rendered);
        assertFalse(rendered.contains(" + "),
            "FPac display must not join operands the COBOL way: " + rendered);
    }

    /**
     * Live production branch — the classic buffer WTO ({@code WTO -08000,00050}): the parser wraps
     * an alphanumeric conversion reference in a positioned substring. The retired backend emitted
     * {@code wto.display(bufferX(<ref>, start, length)) ;}; the migration must preserve that
     * balanced, compilable call (the previous patch produced {@code subString(bufferX(...)} with
     * unbalanced parentheses by hijacking the COBOL substring template).
     */
    @Test
    void bufferSubStringOperandPreservesLegacyOutput()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure buffer = new CEntityStructure(1, "WORKING", catalog, "01");
        CEntityConvertReference conv = factory.NewEntityConvert(1);
        conv.convertToAlphaNum(buffer);
        assertTrue(conv.HasAccessors(),
            "convert reference must report accessors so the substring wrapper composes it");
        CSubStringAttributReference sub = factory.NewEntitySubString(1);
        sub.SetReference(conv,
            factory.NewEntityExprTerminal(factory.NewEntityNumber("08000")),
            factory.NewEntityExprTerminal(factory.NewEntityNumber("00050")));

        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(sub);

        String rendered = render(display);
        String legacyReference = LegacyDataRenderer.renderReference(sub, 1);
        assertEquals("wto.display(" + legacyReference + ") ;", rendered);
        // Pin the concrete balanced buffer call shape and prove the parentheses balance.
        String reference = LegacyDataRenderer.renderReference(buffer, 1);
        assertTrue(legacyReference.startsWith("bufferX(" + reference + ", "),
            "substring must complete the unclosed bufferX call head: " + legacyReference);
        assertTrue(legacyReference.endsWith(")"),
            "the composed buffer reference must be balanced: " + legacyReference);
        assertEquals(0, balance(legacyReference),
            "balanced parentheses expected: " + legacyReference);
    }

    /**
     * Live production branch — a numeric operand. The retired backend emitted the raw
     * {@code csValue} ({@code 00050}); the COBOL number template would instead normalize it
     * (javaNumber formatting), so the migration must keep the raw literal.
     */
    @Test
    void numberOperandPreservesLegacyRawLiteral()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(factory.NewEntityNumber("00050"));

        String rendered = render(display);
        assertEquals("wto.display(00050) ;", rendered);
        assertEquals("wto.display(" + LegacyDataRenderer.renderReference(
            factory.NewEntityNumber("00050"), 1) + ") ;", rendered);
    }

    /** A {@code 0x} numeric operand keeps the legacy {@code hexa("...")} form (String -> display). */
    @Test
    void hexaNumberOperandPreservesLegacyForm()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(factory.NewEntityNumber("0x1F"));

        assertEquals("wto.display(hexa(\"1F\")) ;", render(display));
    }

    /** A string operand keeps the quoted/escaped literal form (String -> Console.display). */
    @Test
    void stringOperandPreservesLegacyLiteral()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(factory.NewEntityString("HELLO"));

        String rendered = render(display);
        assertEquals("wto.display(\"HELLO\") ;", rendered);
        assertEquals("wto.display(" + LegacyDataRenderer.renderReference(
            factory.NewEntityString("HELLO"), 1) + ") ;", rendered);
    }

    /**
     * An undefined operand keeps the legacy {@code [UNDEFINED]} marker. The shared COBOL unknown
     * reference template renders the empty string, which would produce {@code wto.display() ;} —
     * a call matching no {@code Console.display} overload; the migration preserves the marker.
     */
    @Test
    void unknownReferenceOperandPreservesLegacyMarker()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(factory.NewEntityUnknownReference(1, "MISSING"));

        assertEquals("wto.display([UNDEFINED]) ;", render(display));
    }

    /** A plain field operand renders through the shared reference binding, unchanged. */
    @Test
    void plainFieldOperandPreservesLegacyReference()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure field = new CEntityStructure(1, "MY-FIELD", catalog, "01");
        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(field);

        String rendered = render(display);
        assertEquals("wto.display(" + LegacyDataRenderer.renderReference(field, 1) + ") ;",
            rendered);
    }

    /** The retired backend wrote one statement PER OPERAND; the template keeps that shape. */
    @Test
    void multipleOperandsEmitOneStatementPerLine()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDisplay display = factory.NewEntityDisplay(1, Upon.CONSOLE);
        display.AddItemToDisplay(factory.NewEntityNumber("1"));
        display.AddItemToDisplay(factory.NewEntityString("TWO"));

        String rendered = render(display);
        String[] lines = rendered.split("\n");
        assertEquals(2, lines.length, "one wto statement per operand: " + rendered);
        assertEquals("wto.display(" + LegacyDataRenderer.renderReference(
            factory.NewEntityNumber("1"), 1) + ") ;", lines[0].trim());
        assertEquals("wto.display(" + LegacyDataRenderer.renderReference(
            factory.NewEntityString("TWO"), 1) + ") ;", lines[1].trim());
    }

    /** Net parenthesis depth of a fragment (0 == balanced). */
    private static int balance(String fragment)
    {
        int depth = 0;
        for (char c : fragment.toCharArray())
        {
            if (c == '(')
            {
                depth++;
            }
            else if (c == ')')
            {
                depth--;
            }
        }
        return depth;
    }
}
