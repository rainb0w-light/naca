package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityIsFieldAttribute;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-attribute condition rendering through the recursive
 * assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaIsFieldAttribute}
 * emitted, from its {@code Export}, either the single protected
 * {@code OnlineProgram.isFieldAttribute(Edit, VarAndEdit)} call (when a compared
 * value was present) or up to three mutually-exclusive attribute terms —
 * group1 autoSkip&gt;protected&gt;numeric&gt;unprotected, group2 bright&gt;dark,
 * group3 modified&gt;unmodified&gt;cleared — each as {@code is[Not]Field<Kind>(<reference>)},
 * joined by {@code " && "} ({@code " || "} when opposite) and parenthesized when two
 * or three groups are present. This slice de-abstracts the pure semantic entity
 * {@link semantic.forms.CEntityIsFieldAttribute} (moving {@code GetPriorityLevel}
 * and {@code GetOppositeCondition} up from the retired backend) with read-only
 * getters over its reference, compared value, flags and the derived grouping /
 * bracketing, and binds it through the sanctioned BMS forms-island manifest
 * {@code semantic-runtime-bindings.properties} to the
 * {@code recursiveIsFieldAttributeEntity} template, which reads only
 * {@code entity.*} properties: {@code <entity.reference>} and {@code <entity.varValue>}
 * are data references that unfold recursively through the assembler.
 *
 * <p>Production lowering: the factory {@code CJavaEntityFactory.NewEntityIsFieldAttribute}
 * (inherited by {@code CJavaEntityFactoryST}) now builds the pure entity through
 * {@code BmsJavaEntities.isFieldAttribute()}. The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 *
 * <p>Pre-existing latent debt (NOT introduced by this slice): the legacy backend's
 * {@code protected}, opposite-value and opposite-unmodified branches emit
 * {@code isFieldProtected}/{@code isNotFieldProtected}/{@code isNotFieldAttribute}/
 * {@code isNotFieldUnmodified}, which have no {@code OnlineProgram} implementation.
 * This condition entity is unreachable dead code (no parser path constructs it), so
 * those branches never compiled in the legacy output either; they are reproduced here
 * byte-for-byte for parity (see the assertions below) but are deliberately not declared
 * in the runtime contract.
 */
class IsFieldAttributeRenderTest
{
    private static final String FIELD_REFERENCE = "MAP.WS-FIELD";
    private static final String VALUE_REFERENCE = "MAP.WS-VAR";

    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static MockDataEntity field()
    {
        return new MockDataEntity(1, FIELD_REFERENCE);
    }

    private static MockDataEntity value()
    {
        return new MockDataEntity(1, VALUE_REFERENCE);
    }

    private static CJavaEntityFactoryST factory()
    {
        return new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
    }

    private static String render(CBaseEntityCondition condition)
    {
        // Conditions unfold through the assembler exactly as recursiveIfEntity renders its
        // condition: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityIsFieldAttribute -> recursiveIsFieldAttributeEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CEntityIsFieldAttribute condition = factory().NewEntityIsFieldAttribute();
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityIsFieldAttribute.class, condition.getClass());
    }

    @Test
    @DisplayName("a single attribute renders isField<Kind>(field) at priority 7 (legacy Export parity)")
    void singleAttributeRendersBareCall()
    {
        CEntityIsFieldAttribute condition = factory().NewEntityIsFieldAttribute();
        condition.SetVariable(field());
        condition.IsAutoSkip();

        assertEquals(7, condition.GetPriorityLevel());
        assertTrue(condition.isBinaryCondition());
        assertFalse(condition.ignore());
        assertEquals("isFieldAutoSkip(" + FIELD_REFERENCE + ")", render(condition));
    }

    @Test
    @DisplayName("each group-1 attribute kind renders its own legacy call")
    void group1KindsRenderTheirCalls()
    {
        // numeric
        CEntityIsFieldAttribute numeric = factory().NewEntityIsFieldAttribute();
        numeric.SetVariable(field());
        numeric.IsNumeric();
        assertEquals("isFieldNumeric(" + FIELD_REFERENCE + ")", render(numeric));

        // unprotected
        CEntityIsFieldAttribute unprotected = factory().NewEntityIsFieldAttribute();
        unprotected.SetVariable(field());
        unprotected.IsUnprotected();
        assertEquals("isFieldUnprotected(" + FIELD_REFERENCE + ")", render(unprotected));

        // protected — legacy emitted isFieldProtected, which has NO OnlineProgram
        // implementation (pre-existing latent debt); preserved for byte parity.
        CEntityIsFieldAttribute prot = factory().NewEntityIsFieldAttribute();
        prot.SetVariable(field());
        prot.IsProtected();
        assertEquals("isFieldProtected(" + FIELD_REFERENCE + ")", render(prot));
    }

    @Test
    @DisplayName("a compared value renders isFieldAttribute(field, value) (legacy Export parity)")
    void comparedValueRendersFieldAttribute()
    {
        CEntityIsFieldAttribute condition = factory().NewEntityIsFieldAttribute();
        // IsAttribute(data, var): varValue = data, reference = var.
        condition.IsAttribute(value(), field());

        assertEquals(7, condition.GetPriorityLevel());
        assertEquals("isFieldAttribute(" + FIELD_REFERENCE + ", " + VALUE_REFERENCE + ")",
            render(condition));
    }

    @Test
    @DisplayName("two attribute groups join with && and are parenthesized (legacy Export parity)")
    void twoGroupsJoinAndBracket()
    {
        CEntityIsFieldAttribute condition = factory().NewEntityIsFieldAttribute();
        condition.SetVariable(field());
        condition.IsBright();
        condition.IsCleared();

        assertEquals(1, condition.GetPriorityLevel());
        assertEquals("(isFieldBright(" + FIELD_REFERENCE + ") && isFieldCleared("
            + FIELD_REFERENCE + "))", render(condition));
    }

    @Test
    @DisplayName("three attribute groups render the full && chain, parenthesized (legacy Export parity)")
    void threeGroupsRenderFullChain()
    {
        CEntityIsFieldAttribute condition = factory().NewEntityIsFieldAttribute();
        condition.SetVariable(field());
        condition.IsProtected();
        condition.IsBright();
        condition.IsModified();

        assertEquals(1, condition.GetPriorityLevel());
        assertEquals("(isFieldProtected(" + FIELD_REFERENCE + ") && isFieldBright("
            + FIELD_REFERENCE + ") && isFieldModified(" + FIELD_REFERENCE + "))",
            render(condition));
    }

    @Test
    @DisplayName("the opposite flips is/Not and joins with || (legacy GetOppositeCondition parity)")
    void oppositeFlipsPolarityAndJoin()
    {
        CEntityIsFieldAttribute condition = factory().NewEntityIsFieldAttribute();
        condition.SetVariable(field());
        condition.IsProtected();
        condition.IsBright();
        condition.IsModified();

        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        assertEquals(CEntityIsFieldAttribute.class, opposite.getClass());
        assertEquals("(isNotFieldProtected(" + FIELD_REFERENCE + ") || isNotFieldBright("
            + FIELD_REFERENCE + ") || isNotFieldModified(" + FIELD_REFERENCE + "))",
            render(opposite));
    }

    @Test
    @DisplayName("the opposite of a single attribute renders isNotField<Kind>(field)")
    void oppositeSingleAttribute()
    {
        CEntityIsFieldAttribute condition = factory().NewEntityIsFieldAttribute();
        condition.SetVariable(field());
        condition.IsAutoSkip();

        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        assertEquals("isNotFieldAutoSkip(" + FIELD_REFERENCE + ")", render(opposite));
    }
}
