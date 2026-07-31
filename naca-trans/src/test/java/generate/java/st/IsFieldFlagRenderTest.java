package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityFieldFlag;
import semantic.forms.CEntityIsFieldFlag;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-flag condition ({@code IF <FIELD>P = 1 / 0 / LOW-VALUE})
 * rendering through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaIsFieldFlag} emitted, from
 * its {@code Export}, {@code "is"} + ({@code "Not"} when {@code isopposite}) and then either
 * {@code "FieldFlagSet(<reference>)"} when {@code isisSet} was set or
 * {@code "FieldFlag(<reference>", "<value>"))"} otherwise — i.e. one of four protected
 * {@code OnlineProgram} condition calls: {@code isFieldFlag(Edit, String)},
 * {@code isNotFieldFlag(Edit, String)}, {@code isFieldFlagSet(Edit)},
 * {@code isNotFieldFlagSet(Edit)}. Its {@code GetOppositeCondition} built a flag-flipped copy
 * of itself (not a {@code CEntityCondNot} wrap). This slice de-abstracts the pure semantic
 * entity {@link semantic.forms.CEntityIsFieldFlag} with read-only getters over its data
 * reference ({@code getReference()}), its two branch flags ({@code isSet()}/{@code isOpposite()})
 * and its compared constant ({@code getValue()}), and binds it through the sanctioned BMS
 * forms-island manifest {@code semantic-runtime-bindings.properties} (never the concrete
 * manifest that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to the
 * {@code recursiveIsFieldFlagEntity} template, which reads only {@code entity.*} properties:
 * it branches on {@code <entity.opposite>} to select the {@code is}/{@code isNot} call and on
 * {@code <entity.set>} to select the {@code Set} form vs the value form — preserving all four
 * distinct legacy calls byte-for-byte — and {@code <entity.reference>} is a data reference that
 * unfolds recursively through the assembler. The emitted calls are contracted as operations
 * {@code bms.field.flag} / {@code bms.field.flag.not} / {@code bms.field.flag.set} /
 * {@code bms.field.flag.setNot} (runtime-operations.yaml, template-runtime-requirements.yaml,
 * feature {@code bms.condIsFieldFlag}).
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldFlag.GetSpecialCondition} is the
 * parser-driven construction of this condition ({@code IF <FIELD>P = 1} lowers to a value test
 * around the owner field; {@code IF <FIELD>P = LOW-VALUE} lowers to a set test). It builds the
 * pure entity through the rewired factory ({@code CJavaEntityFactory.NewEntityIsFieldFlag},
 * inherited by {@code CJavaEntityFactoryST}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class IsFieldFlagRenderTest
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
        // semantic.forms.CEntityIsFieldFlag -> recursiveIsFieldFlagEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    private static CJavaEntityFactoryST factory()
    {
        return new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
    }

    /**
     * The production analysis path: {@code IF <FIELD>P = <value>} drives
     * {@link CEntityFieldFlag#GetSpecialCondition(int, String,
     * CBaseEntityCondition.EConditionType, semantic.CBaseEntityFactory)}, which builds the
     * flag condition around the owner field reference.
     */
    private static CEntityIsFieldFlag lower(
        CJavaEntityFactoryST factory, String value,
        CBaseEntityCondition.EConditionType type)
    {
        CEntityFieldFlag fieldFlag =
            new CEntityFieldFlag(1, "WS-FIELDP", catalog(), owner());
        CBaseEntityCondition condition =
            fieldFlag.GetSpecialCondition(1, value, type, factory);
        return assertInstanceOf(CEntityIsFieldFlag.class, condition);
    }

    @Test
    @DisplayName("IF <FIELD>P = 1 renders isFieldFlag(field, \"1\") (legacy Export parity)")
    void flagEqualsOneRendersIsFieldFlag()
    {
        CEntityIsFieldFlag condition = lower(
            factory(), "1", CBaseEntityCondition.EConditionType.IS_EQUAL);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldFlag.class, condition.getClass());
        assertFalse(condition.getReference() == null);
        assertFalse(condition.isOpposite());
        assertFalse(condition.isSet());
        assertEquals("1", condition.getValue());
        assertEquals(7, condition.GetPriorityLevel());
        assertTrue(condition.isBinaryCondition());
        // Byte-for-byte the retired backend's Export: isFieldFlag(<reference>, "1").
        assertEquals("isFieldFlag(" + OWNER_REFERENCE + ", \"1\")", render(condition));
    }

    @Test
    @DisplayName("IF <FIELD>P = 0 renders isFieldFlag(field, \"0\") (legacy Export parity)")
    void flagEqualsZeroRendersIsFieldFlag()
    {
        CEntityIsFieldFlag condition = lower(
            factory(), "0", CBaseEntityCondition.EConditionType.IS_EQUAL);

        assertFalse(condition.isSet());
        assertEquals("0", condition.getValue());
        assertEquals("isFieldFlag(" + OWNER_REFERENCE + ", \"0\")", render(condition));
    }

    @Test
    @DisplayName("IF <FIELD>P <> 1 renders isNotFieldFlag(field, \"1\") (legacy Export parity)")
    void flagNotEqualsOneRendersIsNotFieldFlag()
    {
        // IS_DIFFERENT flips the opposite flag on the value test.
        CEntityIsFieldFlag condition = lower(
            factory(), "1", CBaseEntityCondition.EConditionType.IS_DIFFERENT);

        assertTrue(condition.isOpposite());
        assertFalse(condition.isSet());
        assertEquals("isNotFieldFlag(" + OWNER_REFERENCE + ", \"1\")", render(condition));
    }

    @Test
    @DisplayName("IF <FIELD>P = LOW-VALUE renders isNotFieldFlagSet(field) (legacy Export parity)")
    void flagEqualsLowValueRendersIsNotFieldFlagSet()
    {
        // LOW-VALUE lowers to SetIsFlagSet + SetOpposite: set form, opposite.
        CEntityIsFieldFlag condition = lower(
            factory(), "LOW-VALUE", CBaseEntityCondition.EConditionType.IS_EQUAL);

        assertTrue(condition.isSet());
        assertTrue(condition.isOpposite());
        assertEquals("isNotFieldFlagSet(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("IF <FIELD>P <> LOW-VALUE renders isFieldFlagSet(field) (legacy Export parity)")
    void flagNotEqualsLowValueRendersIsFieldFlagSet()
    {
        // IS_DIFFERENT flips the opposite flag again: set form, positive.
        CEntityIsFieldFlag condition = lower(
            factory(), "LOW-VALUE", CBaseEntityCondition.EConditionType.IS_DIFFERENT);

        assertTrue(condition.isSet());
        assertFalse(condition.isOpposite());
        assertEquals("isFieldFlagSet(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("a test on an unsupported constant does not lower to a flag condition")
    void unsupportedConstantDoesNotLower()
    {
        CEntityFieldFlag fieldFlag =
            new CEntityFieldFlag(1, "WS-FIELDP", catalog(), owner());
        // IF <FIELD>P = X is unsupported (legacy GetSpecialCondition returned null).
        assertNull(fieldFlag.GetSpecialCondition(
            1, "X", CBaseEntityCondition.EConditionType.IS_EQUAL, factory()));
        // A non-equality test type on a supported value also returns null.
        assertNull(fieldFlag.GetSpecialCondition(
            1, "1", CBaseEntityCondition.EConditionType.IS_GREATER_THAN, factory()));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactoryST factory = factory();
        CEntityIsFieldFlag condition = factory.NewEntityIsFieldFlag();

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldFlag.class, condition.getClass());
        condition.SetIsFlag(owner(), "1");
        assertEquals("isFieldFlag(" + OWNER_REFERENCE + ", \"1\")", render(condition));

        CEntityIsFieldFlag setCondition = factory.NewEntityIsFieldFlag();
        setCondition.SetIsFlagSet(owner());
        assertEquals("isFieldFlagSet(" + OWNER_REFERENCE + ")", render(setCondition));
    }

    @Test
    @DisplayName("the opposite is a flag-flipped pure flag condition (legacy GetOppositeCondition parity)")
    void oppositeIsAFlagFlippedFlagCondition()
    {
        CEntityIsFieldFlag condition = lower(
            factory(), "1", CBaseEntityCondition.EConditionType.IS_EQUAL);

        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        // A pure semantic flag condition with the opposite flag flipped — no generate.* coupling.
        assertEquals(CEntityIsFieldFlag.class, opposite.getClass());
        assertTrue(((CEntityIsFieldFlag) opposite).isOpposite());
        assertEquals("isNotFieldFlag(" + OWNER_REFERENCE + ", \"1\")", render(opposite));
        // The opposite of the opposite restores the positive form.
        assertEquals("isFieldFlag(" + OWNER_REFERENCE + ", \"1\")",
            render(opposite.GetOppositeCondition()));
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CJavaEntityFactoryST factory = factory();

        // A live reference -> rendered (not ignored).
        CEntityIsFieldFlag live = factory.NewEntityIsFieldFlag();
        live.SetIsFlag(owner(), "1");
        assertFalse(live.ignore());

        // An ignored reference -> ignored (legacy reference.ignore() clause).
        CEntityIsFieldFlag ignored = factory.NewEntityIsFieldFlag();
        ignored.SetIsFlag(new IgnoredDataEntity(), "1");
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
