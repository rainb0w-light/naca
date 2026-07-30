package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.LegacyDataRenderer;
import generate.java.forms.CJavaField;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldValidated;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-validated pseudo-variable ({@code <FIELD>-V}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaFieldValidated}
 * rendered its reference as {@code <owner field reference>.getValidation()}
 * ({@code ExportReference} appended {@code .getValidation()} to
 * {@code renderReference(reference)}). Unlike the retired highlight sibling —
 * whose read wrapped the owner in the {@code getHighlighting(...)} runtime call —
 * the validated read is a fluent {@code .getValidation()} on the owner reference,
 * the same shape as the retired flag sibling's {@code .getFlag()}. This slice adds
 * the {@code fieldValidatedReferenceEntity} template bound through
 * {@code semantic-runtime-bindings.properties} (the sanctioned BMS forms island
 * manifest, never the concrete manifest that {@code FinalArchitectureContractTest}
 * pins to the COBOL/SQL/CICS tree). The template reads only {@code entity.*}
 * properties: {@code <entity.reference>} is the inherited owner slot
 * ({@link semantic.CBaseDataReference#getReference()}), recursively rendered by
 * the assembler. No formatting, export or reference resolution happens in the
 * semantic node, per the architecture principle.
 *
 * <p>{@code getValidation()}/{@code moveValidation()} are a legacy runtime shape
 * with no current naca-rt signature ({@code Edit}/{@code OnlineProgram} expose no
 * Validation accessor), so the feature is registered as {@code bms.field.validated}
 * with no Codegen-Runtime Contract operation — the same treatment as
 * {@code sql.singleStatement}'s verbatim legacy emission. The template mirrors the
 * retired backend byte-for-byte and no runtime operation is declared.
 *
 * <p>The legacy {@code ExportWriteAccessorTo} ({@code moveValidation(...)}) had no
 * live consumer: {@code LegacyDataRenderer.renderWriteAccessor} is only invoked
 * from the FPac accessor backend (the FPac factory's {@code NewEntityFieldValidated}
 * throws {@code NacaTransAssertException}, so no FPac tree ever holds a validated
 * entity) and {@code CJavaFormAccessor} delegates to its owner form, never to a
 * field-validated entity. {@code GetSpecialAssignment}/{@code GetSpecialCondition}
 * on {@code <FIELD>-V} produce no action/condition entity (legacy behavior). So the
 * write accessor retires without a replacement, with no behavior change.
 */
class FieldValidatedRenderTest
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

    private static String render(CEntityFieldValidated fieldValidated)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(fieldValidated, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("validated reference renders owner.getValidation() (legacy ExportReference parity)")
    void referenceRendersGetValidationWrapper()
    {
        CEntityFieldValidated fieldValidated =
            new CEntityFieldValidated(1, "WS-FIELD-V", catalog(), owner());
        // fieldValidatedReferenceEntity renders <entity.reference>.getValidation():
        // the fluent read on the owner field, exactly what
        // CJavaFieldValidated.ExportReference emitted.
        assertEquals(OWNER_REFERENCE + ".getValidation()",
            render(fieldValidated).trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntityFieldValidated fieldValidated =
            factory.NewEntityFieldValidated(1, "WS-FIELD-V", owner());
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityFieldValidated.class, fieldValidated.getClass());
        assertEquals(OWNER_REFERENCE + ".getValidation()",
            render(fieldValidated).trim());
    }

    @Test
    @DisplayName("the legacy data-renderer bridge falls through to the recursive assembler")
    void legacyBridgeFallsThroughToAssembler()
    {
        CEntityFieldValidated fieldValidated =
            new CEntityFieldValidated(1, "WS-FIELD-V", catalog(), owner());
        // Production reference rendering enters through LegacyDataRenderer: the
        // reflective ExportReference lookup finds no generate.* override on the
        // pure semantic entity, so it falls through to renderRoot(REFERENCE).
        String bridged = LegacyDataRenderer.renderReference(fieldValidated, 1);
        assertEquals(OWNER_REFERENCE + ".getValidation()", bridged.trim());
        assertEquals(render(fieldValidated).trim(), bridged.trim());
    }

    @Test
    @DisplayName("the retired backend's semantic predicates are preserved on the pure entity")
    void semanticPredicatesPreserved()
    {
        MockDataEntity ownerField = owner();
        CEntityFieldValidated fieldValidated =
            new CEntityFieldValidated(1, "WS-FIELD-V", catalog(), ownerField);
        // Values exactly as the retired CJavaFieldValidated backend declared them
        // (FIELD / has-accessors / no-val-needed — same shape as CEntityFieldHighlight).
        assertTrue(fieldValidated.HasAccessors());
        assertFalse(fieldValidated.isValNeeded());
        assertEquals(CDataEntity.CDataEntityType.FIELD, fieldValidated.GetDataType());
        assertFalse(fieldValidated.ignore());
        assertSame(ownerField, fieldValidated.getReference());
    }

    @Test
    @DisplayName("production BMS lowering builds a pure <FIELD>V pseudo-variable")
    void productionLoweringBuildsPureValidatedPseudoVar()
    {
        // parser/Cobol/elements/CWorkingEntry.CreateAttributeForCurrentPositionInField
        // (position-6 byte of a BMS MAP/MAPREDEFINE layout) is the production call
        // that builds the <FIELD>V pseudo-variable; it routes through
        // factory.NewEntityFieldValidated, which must now build the pure semantic
        // entity through the rewired factory.
        CObjectCatalog catalog = catalog();
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
        CJavaField field = new CJavaField(1, "NMMASQ", catalog, exporter);

        CEntityFieldValidated fieldValidated =
            factory.NewEntityFieldValidated(1, "NMMASQV", field);

        // The exact pure semantic class: no legacy CJava* controller subclass.
        assertEquals(CEntityFieldValidated.class, fieldValidated.getClass());
        assertSame(field, fieldValidated.getReference());
        assertEquals(CDataEntity.CDataEntityType.FIELD, fieldValidated.GetDataType());
        // Owner-field reference rendering stays on the owner's own backend until
        // the CJavaField retirement slice; this slice only owns the -V wrapper.
    }

    @Test
    @DisplayName("special assignment/condition analysis preserves the legacy no-entity behavior")
    void specialAssignmentAndConditionBranchesPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        MockDataEntity ownerField = owner();
        CEntityFieldValidated fieldValidated =
            new CEntityFieldValidated(1, "WS-FIELD-V", catalog, ownerField);

        // MOVE x TO <FIELD>-V -> no action entity (legacy GetSpecialAssignment
        // body is intentionally empty).
        assertNull(fieldValidated.GetSpecialAssignment(
            new MockTerminal("1"), factory, 2));
        // IF <FIELD>-V = n -> no condition entity (legacy logs an incoherence
        // error and returns null).
        assertNull(fieldValidated.GetSpecialCondition(
            3, "1", semantic.expression.CBaseEntityCondition.EConditionType.IS_EQUAL, factory));
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
