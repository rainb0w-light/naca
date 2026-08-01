package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import generate.LegacyLanguageRenderer;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityStructure;
import semantic.CDataEntity;
import semantic.CSubStringAttributReference;
import semantic.Verbs.CEntityConvertReference;
import semantic.Verbs.CEntityDivide;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaDivide} direct backend.
 * {@link CJavaFPacEntityFactory#NewEntityDivide(int)} must hand back the pure, target-neutral
 * {@link CEntityDivide} (no {@code generate.fpacjava} subclass); the FPac factory injects the
 * {@link LegacyDataRenderer#renderReference} bridge so the entity's
 * {@code getDividendReference()/getDivisorReference()/getResultReference()} reproduce the retired
 * backend's per-operand reference text, and rendering reaches the recursive ST4 assembler through
 * the FPAC_REFERENCE override binding {@code semantic.Verbs.CEntityDivide -> recursiveFPacDivideEntity}.
 *
 * <p>The retired backend wrote a single statement
 * {@code "divide(" + renderReference(what) + ", " + renderReference(by) + ").to(" +
 * renderReference(result) + ") ;"}. The FPac parser ({@code CFPacArithmeticOperation}, D operation)
 * always calls {@code SetDivide(var2, var1, false)}, so {@code result == what} (the quotient is
 * stored back into the dividend) and the rounded/remainder branches the frozen COBOL
 * {@code recursiveDivideEntity} template supports are never reachable on the FPac path. The
 * assertions below render the verb through the assembler ({@code renderRoot(divide, FPAC_REFERENCE)})
 * and pin that exact formula for EVERY operand shape the FPac parser actually builds for
 * {@code var1}/{@code var2}:
 * <ul>
 *   <li>a positioned substring wrapping a conversion buffer ({@code bufferX(<ref>, start, len)},
 *       the classic buffer operand) — routing it through the shared COBOL substring template would
 *       emit unbalanced parentheses;</li>
 *   <li>a numeric literal — the raw {@code csValue} ({@code 00050}), NOT the normalized javaNumber
 *       form the COBOL reference walk would emit;</li>
 *   <li>a string literal — the quoted/escaped form;</li>
 *   <li>an undefined reference — the legacy {@code [UNDEFINED]} marker;</li>
 *   <li>a plain field reference.</li>
 * </ul>
 * Each expected value is computed from the same {@code LegacyDataRenderer.renderReference} call the
 * deleted backend used, so a green test proves byte-parity by construction; the concrete literal
 * assertions additionally pin the real emitted text (and the {@code result == dividend} production
 * invariant) so the suite is not a tautology.
 */
class CFPacJavaDivideRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityDivide divide)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(divide, JavaTemplateRole.FPAC_REFERENCE).trim();
    }

    /** Mirrors the parser: {@code divide.SetDivide(var2, var1, false)} (result == dividend). */
    private static CEntityDivide divide(CJavaFPacEntityFactory factory, CDataEntity dividend, CDataEntity divisor)
    {
        CEntityDivide divide = factory.NewEntityDivide(1);
        divide.SetDivide(dividend, divisor, false);
        return divide;
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityDivide.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityDivide(1).getClass());
    }

    /**
     * The FPAC_REFERENCE override binding routes the shared {@code CEntityDivide} to the FPac
     * {@code recursiveFPacDivideEntity} template, never the frozen COBOL
     * {@code recursiveDivideEntity}. For a numeric dividend the two diverge: the FPac template keeps
     * the raw literal the retired backend emitted, whereas the COBOL reference walk would normalize
     * it (javaNumber formatting). Pinning the raw literal proves the FPac template — not the COBOL
     * one — produced the output.
     */
    @Test
    void fpacDivideUsesTheFpacTemplateNotTheCobolDivideTemplate()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDivide divide = divide(factory,
            factory.NewEntityNumber("00050"), factory.NewEntityNumber("00002"));

        String rendered = render(divide);
        assertTrue(rendered.startsWith("divide("),
            "FPac divide must lower to the divide(...).to(...) statement: " + rendered);
        assertTrue(rendered.contains("00050"),
            "FPac divide must keep the raw numeric literal, not the COBOL-normalized form: " + rendered);
        assertFalse(rendered.contains("toRounded"),
            "FPac divide never reaches the rounded branch: " + rendered);
    }

    /**
     * Live production invariant — the D operation stores the quotient back into the dividend
     * ({@code SetDivide(var2, var1, false)} sets {@code result = what}). The emitted {@code .to(...)}
     * argument must therefore equal the dividend argument, exactly as the retired backend rendered it.
     */
    @Test
    void quotientDestinationEqualsDividend()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure dividend = new CEntityStructure(1, "WS-TOTAL", catalog, "01");
        CEntityStructure divisor = new CEntityStructure(1, "WS-COUNT", catalog, "01");

        String rendered = render(divide(factory, dividend, divisor));
        String dividendRef = LegacyDataRenderer.renderReference(dividend, 1);
        String divisorRef = LegacyDataRenderer.renderReference(divisor, 1);
        assertEquals("divide(" + dividendRef + ", " + divisorRef + ").to(" + dividendRef + ") ;",
            rendered);
    }

    /**
     * Live production branch — a positioned substring wrapping a conversion buffer (the parser wraps
     * such an operand in {@code CSubStringAttributReference}). The retired backend emitted the
     * balanced {@code bufferX(<ref>, start, length)} call; the migration must preserve it (the COBOL
     * substring template would yield unbalanced parentheses).
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

        CEntityStructure divisor = new CEntityStructure(1, "WS-DIV", catalog, "01");
        String rendered = render(divide(factory, sub, divisor));

        String dividendRef = LegacyDataRenderer.renderReference(sub, 1);
        String divisorRef = LegacyDataRenderer.renderReference(divisor, 1);
        assertEquals("divide(" + dividendRef + ", " + divisorRef + ").to(" + dividendRef + ") ;",
            rendered);
        // Pin the concrete balanced buffer call shape and prove the parentheses balance.
        String reference = LegacyDataRenderer.renderReference(buffer, 1);
        assertTrue(dividendRef.startsWith("bufferX(" + reference + ", "),
            "substring must complete the unclosed bufferX call head: " + dividendRef);
        assertEquals(0, balance(dividendRef),
            "balanced parentheses expected: " + dividendRef);
    }

    /**
     * Live production branch — a numeric operand. The retired backend emitted the raw {@code csValue}
     * ({@code 00050}); the COBOL reference walk would normalize it, so the migration keeps the raw
     * literal in both the dividend and the (result == dividend) quotient destination.
     */
    @Test
    void numberOperandPreservesLegacyRawLiteral()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDivide divide = divide(factory,
            factory.NewEntityNumber("00050"), factory.NewEntityNumber("00002"));

        String rendered = render(divide);
        assertEquals("divide(00050, 00002).to(00050) ;", rendered);
        assertEquals("divide(" + LegacyDataRenderer.renderReference(factory.NewEntityNumber("00050"), 1)
            + ", " + LegacyDataRenderer.renderReference(factory.NewEntityNumber("00002"), 1)
            + ").to(" + LegacyDataRenderer.renderReference(factory.NewEntityNumber("00050"), 1) + ") ;",
            rendered);
    }

    /** A {@code 0x} numeric operand keeps the legacy {@code hexa("...")} form in both positions. */
    @Test
    void hexaNumberOperandPreservesLegacyForm()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDivide divide = divide(factory,
            factory.NewEntityNumber("0x1F"), factory.NewEntityNumber("00002"));

        assertEquals("divide(hexa(\"1F\"), 00002).to(hexa(\"1F\")) ;", render(divide));
    }

    /** A string operand keeps the quoted/escaped literal form. */
    @Test
    void stringOperandPreservesLegacyLiteral()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDivide divide = divide(factory,
            factory.NewEntityString("HELLO"), factory.NewEntityString("WORLD"));

        String rendered = render(divide);
        assertEquals("divide(\"HELLO\", \"WORLD\").to(\"HELLO\") ;", rendered);
        assertEquals("divide(" + LegacyDataRenderer.renderReference(factory.NewEntityString("HELLO"), 1)
            + ", " + LegacyDataRenderer.renderReference(factory.NewEntityString("WORLD"), 1)
            + ").to(" + LegacyDataRenderer.renderReference(factory.NewEntityString("HELLO"), 1) + ") ;",
            rendered);
    }

    /**
     * An undefined operand keeps the legacy {@code [UNDEFINED]} marker. The shared COBOL unknown
     * reference template renders the empty string, which would produce {@code divide(, ...) ;} — a
     * call matching no {@code BaseProgram.divide} overload; the migration preserves the marker.
     */
    @Test
    void unknownReferenceOperandPreservesLegacyMarker()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityDivide divide = divide(factory,
            factory.NewEntityUnknownReference(1, "MISSING"), factory.NewEntityNumber("00002"));

        assertEquals("divide([UNDEFINED], 00002).to([UNDEFINED]) ;", render(divide));
    }

    /**
     * End-to-end production lowering mirroring {@code CFPacArithmeticOperation} (D operation) +
     * {@code CFPacJavaProcedure.DoExport}. The verb is built with {@code factory.NewEntityDivide(line)}
     * (which binds the legacy output controller at creation), the operands are set with the exact
     * {@code SetDivide(var2, var1, false)} call the parser issues, then {@code parent.AddChild(divide)}
     * — which does NOT propagate bindings. Driving the owning procedure's export through the SAME
     * statement {@code CFPacJavaProcedure.DoExport} runs ({@code exportChildren(this, false,
     * FPAC_REFERENCE)}) emits {@code divide(00050, 00002).to(00050) ;} into the bound output: the pure
     * verb (no {@code DoExport}) falls through to {@code renderRoot(divide, FPAC_REFERENCE)} and is
     * written via {@code writeLine}, which {@code requireOutput}s the controller bound to THAT entity.
     *
     * <p>The owning procedure self-binds in its constructor (production behavior); this test performs
     * NO {@code LegacyLanguageRenderer.bind(owner, out)} after {@code AddChild}, so a missing factory
     * bind would surface here exactly as it does in production — an
     * {@code IllegalStateException("No legacy output controller bound to ...")} — rather than being
     * masked by a whole-tree bind the FPac engine never performs. This proves production reachability
     * of the recursive-ST4 lowering, not just a hand-rendered entity.
     */
    @Test
    void productionProcedureBodyRendersDivideThroughAssembler()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog productionCatalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(productionCatalog, out);

        // Constructor self-binds the owner to `out`, exactly as in production.
        CFPacJavaProcedure owner = new CFPacJavaProcedure(2, "MAIN", productionCatalog, out, null);

        // Factory binds the verb to `out` at creation; AddChild below does not propagate. The
        // operand order mirrors the parser: SetDivide(var2, var1, false) -> result == var2 (dividend).
        CEntityDivide divide = factory.NewEntityDivide(3);
        divide.SetDivide(factory.NewEntityNumber("00050"), factory.NewEntityNumber("00002"), false);
        owner.AddChild(divide);

        // The exact production driver: CFPacJavaProcedure.DoExport -> exportChildren(FPAC_REFERENCE).
        LegacyLanguageRenderer.invokeExport(owner);

        String rendered = out.getCapturedOutput();
        assertTrue(rendered.contains("divide(00050, 00002).to(00050) ;"),
            "production FPac rendering must emit the divide through the recursive assembler with the "
                + "raw legacy operand references and the quotient stored back into the dividend; got:\n"
                + rendered);
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
