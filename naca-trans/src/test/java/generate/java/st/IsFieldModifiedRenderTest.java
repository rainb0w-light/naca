package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondNot;
import semantic.forms.CEntityFieldLength;
import semantic.forms.CEntityIsFieldModified;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-modified condition ({@code IF <FIELD>-L > 0}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaIsFieldModified}
 * emitted, from its {@code Export}, the single protected {@code OnlineProgram}
 * condition call {@code isFieldModified(<reference>)}; its {@code GetOppositeCondition}
 * wrapped the condition in a {@code generate.bmsjava.CBmsJavaCondNot} rendering
 * {@code !isFieldModified(<reference>)}. This slice de-abstracts the pure semantic
 * entity {@link semantic.forms.CEntityIsFieldModified} with a read-only getter over
 * its data reference ({@code getReference()}) and binds it through the sanctioned BMS
 * forms-island manifest {@code semantic-runtime-bindings.properties} (never the
 * concrete manifest that {@code FinalArchitectureContractTest} pins to the
 * COBOL/SQL/CICS tree) to the {@code recursiveIsFieldModifiedEntity} template, which
 * reads only {@code entity.*} properties: {@code <entity.reference>} is a data
 * reference that unfolds recursively through the assembler. The entity SHADOWS the
 * inherited {@code CUnitaryEntityCondition.reference} slot ({@code SetIsModified}
 * writes the subclass field), so the template reads {@code getReference()} — not the
 * inherited {@code GetConditionReference()}, which would return the unset superclass
 * slot. The emitted call is contracted as operation {@code bms.field.modified}
 * (runtime-operations.yaml, template-runtime-requirements.yaml, feature
 * {@code bms.condIsFieldModified}).
 *
 * <p>Negation no longer couples the semantic tree to {@code generate.bmsjava}:
 * {@code GetOppositeCondition} now returns a pure {@link semantic.expression.CEntityCondNot}
 * (rendered {@code !(isFieldModified(<reference>))} by {@code recursiveCondNotEntity}).
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldLength.GetSpecialCondition}
 * is the parser-driven construction of this condition ({@code IF <FIELD>-L > 0} lowers
 * to a field-modified test around the owner field); it builds the pure entity through
 * the rewired factory ({@code CJavaEntityFactory.NewEntityIsFieldModified}, inherited
 * by {@code CJavaEntityFactoryST}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class IsFieldModifiedRenderTest
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
        // semantic.forms.CEntityIsFieldModified -> recursiveIsFieldModifiedEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production analysis path: {@code IF <FIELD>-L > 0} drives
     * {@link CEntityFieldLength#GetSpecialCondition(int, String,
     * CBaseEntityCondition.EConditionType, semantic.CBaseEntityFactory)}, which builds
     * the field-modified condition around the owner field reference.
     */
    private static CEntityIsFieldModified lowerFieldLengthGreaterThanZero(
        CJavaEntityFactoryST factory)
    {
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        CBaseEntityCondition condition = fieldLength.GetSpecialCondition(
            1, "0", CBaseEntityCondition.EConditionType.IS_GREATER_THAN, factory);
        return assertInstanceOf(CEntityIsFieldModified.class, condition);
    }

    @Test
    @DisplayName("IF <FIELD>-L > 0 renders isFieldModified(field) (legacy Export parity)")
    void fieldLengthGreaterThanZeroRendersIsFieldModified()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityIsFieldModified condition = lowerFieldLengthGreaterThanZero(factory);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldModified.class, condition.getClass());
        // The reference slot is populated (the production owner field).
        assertFalse(condition.getReference() == null);
        assertEquals(7, condition.GetPriorityLevel());
        assertTrue(condition.isBinaryCondition());
        // Byte-for-byte the retired backend's Export: isFieldModified(<reference>).
        assertEquals("isFieldModified(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("ZERO/ZEROS/ZEROES spellings all lower to the field-modified condition")
    void zeroSpellingsLowerToIsFieldModified()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        for (String zero : new String[] { "0", "ZERO", "ZEROS", "ZEROES" })
        {
            CEntityFieldLength fieldLength =
                new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
            CBaseEntityCondition condition = fieldLength.GetSpecialCondition(
                1, zero, CBaseEntityCondition.EConditionType.IS_GREATER_THAN, factory);
            CEntityIsFieldModified modified =
                assertInstanceOf(CEntityIsFieldModified.class, condition);
            assertEquals("isFieldModified(" + OWNER_REFERENCE + ")", render(modified),
                "lowering for > " + zero);
        }
    }

    @Test
    @DisplayName("a non-greater-than test does not lower to a field-modified condition")
    void nonGreaterThanDoesNotLowerToIsFieldModified()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        // IF <FIELD>-L = 0 is an equality test, never a field-modified test (legacy
        // GetSpecialCondition behavior preserved).
        CBaseEntityCondition condition = fieldLength.GetSpecialCondition(
            1, "0", CBaseEntityCondition.EConditionType.IS_EQUAL, factory);
        assertFalse(condition instanceof CEntityIsFieldModified);
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityIsFieldModified condition = factory.NewEntityIsFieldModified();

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldModified.class, condition.getClass());
        condition.SetIsModified(owner());
        assertEquals("isFieldModified(" + OWNER_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("the opposite is a pure CEntityCondNot rendering !(isFieldModified(field))")
    void oppositeIsAPureCondNot()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityIsFieldModified condition = lowerFieldLengthGreaterThanZero(factory);

        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        // A pure semantic CEntityCondNot — no generate.bmsjava.CBmsJavaCondNot coupling.
        assertEquals(CEntityCondNot.class, opposite.getClass());
        assertSame(condition, opposite.GetOppositeCondition());
        assertEquals("!(isFieldModified(" + OWNER_REFERENCE + "))", render(opposite));
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        // A live reference -> rendered (not ignored).
        CEntityIsFieldModified live = factory.NewEntityIsFieldModified();
        live.SetIsModified(owner());
        assertFalse(live.ignore());

        // An ignored reference -> ignored (legacy reference.ignore() clause).
        CEntityIsFieldModified ignored = factory.NewEntityIsFieldModified();
        ignored.SetIsModified(new IgnoredDataEntity());
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
