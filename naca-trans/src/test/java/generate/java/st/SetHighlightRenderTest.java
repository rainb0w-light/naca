package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldAttributeReference;
import semantic.forms.CEntityFieldHighlight;
import semantic.forms.CEntitySetHighligh;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource highlight action ({@code MOVE <val> TO <FIELD>-H}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaSetHighlight}
 * emitted, from its {@code DoExport}, the protected {@code OnlineProgram} highlight
 * calls selected from independent semantic flag slots, in this order: blink emitted
 * {@code setFieldBlink(<field>) ;}, reverse {@code setFieldReverse(<field>) ;},
 * underline {@code setFieldUnderline(<field>) ;}, normal
 * {@code setFieldUnhighlighted(<field>) ;}, a moved value
 * {@code moveHighLighting(<value>, <field>) ;}, and when no slot was set a reset
 * branch. The flags are independent, so the {@code "6"} highlight (reverse +
 * underline) emitted two statements. This slice de-abstracts the pure semantic
 * entity {@link semantic.forms.CEntitySetHighligh} with read-only getters over those
 * slots ({@code getField()}, {@code getHighLightValue()}, {@code isBlink()},
 * {@code isReverse()}, {@code isUnderlined()}, {@code isNormal()}, {@code isReset()})
 * and binds it through the sanctioned BMS forms-island manifest
 * {@code semantic-runtime-bindings.properties} (never the concrete manifest that
 * {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to the
 * {@code recursiveSetHighlightEntity} template, which reads only {@code entity.*}
 * properties: {@code <entity.field>} and {@code <entity.highLightValue>} are data
 * references that unfold recursively through the assembler. The emitted calls are
 * contracted as operations {@code bms.highlight.blink} / {@code bms.highlight.reverse}
 * / {@code bms.highlight.underline} / {@code bms.highlight.unhighlighted} /
 * {@code bms.highlight.move.var} / {@code bms.highlight.move.attr} (both
 * {@code moveHighLighting} runtime overload families, selected by the rendered static
 * type of the MOVE source; runtime-operations.yaml, template-runtime-requirements.yaml,
 * feature {@code bms.highlight.set}).
 *
 * <p>Latent legacy defect fixed: the retired reset branch named
 * {@code resetFieldHighlighting(<field>)}, which has no naca-rt signature and never
 * compiled. The reset slot now maps to the real
 * {@code OnlineProgram.setFieldUnhighlighted} (highlighting OFF), the semantic
 * equivalent — so generated Java compiles on every reachable template branch.
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldHighlight.GetSpecialAssignment}
 * is the parser-driven construction of this entity ({@code MOVE "1" TO <FIELD>-H}
 * lowers to blink, {@code "2"} to reverse, {@code "4"} to underline, {@code "6"} to
 * reverse + underline, {@code "0"}/HIGH-VALUE to normal, LOW-VALUE to the reset slot,
 * and a moved data reference to {@code moveHighLighting}); it now builds the pure
 * entity through the rewired factory ({@code CJavaEntityFactory.NewEntitySetHighlight},
 * inherited by {@code CJavaEntityFactoryST}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class SetHighlightRenderTest
{
    private static final String OWNER_REFERENCE = "MAP.WS-FIELD";

    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static MockDataEntity owner()
    {
        return new MockDataEntity(1, OWNER_REFERENCE);
    }

    private static String render(CEntitySetHighligh setHighlight)
    {
        // Statements unfold through the assembler exactly as the procedure
        // templates render their activeChildren: REFERENCE role, resolved by the
        // runtime manifest binding semantic.forms.CEntitySetHighligh ->
        // recursiveSetHighlightEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(setHighlight, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production analysis path for a highlight constant:
     * {@code MOVE "<val>" TO <FIELD>-H} drives
     * {@link CEntityFieldHighlight#GetSpecialAssignment(parser.expression.CTerminal, semantic.CBaseEntityFactory, int)},
     * which builds the highlight action around a {@link CEntityFieldAttributeReference}
     * to the owner field.
     */
    private static CEntitySetHighligh lowerHighlightConstant(
        String value, CJavaEntityFactoryST factory)
    {
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog(), owner());
        CBaseActionEntity action =
            fieldHighlight.GetSpecialAssignment(new MockTerminal(value), factory, 7);
        return assertInstanceOf(CEntitySetHighligh.class, action);
    }

    @Test
    @DisplayName("MOVE 1 TO <FIELD>-H renders setFieldBlink(field) (legacy DoExport parity)")
    void blinkRendersSetFieldBlink()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntitySetHighligh setHighlight = lowerHighlightConstant("1", factory);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetHighligh.class, setHighlight.getClass());
        // The field slot is the production attribute reference wrapping the owner.
        assertInstanceOf(CEntityFieldAttributeReference.class, setHighlight.getField());
        assertTrue(setHighlight.isBlink());
        assertFalse(setHighlight.isReverse());
        assertFalse(setHighlight.isReset());
        assertEquals("setFieldBlink(" + OWNER_REFERENCE + ") ;", render(setHighlight).trim());
    }

    @Test
    @DisplayName("MOVE 2 TO <FIELD>-H renders setFieldReverse(field)")
    void reverseRendersSetFieldReverse()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntitySetHighligh setHighlight = lowerHighlightConstant("2", factory);
        assertTrue(setHighlight.isReverse());
        assertEquals("setFieldReverse(" + OWNER_REFERENCE + ") ;", render(setHighlight).trim());
    }

    @Test
    @DisplayName("MOVE 4 TO <FIELD>-H renders setFieldUnderline(field)")
    void underlineRendersSetFieldUnderline()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntitySetHighligh setHighlight = lowerHighlightConstant("4", factory);
        assertTrue(setHighlight.isUnderlined());
        assertEquals("setFieldUnderline(" + OWNER_REFERENCE + ") ;", render(setHighlight).trim());
    }

    @Test
    @DisplayName("MOVE 6 TO <FIELD>-H renders both reverse and underline (independent flags)")
    void reverseUnderlineRendersTwoStatements()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // "6" sets reverse AND underline: two statements in the legacy branch order,
        // no blank line for the absent flags.
        CEntitySetHighligh setHighlight = lowerHighlightConstant("6", factory);
        assertTrue(setHighlight.isReverse());
        assertTrue(setHighlight.isUnderlined());
        assertFalse(setHighlight.isReset());
        assertEquals(
            "setFieldReverse(" + OWNER_REFERENCE + ") ;\n"
                + "setFieldUnderline(" + OWNER_REFERENCE + ") ;",
            render(setHighlight).trim());
    }

    @Test
    @DisplayName("MOVE 0 / HIGH-VALUE TO <FIELD>-H renders setFieldUnhighlighted(field)")
    void normalRendersSetFieldUnhighlighted()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        for (String normal : new String[] { "0", "HIGH-VALUE", "HIGH-VALUES" })
        {
            CEntitySetHighligh setHighlight = lowerHighlightConstant(normal, factory);
            assertTrue(setHighlight.isNormal(), "normal flag for constant " + normal);
            assertFalse(setHighlight.isReset());
            assertEquals("setFieldUnhighlighted(" + OWNER_REFERENCE + ") ;",
                render(setHighlight).trim());
        }
    }

    @Test
    @DisplayName("MOVE LOW-VALUE TO <FIELD>-H maps the legacy reset branch to setFieldUnhighlighted")
    void resetRendersSetFieldUnhighlighted()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // LOW-VALUE calls Reset(): no flag set, so the legacy reset branch fired. The
        // retired backend emitted resetFieldHighlighting(<field>) — a call with no
        // naca-rt signature that never compiled. The reset slot now maps to the real
        // setFieldUnhighlighted (highlighting OFF).
        CEntitySetHighligh setHighlight = lowerHighlightConstant("LOW-VALUE", factory);
        assertFalse(setHighlight.isBlink());
        assertFalse(setHighlight.isReverse());
        assertFalse(setHighlight.isUnderlined());
        assertFalse(setHighlight.isNormal());
        assertNull(setHighlight.getHighLightValue());
        assertTrue(setHighlight.isReset());
        assertEquals("setFieldUnhighlighted(" + OWNER_REFERENCE + ") ;",
            render(setHighlight).trim());
    }

    @Test
    @DisplayName("MOVE <value> TO <FIELD>-H renders moveHighLighting(value, field) (production lowering)")
    void movedValueRendersMoveHighLighting()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // The data-reference overload of GetSpecialAssignment is the production path
        // for a moved highlight value (MOVE <var> TO <FIELD>-H).
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog(), owner());
        CBaseActionEntity action = fieldHighlight.GetSpecialAssignment(
            new MockDataEntity(2, "WS-ATTR"), factory, 8);

        CEntitySetHighligh setHighlight = assertInstanceOf(CEntitySetHighligh.class, action);
        assertEquals(CEntitySetHighligh.class, setHighlight.getClass());
        assertInstanceOf(CEntityFieldAttributeReference.class, setHighlight.getField());
        assertFalse(setHighlight.isReset());
        assertEquals("moveHighLighting(WS-ATTR, " + OWNER_REFERENCE + ") ;",
            render(setHighlight).trim());
    }

    @Test
    @DisplayName("an unsupported highlight constant still lowers to no action")
    void unsupportedConstantLowersToNothing()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog(), owner());
        // "3" is not a recognized highlight constant: GetSpecialAssignment returns
        // null (legacy intGetSpecialAssignment fall-through preserved).
        assertNull(fieldHighlight.GetSpecialAssignment(new MockTerminal("3"), factory, 10));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        MockDataEntity field = owner();

        CEntitySetHighligh setHighlight = factory.NewEntitySetHighlight(1, field);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetHighligh.class, setHighlight.getClass());
        assertSame(field, setHighlight.getField());
        // Bare factory construction: no flag and no moved value -> the reset slot,
        // exactly the legacy DoExport final branch (now the real setFieldUnhighlighted).
        assertTrue(setHighlight.isReset());
        assertEquals("setFieldUnhighlighted(" + OWNER_REFERENCE + ") ;",
            render(setHighlight).trim());
    }

    @Test
    @DisplayName("the retired backend's branch order is preserved")
    void legacyBranchOrderPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySetHighligh setHighlight = factory.NewEntitySetHighlight(1, owner());

        // Legacy DoExport tested the flags in the fixed order blink, reverse,
        // underline, normal, move: with blink + reverse + underline all set, all three
        // statements render in that order.
        setHighlight.SetBlink();
        setHighlight.SetReverse();
        setHighlight.SetUnderlined();
        assertFalse(setHighlight.isReset());
        assertEquals(
            "setFieldBlink(" + OWNER_REFERENCE + ") ;\n"
                + "setFieldReverse(" + OWNER_REFERENCE + ") ;\n"
                + "setFieldUnderline(" + OWNER_REFERENCE + ") ;",
            render(setHighlight).trim());
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());

        // No field reference -> ignored (never rendered).
        CEntitySetHighligh noField = factory.NewEntitySetHighlight(1, null);
        assertTrue(noField.ignore());

        // An ignored moved value -> ignored (legacy second clause).
        CEntitySetHighligh ignoredValue = factory.NewEntitySetHighlight(1, owner());
        ignoredValue.SetHighLight(new IgnoredDataEntity());
        assertTrue(ignoredValue.ignore());

        // A live field and a live value -> rendered.
        CEntitySetHighligh live = factory.NewEntitySetHighlight(1, owner());
        live.SetHighLight(new MockDataEntity(2, "WS-ATTR"));
        assertFalse(live.ignore());
    }

    /** A data reference the analysis marks as ignored ({@code ignore()} true). */
    private static final class IgnoredDataEntity extends MockDataEntity
    {
        private IgnoredDataEntity()
        {
            super(2, "WS-IGNORED");
        }

        @Override
        public boolean ignore()
        {
            return true;
        }
    }

    /** Minimal terminal standing in for the lexer-produced constant token. */
    private static final class MockTerminal extends parser.expression.CTerminal
    {
        private final String value;

        private MockTerminal(String value)
        {
            this.value = value;
        }

        @Override
        public String GetValue()
        {
            return value;
        }

        @Override
        public boolean IsReference()
        {
            return false;
        }

        @Override
        public void ExportTo(org.w3c.dom.Element e, org.w3c.dom.Document root)
        {
            // not exercised by semantic analysis
        }

        @Override
        public CDataEntity GetDataEntity(int nLine, semantic.CBaseEntityFactory factory)
        {
            return null;
        }

        @Override
        public boolean IsNumber()
        {
            return false;
        }
    }
}
