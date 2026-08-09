package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityFieldHighlight;
import semantic.forms.CEntityIsFieldHighlight;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-highlight condition ({@code IF <FIELD>H = <code>} /
 * {@code <> <code>}) rendering through the recursive assembler (the production
 * path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaIsFieldHighlight}
 * emitted, from its {@code Export}, "is" + ("Not" when its {@code isopposite} flag was
 * set) + one of "FieldUnderlined" / "FieldBlink" / "FieldReverse" /
 * "FieldHighlightNormal" (that precedence order) + "({@code <reference>})": the
 * protected {@code OnlineProgram} condition calls {@code is[Not]FieldUnderlined(Edit)},
 * {@code is[Not]FieldReverse(Edit)} and {@code is[Not]FieldHighlightNormal(Edit)}, plus
 * {@code isFieldBlink(Edit)}. Its {@code GetOppositeCondition} built a flag-flipped copy
 * of itself (not a {@code CEntityCondNot} wrap). This slice de-abstracts the pure
 * semantic entity {@link semantic.forms.CEntityIsFieldHighlight} with read-only getters
 * over its data reference ({@code getReference()}), its negation flag
 * ({@code isOpposite()}) and its three mode flags ({@code isUnderlined()} /
 * {@code isBlink()} / {@code isReverse()}; normal is the all-clear else case), and binds
 * it through the sanctioned BMS forms-island manifest
 * {@code semantic-runtime-bindings.properties} (never the concrete manifest that
 * {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to the
 * {@code recursiveIsFieldHighlightEntity} template, which reads only {@code entity.*}
 * properties: it selects the mode in the legacy precedence order, branches on
 * {@code <entity.opposite>} to select the positive/negated runtime call — preserving
 * the seven legacy {@code OnlineProgram} calls byte-for-byte — and
 * {@code <entity.reference>} is a data reference that unfolds recursively through the
 * assembler. The emitted calls are contracted as operations
 * {@code bms.field.highlight.underlined} / {@code .underlined.not} / {@code .blink} /
 * {@code .reverse} / {@code .reverse.not} / {@code .normal} / {@code .normal.not}
 * (runtime-operations.yaml, template-runtime-requirements.yaml, feature
 * {@code bms.condIsFieldHighlight}).
 *
 * <p>One legacy output is deliberately NOT reproduced byte-for-byte: the retired
 * backend's blink + opposite branch emitted {@code isNotFieldBlink(<reference>)}, a
 * call with no naca-rt implementation ({@code OnlineProgram} declares
 * {@code isFieldBlink} but no {@code isNotFieldBlink} anywhere) — invalid generated
 * Java on a production-reachable branch ({@code IF <FIELD>H <> 1}). The contract gate
 * requires every reachable template branch to compile, so that branch renders the Java
 * negation {@code !isFieldBlink(<reference>)} of the same contracted call instead:
 * semantically identical (OnlineProgram's own {@code isNotField*} helpers are
 * implemented as exactly such a negation) and compilable.
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldHighlight.GetSpecialCondition}
 * is the parser-driven construction of this condition ({@code IF <FIELD>H = 1} lowers to
 * a blink test around the owner field): "4" sets underlined, "2" reverse, "1" blink,
 * HIGH-VALUE / HIGH-VALUES sets normal, and {@code IS_DIFFERENT} lowers through
 * {@code setOpposite} (renders the negated call); any other value never lowers (the
 * legacy pipeline returned null and the caller fell back to a plain comparison). It
 * builds the pure entity through the rewired factory
 * ({@code CJavaEntityFactory.NewEntityIsFieldHighlight}, inherited by
 * {@code CJavaEntityFactory}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class IsFieldHighlightRenderTest
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

    private static String render(CBaseEntityCondition condition)
    {
        // Conditions unfold through the assembler exactly as recursiveIfEntity renders
        // its condition: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityIsFieldHighlight -> recursiveIsFieldHighlightEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production analysis path: {@code IF <FIELD>H <op> <code>} drives
     * {@link CEntityFieldHighlight#GetSpecialCondition(int, String,
     * CBaseEntityCondition.EConditionType, semantic.CBaseEntityFactory)}, which builds
     * the highlight condition around the owner field reference (IS_EQUAL -> positive,
     * IS_DIFFERENT -> setOpposite).
     */
    private static CEntityIsFieldHighlight lowerFieldHighlightCondition(
        CJavaEntityFactory factory, String code,
        CBaseEntityCondition.EConditionType type)
    {
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog(), owner());
        CBaseEntityCondition condition =
            fieldHighlight.GetSpecialCondition(1, code, type, factory);
        return assertInstanceOf(CEntityIsFieldHighlight.class, condition);
    }

    @Test
    @DisplayName("IF <FIELD>H = 1 renders isFieldBlink(field) (legacy Export parity)")
    void fieldHighlightEqualsOneRendersIsFieldBlink()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldHighlight condition = lowerFieldHighlightCondition(
            factory, "1", CBaseEntityCondition.EConditionType.IS_EQUAL);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldHighlight.class, condition.getClass());
        // The reference slot is populated (the production owner field).
        assertFalse(condition.getReference() == null);
        assertTrue(condition.isBlink());
        assertFalse(condition.isReverse());
        assertFalse(condition.isUnderlined());
        assertFalse(condition.isOpposite());
        assertEquals(7, condition.GetPriorityLevel());
        assertTrue(condition.isBinaryCondition());
        // Byte-for-byte the retired backend's Export: isFieldBlink(<reference>).
        assertEquals("isFieldBlink(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("IF <FIELD>H <> 1 renders !isFieldBlink(field): the legacy isNotFieldBlink call has no naca-rt signature, so the negation rides Java's ! operator on the contracted isFieldBlink call")
    void fieldHighlightDifferentOneRendersNegatedIsFieldBlink()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        // IS_DIFFERENT lowers through setOpposite — the production-reachable branch
        // whose legacy emission (isNotFieldBlink) never compiled.
        CEntityIsFieldHighlight condition = lowerFieldHighlightCondition(
            factory, "1", CBaseEntityCondition.EConditionType.IS_DIFFERENT);

        assertTrue(condition.isBlink());
        assertTrue(condition.isOpposite());
        // Semantically identical to the legacy intent ("field does not blink") and
        // compilable: !isFieldBlink(<reference>), negating the contracted
        // bms.field.highlight.blink call instead of calling a nonexistent
        // isNotFieldBlink.
        assertEquals("!isFieldBlink(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("IF <FIELD>H = 4 / <> 4 renders is[Not]FieldUnderlined(field) (legacy Export parity)")
    void fieldHighlightFourRendersIsFieldUnderlined()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldHighlight positive = lowerFieldHighlightCondition(
            factory, "4", CBaseEntityCondition.EConditionType.IS_EQUAL);
        assertTrue(positive.isUnderlined());
        assertFalse(positive.isOpposite());
        assertEquals("isFieldUnderlined(" + OWNER_REFERENCE + ")", render(positive));

        CEntityIsFieldHighlight negative = lowerFieldHighlightCondition(
            factory, "4", CBaseEntityCondition.EConditionType.IS_DIFFERENT);
        assertTrue(negative.isUnderlined());
        assertTrue(negative.isOpposite());
        assertEquals("isNotFieldUnderlined(" + OWNER_REFERENCE + ")", render(negative));
    }

    @Test
    @DisplayName("IF <FIELD>H = 2 / <> 2 renders is[Not]FieldReverse(field) (legacy Export parity)")
    void fieldHighlightTwoRendersIsFieldReverse()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldHighlight positive = lowerFieldHighlightCondition(
            factory, "2", CBaseEntityCondition.EConditionType.IS_EQUAL);
        assertTrue(positive.isReverse());
        assertFalse(positive.isOpposite());
        assertEquals("isFieldReverse(" + OWNER_REFERENCE + ")", render(positive));

        CEntityIsFieldHighlight negative = lowerFieldHighlightCondition(
            factory, "2", CBaseEntityCondition.EConditionType.IS_DIFFERENT);
        assertTrue(negative.isReverse());
        assertTrue(negative.isOpposite());
        assertEquals("isNotFieldReverse(" + OWNER_REFERENCE + ")", render(negative));
    }

    @Test
    @DisplayName("IF <FIELD>H = HIGH-VALUE / <> HIGH-VALUE renders is[Not]FieldHighlightNormal(field); HIGH-VALUES and the HIGH literal byte lower identically")
    void fieldHighlightHighValueRendersIsFieldHighlightNormal()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        for (String highValue : new String[]{"HIGH-VALUE", "HIGH-VALUES", "\u009F"})
        {
            CEntityIsFieldHighlight positive = lowerFieldHighlightCondition(
                factory, highValue, CBaseEntityCondition.EConditionType.IS_EQUAL);
            assertFalse(positive.isBlink());
            assertFalse(positive.isReverse());
            assertFalse(positive.isUnderlined());
            assertFalse(positive.isOpposite());
            assertEquals("isFieldHighlightNormal(" + OWNER_REFERENCE + ")",
                render(positive));
        }

        CEntityIsFieldHighlight negative = lowerFieldHighlightCondition(
            factory, "HIGH-VALUE", CBaseEntityCondition.EConditionType.IS_DIFFERENT);
        assertTrue(negative.isOpposite());
        assertEquals("isNotFieldHighlightNormal(" + OWNER_REFERENCE + ")",
            render(negative));
    }

    @Test
    @DisplayName("an unrecognized highlight code does not lower to a highlight condition (legacy null path)")
    void unrecognizedCodeDoesNotLowerToIsFieldHighlight()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog(), owner());
        // Values outside the 1/2/4/HIGH table never lowered in the legacy pipeline
        // either: GetSpecialCondition returns null, so the caller falls back to a
        // plain comparison (no silent drop into an invalid condition).
        assertNull(fieldHighlight.GetSpecialCondition(
            1, "3", CBaseEntityCondition.EConditionType.IS_EQUAL, factory));
        assertNull(fieldHighlight.GetSpecialCondition(
            1, "5", CBaseEntityCondition.EConditionType.IS_DIFFERENT, factory));
        assertNull(fieldHighlight.GetSpecialCondition(
            1, "", CBaseEntityCondition.EConditionType.IS_EQUAL, factory));
    }

    @Test
    @DisplayName("a comparison other than <> keeps the positive form (legacy only flips on IS_DIFFERENT)")
    void nonDifferentComparisonKeepsPositiveForm()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        // Legacy parity: GetSpecialCondition only calls setOpposite for IS_DIFFERENT;
        // any other comparison type builds the positive condition.
        CEntityIsFieldHighlight condition = lowerFieldHighlightCondition(
            factory, "2", CBaseEntityCondition.EConditionType.IS_GREATER_THAN);
        assertFalse(condition.isOpposite());
        assertEquals("isFieldReverse(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("mode selection follows the legacy Export precedence underlined > blink > reverse > normal")
    void modePrecedenceFollowsLegacyExportOrder()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // Underlined wins over blink (legacy if/else-if order).
        CEntityIsFieldHighlight underlinedAndBlink =
            factory.NewEntityIsFieldHighlight(owner());
        underlinedAndBlink.IsUnderlined();
        underlinedAndBlink.IsBlink();
        assertEquals("isFieldUnderlined(" + OWNER_REFERENCE + ")",
            render(underlinedAndBlink));

        // Blink wins over reverse.
        CEntityIsFieldHighlight blinkAndReverse =
            factory.NewEntityIsFieldHighlight(owner());
        blinkAndReverse.IsBlink();
        blinkAndReverse.IsReverse();
        assertEquals("isFieldBlink(" + OWNER_REFERENCE + ")", render(blinkAndReverse));

        // Reverse wins over the normal fall-through...
        CEntityIsFieldHighlight reverse = factory.NewEntityIsFieldHighlight(owner());
        reverse.IsReverse();
        assertEquals("isFieldReverse(" + OWNER_REFERENCE + ")", render(reverse));

        // ...and no mode flag at all renders the normal condition (IsNormal clears
        // all three flags).
        CEntityIsFieldHighlight normal = factory.NewEntityIsFieldHighlight(owner());
        normal.IsNormal();
        assertEquals("isFieldHighlightNormal(" + OWNER_REFERENCE + ")", render(normal));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldHighlight condition =
            factory.NewEntityIsFieldHighlight(owner());

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldHighlight.class, condition.getClass());
        condition.IsBlink();
        assertEquals("isFieldBlink(" + OWNER_REFERENCE + ")", render(condition));

        condition.setOpposite();
        assertTrue(condition.isOpposite());
        assertEquals("!isFieldBlink(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("the opposite is a flag-flipped pure highlight condition (legacy GetOppositeCondition parity)")
    void oppositeIsAFlagFlippedHighlightCondition()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldHighlight condition = lowerFieldHighlightCondition(
            factory, "4", CBaseEntityCondition.EConditionType.IS_EQUAL);

        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        // A pure semantic highlight condition with the flag flipped — no generate.*
        // coupling — carrying the same mode flags.
        assertEquals(CEntityIsFieldHighlight.class, opposite.getClass());
        CEntityIsFieldHighlight oppositeHighlight = (CEntityIsFieldHighlight) opposite;
        assertTrue(oppositeHighlight.isOpposite());
        assertTrue(oppositeHighlight.isUnderlined());
        assertFalse(oppositeHighlight.isBlink());
        assertEquals("isNotFieldUnderlined(" + OWNER_REFERENCE + ")", render(opposite));
        // The opposite of the opposite restores the positive form.
        assertEquals("isFieldUnderlined(" + OWNER_REFERENCE + ")",
            render(opposite.GetOppositeCondition()));
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // A live reference -> rendered (not ignored).
        CEntityIsFieldHighlight live = factory.NewEntityIsFieldHighlight(owner());
        live.IsReverse();
        assertFalse(live.ignore());

        // An ignored reference -> ignored (legacy reference.ignore() clause).
        CEntityIsFieldHighlight ignored =
            factory.NewEntityIsFieldHighlight(new IgnoredDataEntity());
        ignored.IsReverse();
        assertTrue(ignored.ignore());
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
}
