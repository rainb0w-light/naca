package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.LegacyDataRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.CEntityNoAction;
import semantic.Verbs.CEntitySetConstant;
import semantic.forms.CEntityFormAccessor;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaFormAccessor}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityFormAccessor} (BMS is a CICS screen-map DSL, never a COBOL dialect).
 *
 * <p>The retired backend was <b>dead wiring</b>: factory-wired in name, but no production path
 * ever constructed one — the transcoder's {@code NewEntityFormAccessor} factory method is
 * commented out, so no COBOL/BMS/FPac tree holds the entity today. Its only live output protocol
 * merely delegated: {@code ExportReference(nLine) == LegacyDataRenderer.renderReference(owner,
 * getLine())} — exactly the owning form's data reference. This slice binds the pure entity through
 * the sanctioned BMS forms-island manifest {@code semantic-runtime-bindings.properties} (never the
 * concrete manifest that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to
 * the {@code recursiveFormAccessorEntity} template, which reads only {@code entity.formReference}
 * — a pure getter delegating to the owning form's own precomputed reference (the owner renders
 * through {@code recursiveFormEntity}) — byte-for-byte the retired backend's ExportReference.
 *
 * <p>The slice also fixes the latent constructor self-assignment the backend carried
 * ({@code owner = owner} — the parameter shadowed the field, so {@code GetForm()} and every
 * owner-delegating protocol saw {@code null}); the pure entity assigns {@code this.owner = owner}.
 *
 * <p>Because the entity is dead wiring there is no production construction path to drive; the
 * reference consumption protocol ({@code LegacyDataRenderer.renderReference} fall-through, exactly
 * what the retired backend itself called on its owner) and the entity's parser-facing lowering
 * ({@code GetSpecialAssignment} through the production {@code CJavaEntityFactoryST}) are exercised
 * below instead.
 */
class FormAccessorRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntityFormAccessor entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityFormAccessor -> recursiveFormAccessorEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("accessor reference renders the owning form's reference through the recursive assembler")
    void referenceRendersOwnerFormReferenceThroughRecursiveAssembler()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // Production construction of the owning screen-map form (BMS .bms MAP path).
        CEntityResourceForm form = factory.NewEntityForm(1, "MY-MAP", false);
        CEntityFormAccessor accessor = new CEntityFormAccessor(1, "MY-MAP", catalog(), form);

        // Byte-for-byte the retired backend's ExportReference == renderReference(owner, getLine()):
        // the accessor reference IS the owning form's reference.
        assertEquals("MY_MAP", render(accessor));
        assertEquals(LegacyDataRenderer.renderReference(form, 1), render(accessor));
        assertEquals(form.getFormReference(), accessor.getFormReference());
    }

    @Test
    @DisplayName("accessor reference qualifies the container when the parser set the owner's of qualifier")
    void referenceQualifiesContainerQualifier()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityResourceForm form = factory.NewEntityForm(1, "MY-MAP", false);
        CEntityResourceFormContainer container =
            factory.NewEntityFormContainer(1, "MY-SET", false);
        // CMapElement assigns ef.of = container in production; the accessor delegates to the
        // owner's reference, which the owner qualifies with the target-formatted container name.
        form.of = container;
        CEntityFormAccessor accessor = new CEntityFormAccessor(1, "MY-MAP", catalog(), form);

        assertEquals("MY_SET.MY_MAP", render(accessor));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler binding")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityResourceForm form = factory.NewEntityForm(1, "MY-MAP", false);
        CEntityFormAccessor accessor = new CEntityFormAccessor(1, "MY-MAP", catalog(), form);

        // The exact production consumption protocol: with the backend's reflective
        // ExportReference gone, the semantic-declared path returns null and falls through to
        // the recursive assembler binding (recursiveFormAccessorEntity).
        assertEquals("MY_MAP", LegacyDataRenderer.renderReference(accessor, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("an accessor without an owner renders the legacy [UNDEFINED] sentinel")
    void ownerlessAccessorRendersLegacyUndefinedSentinel()
    {
        CEntityFormAccessor accessor = new CEntityFormAccessor(1, "ACC", catalog(), null);

        // Byte-for-byte the retired backend: ExportReference -> renderReference(null, line)
        // == "[UNDEFINED]". Both the direct protocol and the assembler binding agree.
        assertNull(accessor.GetForm());
        assertEquals("[UNDEFINED]", accessor.ExportReference(0));
        assertEquals("[UNDEFINED]", render(accessor));
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(accessor, 0));
    }

    @Test
    @DisplayName("Clear() drops the owner and the reference falls back to [UNDEFINED]")
    void clearedAccessorRendersLegacyUndefinedSentinel()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityResourceForm form = factory.NewEntityForm(1, "MY-MAP", false);
        CEntityFormAccessor accessor = new CEntityFormAccessor(1, "MY-MAP", catalog(), form);
        assertEquals("MY_MAP", render(accessor));

        accessor.Clear();

        assertNull(accessor.GetForm());
        assertEquals("[UNDEFINED]", render(accessor));
    }

    @Test
    @DisplayName("GetForm() returns the owner (fixes the retired backend's self-assignment no-op)")
    void getFormReturnsOwnerFixesLegacySelfAssignment()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityResourceForm form = factory.NewEntityForm(1, "MY-MAP", false);
        CEntityFormAccessor accessor = new CEntityFormAccessor(1, "MY-MAP", catalog(), form);

        // The legacy constructor's `owner = owner` assigned the parameter to itself (the field
        // stayed null), so GetForm() and every owner-delegating protocol saw null. The pure
        // entity assigns this.owner = owner; the delegation is now well-formed.
        assertSame(form, accessor.GetForm());
        assertSame(form, accessor.getReference());
        // The save-copy protocol delegates to the owner (null until a copy is made).
        assertSame(form.getSaveCopy(), accessor.getSaveCopy());
    }

    @Test
    @DisplayName("parser-facing special assignments lower through the production factory")
    void specialAssignmentLowersThroughProductionFactory()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityResourceForm form = factory.NewEntityForm(1, "MY-MAP", false);
        CEntityFormAccessor accessor = new CEntityFormAccessor(1, "MY-MAP", catalog(), form);

        // MOVE SPACE TO <form>: the accessor lowers the figurative constant to a
        // CEntitySetConstant set-to-space registered as a writing action on the owner form.
        CEntitySetConstant setSpace = assertInstanceOf(CEntitySetConstant.class,
            accessor.GetSpecialAssignment(new KeywordTerminal("SPACE"), factory, 1));
        assertTrue(setSpace.isSetToSpace());
        CEntitySetConstant setZero = assertInstanceOf(CEntitySetConstant.class,
            accessor.GetSpecialAssignment(new KeywordTerminal("ZEROS"), factory, 1));
        assertTrue(setZero.isSetToZero());
        // An unrecognized keyword is not silently dropped into a bad assignment: null signals
        // the caller to take the regular assignment path.
        assertNull(accessor.GetSpecialAssignment(new KeywordTerminal("SOMETHING-ELSE"), factory, 1));

        // MOVE <other-form> TO <form>: a map copy, lowered to a CEntityNoAction registered in
        // the program catalog (the owner is not a save copy).
        CEntityResourceForm other = factory.NewEntityForm(2, "OTHER-MAP", false);
        assertInstanceOf(CEntityNoAction.class,
            accessor.GetSpecialAssignment(other, factory, 1));
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityResourceForm form = factory.NewEntityForm(1, "MY-MAP", false);
        CEntityFormAccessor accessor = new CEntityFormAccessor(1, "MY-MAP", catalog(), form);

        // A form accessor: FORM data type (VIRTUAL_FORM once setVirtual()), never a declared
        // val, bears accessors iff the owner does (a form bears none), never ignored, and not
        // a literal constant.
        assertEquals(CDataEntity.CDataEntityType.FORM, accessor.GetDataType());
        accessor.setVirtual();
        assertEquals(CDataEntity.CDataEntityType.VIRTUAL_FORM, accessor.GetDataType());
        assertFalse(accessor.isValNeeded());
        assertFalse(accessor.HasAccessors());
        assertFalse(accessor.ignore());
        assertEquals("", accessor.GetConstantValue());
        // No write-accessor protocol is reachable: the semantic-declared method is ignored by
        // the reflection boundary (returns null), exactly the retired backend's effective
        // result (its delegation to renderWriteAccessor(owner, value) also yielded null).
        assertNull(accessor.ExportWriteAccessorTo("X"));
        assertNull(LegacyDataRenderer.renderWriteAccessor(accessor, "X"));
    }

    /**
     * A figurative-constant terminal stand-in (the parser builds these for {@code SPACE}/
     * {@code ZERO} literals), carrying only the keyword value {@code GetSpecialAssignment}
     * reads.
     */
    static class KeywordTerminal extends CTerminal
    {
        private final String value;

        KeywordTerminal(String value)
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
        public void ExportTo(Element e, Document root)
        {
            // No-op for the keyword stub.
        }

        @Override
        public CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory)
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
