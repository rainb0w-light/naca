package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityFieldColor;
import semantic.forms.CEntityIsFieldColor;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;
import utils.NacaTransAssertException;

/**
 * BMS map-resource field-color condition ({@code IF <FIELD>C = <color-code>} /
 * {@code <> <color-code>}) rendering through the recursive assembler (the
 * production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaIsFieldColor} emitted,
 * from its {@code Export}, one of two protected {@code OnlineProgram} condition calls
 * selected by its {@code isopposite} flag:
 * {@code isFieldColored(<reference>, MapFieldAttrColor.<color>)} when the flag was
 * clear and {@code isNotFieldColored(<reference>, MapFieldAttrColor.<color>)} when it
 * was set. Its {@code GetOppositeCondition} built a flag-flipped copy of itself (not a
 * {@code CEntityCondNot} wrap). This slice de-abstracts the pure semantic entity
 * {@link semantic.forms.CEntityIsFieldColor} with read-only getters over its data
 * reference ({@code getReference()}), its negation flag ({@code isOpposite()}) and the
 * compared color folded into the static {@code nacaLib.mapSupport.MapFieldAttrColor}
 * field reference ({@code getColorConstant()}), and binds it through the sanctioned
 * BMS forms-island manifest {@code semantic-runtime-bindings.properties} (never the
 * concrete manifest that {@code FinalArchitectureContractTest} pins to the
 * COBOL/SQL/CICS tree) to the {@code recursiveIsFieldColorEntity} template, which
 * reads only {@code entity.*} properties: it branches on {@code <entity.opposite>} to
 * select the {@code is}/{@code isNot} runtime call — preserving the two distinct
 * legacy calls byte-for-byte — and {@code <entity.reference>} is a data reference that
 * unfolds recursively through the assembler. The emitted calls are contracted as
 * operations {@code bms.field.color.is} and {@code bms.field.color.isNot}
 * (runtime-operations.yaml, template-runtime-requirements.yaml, feature
 * {@code bms.condIsFieldColor}).
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldColor.GetSpecialCondition}
 * is the parser-driven construction of this condition ({@code IF <FIELD>C = 2} lowers
 * to a color test around the owner field); {@code IS_EQUAL} keeps the positive flag
 * (renders {@code isFieldColored}), {@code IS_DIFFERENT} lowers through
 * {@code SetOpposite} (renders {@code isNotFieldColored}), and a non-color literal
 * never lowers (the legacy {@code CFieldColor.WhichColor} returns null, so the
 * condition is null and the caller falls back to a plain comparison). It builds the
 * pure entity through the rewired factory
 * ({@code CJavaEntityFactory.NewEntityIsFieldColor}, inherited by
 * {@code CJavaEntityFactory}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class IsFieldColorRenderTest
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
        // semantic.forms.CEntityIsFieldColor -> recursiveIsFieldColorEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production analysis path: {@code IF <FIELD>C = <colorCode>} drives
     * {@link CEntityFieldColor#GetSpecialCondition(int, String,
     * CBaseEntityCondition.EConditionType, semantic.CBaseEntityFactory)}, which builds
     * the color condition around the owner field reference (IS_EQUAL -> positive,
     * IS_DIFFERENT -> SetOpposite).
     */
    private static CEntityIsFieldColor lowerFieldColorCondition(
        CJavaEntityFactory factory, String colorCode,
        CBaseEntityCondition.EConditionType type)
    {
        CEntityFieldColor fieldColor =
            new CEntityFieldColor(1, "WS-FIELD-C", catalog(), owner());
        CBaseEntityCondition condition =
            fieldColor.GetSpecialCondition(1, colorCode, type, factory);
        return assertInstanceOf(CEntityIsFieldColor.class, condition);
    }

    @Test
    @DisplayName("IF <FIELD>C = 2 renders isFieldColored(field, MapFieldAttrColor.RED) (legacy Export parity)")
    void fieldColorEqualsRendersIsFieldColored()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldColor condition = lowerFieldColorCondition(
            factory, "2", CBaseEntityCondition.EConditionType.IS_EQUAL);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldColor.class, condition.getClass());
        // The reference slot is populated (the production owner field).
        assertFalse(condition.getReference() == null);
        assertFalse(condition.isOpposite());
        assertEquals(7, condition.GetPriorityLevel());
        assertTrue(condition.isBinaryCondition());
        // Byte-for-byte the retired backend's Export:
        // isFieldColored(<reference>, MapFieldAttrColor.<color>).
        assertEquals("isFieldColored(" + OWNER_REFERENCE + ", MapFieldAttrColor.RED)",
            render(condition));
    }

    @Test
    @DisplayName("IF <FIELD>C <> 2 renders isNotFieldColored(field, MapFieldAttrColor.RED) (legacy Export parity)")
    void fieldColorDifferentRendersIsNotFieldColored()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        // IS_DIFFERENT lowers through SetOpposite.
        CEntityIsFieldColor condition = lowerFieldColorCondition(
            factory, "2", CBaseEntityCondition.EConditionType.IS_DIFFERENT);

        assertTrue(condition.isOpposite());
        // Byte-for-byte the retired backend's Export:
        // isNotFieldColored(<reference>, MapFieldAttrColor.<color>).
        assertEquals("isNotFieldColored(" + OWNER_REFERENCE + ", MapFieldAttrColor.RED)",
            render(condition));
    }

    @Test
    @DisplayName("all seven BMS color codes map to the legacy MapFieldAttrColor names")
    void allSevenColorCodesRenderTheirMapFieldAttrColorName()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        // Legacy CEntityFieldColor.CFieldColor.WhichColor digit -> name table.
        String[][] codes = {
            {"1", "BLUE"}, {"2", "RED"}, {"3", "PINK"}, {"4", "GREEN"},
            {"5", "TURQUOISE"}, {"6", "YELLOW"}, {"7", "NEUTRAL"},
        };
        for (String[] code : codes)
        {
            CEntityIsFieldColor condition = lowerFieldColorCondition(
                factory, code[0], CBaseEntityCondition.EConditionType.IS_EQUAL);
            assertEquals("MapFieldAttrColor." + code[1], condition.getColorConstant());
            assertEquals("isFieldColored(" + OWNER_REFERENCE
                + ", MapFieldAttrColor." + code[1] + ")", render(condition));
        }
    }

    @Test
    @DisplayName("a non-color literal does not lower to a color condition (legacy WhichColor null path)")
    void nonColorLiteralDoesNotLowerToIsFieldColor()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityFieldColor fieldColor =
            new CEntityFieldColor(1, "WS-FIELD-C", catalog(), owner());
        // Values outside the 1..7 color-code table never lowered in the legacy
        // pipeline either: WhichColor returns null, so GetSpecialCondition returns
        // null and the caller falls back to a plain comparison (no silent drop into
        // an invalid condition).
        assertNull(fieldColor.GetSpecialCondition(
            1, "9", CBaseEntityCondition.EConditionType.IS_EQUAL, factory));
        assertNull(fieldColor.GetSpecialCondition(
            1, "", CBaseEntityCondition.EConditionType.IS_EQUAL, factory));
    }

    @Test
    @DisplayName("an unsupported comparison type on a color code is rejected, not silently lowered")
    void unsupportedComparisonTypeIsRejected()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityFieldColor fieldColor =
            new CEntityFieldColor(1, "WS-FIELD-C", catalog(), owner());
        // Legacy behavior preserved: only IS_EQUAL / IS_DIFFERENT lower to a color
        // condition; anything else throws (the legacy NacaTransAssertException).
        assertThrows(NacaTransAssertException.class, () -> fieldColor.GetSpecialCondition(
            1, "2", CBaseEntityCondition.EConditionType.IS_GREATER_THAN, factory));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldColor condition = factory.NewEntityIsFieldColor();

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldColor.class, condition.getClass());
        condition.IsColor(CEntityFieldColor.CFieldColor.GREEN, owner());
        assertEquals("isFieldColored(" + OWNER_REFERENCE + ", MapFieldAttrColor.GREEN)",
            render(condition));

        condition.SetOpposite();
        assertTrue(condition.isOpposite());
        assertEquals("isNotFieldColored(" + OWNER_REFERENCE + ", MapFieldAttrColor.GREEN)",
            render(condition));
    }

    @Test
    @DisplayName("the opposite is a flag-flipped pure color condition (legacy GetOppositeCondition parity)")
    void oppositeIsAFlagFlippedColorCondition()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldColor condition = lowerFieldColorCondition(
            factory, "2", CBaseEntityCondition.EConditionType.IS_EQUAL);

        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        // A pure semantic color condition with the flag flipped — no generate.* coupling.
        assertEquals(CEntityIsFieldColor.class, opposite.getClass());
        assertTrue(((CEntityIsFieldColor) opposite).isOpposite());
        assertEquals("isNotFieldColored(" + OWNER_REFERENCE + ", MapFieldAttrColor.RED)",
            render(opposite));
        // The opposite of the opposite restores the positive form.
        assertEquals("isFieldColored(" + OWNER_REFERENCE + ", MapFieldAttrColor.RED)",
            render(opposite.GetOppositeCondition()));
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // A live reference -> rendered (not ignored).
        CEntityIsFieldColor live = factory.NewEntityIsFieldColor();
        live.IsColor(CEntityFieldColor.CFieldColor.BLUE, owner());
        assertFalse(live.ignore());

        // An ignored reference -> ignored (legacy reference.ignore() clause).
        CEntityIsFieldColor ignored = factory.NewEntityIsFieldColor();
        ignored.IsColor(CEntityFieldColor.CFieldColor.BLUE, new IgnoredDataEntity());
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
