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
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityFieldLength;
import semantic.forms.CEntityIsFieldCursor;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-cursor condition ({@code IF <FIELD>-L = -1} / {@code <> -1})
 * rendering through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaIsFieldCursor} emitted,
 * from its {@code Export}, one of two protected {@code OnlineProgram} condition calls
 * selected by its {@code ishasCursor} flag: {@code isFieldHasCursor(<reference>)} when
 * the flag was set and {@code isNotFieldHasCursor(<reference>)} otherwise. Its
 * {@code GetOppositeCondition} built a flag-flipped copy of itself (not a
 * {@code CEntityCondNot} wrap). This slice de-abstracts the pure semantic entity
 * {@link semantic.forms.CEntityIsFieldCursor} with read-only getters over its data
 * reference ({@code getReference()}) and its negation flag ({@code isOpposite()}) and
 * binds it through the sanctioned BMS forms-island manifest
 * {@code semantic-runtime-bindings.properties} (never the concrete manifest that
 * {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to the
 * {@code recursiveIsFieldCursorEntity} template, which reads only {@code entity.*}
 * properties: it branches on {@code <entity.opposite>} to select the {@code is}/{@code isNot}
 * runtime call — preserving the two distinct legacy calls byte-for-byte — and
 * {@code <entity.reference>} is a data reference that unfolds recursively through the
 * assembler. The emitted calls are contracted as operations {@code bms.field.cursor.has}
 * and {@code bms.field.cursor.hasNot} (runtime-operations.yaml,
 * template-runtime-requirements.yaml, feature {@code bms.condIsFieldCursor}).
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldLength.GetSpecialCondition}
 * is the parser-driven construction of this condition ({@code IF <FIELD>-L = -1} lowers
 * to a cursor test around the owner field); {@code IS_EQUAL} builds {@code SetHasCursor}
 * (renders {@code isFieldHasCursor}), any other test on {@code -1} builds
 * {@code SetHasNotCursor} (renders {@code isNotFieldHasCursor}). It builds the pure
 * entity through the rewired factory ({@code CJavaEntityFactory.NewEntityIsFieldCursor},
 * inherited by {@code CJavaEntityFactory}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class IsFieldCursorRenderTest
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
        // semantic.forms.CEntityIsFieldCursor -> recursiveIsFieldCursorEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production analysis path: {@code IF <FIELD>-L = -1} drives
     * {@link CEntityFieldLength#GetSpecialCondition(int, String,
     * CBaseEntityCondition.EConditionType, semantic.CBaseEntityFactory)}, which builds
     * the cursor condition around the owner field reference (IS_EQUAL -> SetHasCursor).
     */
    private static CEntityIsFieldCursor lowerFieldLengthEqualsMinusOne(
        CJavaEntityFactory factory)
    {
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        CBaseEntityCondition condition = fieldLength.GetSpecialCondition(
            1, "-1", CBaseEntityCondition.EConditionType.IS_EQUAL, factory);
        return assertInstanceOf(CEntityIsFieldCursor.class, condition);
    }

    @Test
    @DisplayName("IF <FIELD>-L = -1 renders isFieldHasCursor(field) (legacy Export parity)")
    void fieldLengthEqualsMinusOneRendersIsFieldHasCursor()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldCursor condition = lowerFieldLengthEqualsMinusOne(factory);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldCursor.class, condition.getClass());
        // The reference slot is populated (the production owner field).
        assertFalse(condition.getReference() == null);
        assertFalse(condition.isOpposite());
        assertEquals(7, condition.GetPriorityLevel());
        assertTrue(condition.isBinaryCondition());
        // Byte-for-byte the retired backend's Export: isFieldHasCursor(<reference>).
        assertEquals("isFieldHasCursor(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("IF <FIELD>-L <> -1 renders isNotFieldHasCursor(field) (legacy Export parity)")
    void fieldLengthNotEqualsMinusOneRendersIsNotFieldHasCursor()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        // Any non-IS_EQUAL test on -1 lowers through SetHasNotCursor.
        CBaseEntityCondition condition = fieldLength.GetSpecialCondition(
            1, "-1", CBaseEntityCondition.EConditionType.IS_DIFFERENT, factory);
        CEntityIsFieldCursor cursor =
            assertInstanceOf(CEntityIsFieldCursor.class, condition);

        assertTrue(cursor.isOpposite());
        // Byte-for-byte the retired backend's Export: isNotFieldHasCursor(<reference>).
        assertEquals("isNotFieldHasCursor(" + OWNER_REFERENCE + ")", render(cursor));
    }

    @Test
    @DisplayName("a test on a value other than -1 does not lower to a cursor condition")
    void nonMinusOneDoesNotLowerToIsFieldCursor()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        // IF <FIELD>-L = 0 is the field-modified test, never a cursor test (legacy
        // GetSpecialCondition behavior preserved).
        CBaseEntityCondition condition = fieldLength.GetSpecialCondition(
            1, "0", CBaseEntityCondition.EConditionType.IS_GREATER_THAN, factory);
        assertFalse(condition instanceof CEntityIsFieldCursor);
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldCursor condition = factory.NewEntityIsFieldCursor();

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldCursor.class, condition.getClass());
        condition.SetHasCursor(owner());
        assertEquals("isFieldHasCursor(" + OWNER_REFERENCE + ")", render(condition));

        CEntityIsFieldCursor notCondition = factory.NewEntityIsFieldCursor();
        notCondition.SetHasNotCursor(owner());
        assertEquals("isNotFieldHasCursor(" + OWNER_REFERENCE + ")", render(notCondition));
    }

    @Test
    @DisplayName("the opposite is a flag-flipped pure cursor condition (legacy GetOppositeCondition parity)")
    void oppositeIsAFlagFlippedCursorCondition()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityIsFieldCursor condition = lowerFieldLengthEqualsMinusOne(factory);

        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        // A pure semantic cursor condition with the flag flipped — no generate.* coupling.
        assertEquals(CEntityIsFieldCursor.class, opposite.getClass());
        assertTrue(((CEntityIsFieldCursor) opposite).isOpposite());
        assertEquals("isNotFieldHasCursor(" + OWNER_REFERENCE + ")", render(opposite));
        // The opposite of the opposite restores the positive form.
        assertEquals("isFieldHasCursor(" + OWNER_REFERENCE + ")",
            render(opposite.GetOppositeCondition()));
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // A live reference -> rendered (not ignored).
        CEntityIsFieldCursor live = factory.NewEntityIsFieldCursor();
        live.SetHasCursor(owner());
        assertFalse(live.ignore());

        // An ignored reference -> ignored (legacy reference.ignore() clause).
        CEntityIsFieldCursor ignored = factory.NewEntityIsFieldCursor();
        ignored.SetHasCursor(new IgnoredDataEntity());
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
