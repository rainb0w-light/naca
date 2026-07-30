package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityFieldHighlight;
import semantic.forms.CEntityIsFieldHighlight;
import semantic.forms.CEntitySetHighligh;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-highlight pseudo-variable ({@code <FIELD>-H}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaFieldHighligh}
 * rendered its reference as {@code getHighlighting(<owner field reference>)}
 * ({@code ExportReference} wrapped {@code renderReference(reference)} in the
 * {@code OnlineProgram.getHighlighting(Edit)} runtime read). Unlike the retired
 * color/length siblings — whose {@code ExportReference} merely delegated to the
 * owner field and could reuse {@code valueReferenceEntity} — the highlight read
 * wraps the owner in a runtime call, so this slice adds the
 * {@code fieldHighlightReferenceEntity} template bound through
 * {@code semantic-runtime-bindings.properties} (the sanctioned BMS forms island
 * manifest, never the concrete manifest that {@code FinalArchitectureContractTest}
 * pins to the COBOL/SQL/CICS tree). The template reads only {@code entity.*}
 * properties: {@code <entity.reference>} is the inherited owner slot
 * ({@link semantic.CBaseDataReference#getReference()}), recursively rendered by
 * the assembler. No formatting, export or reference resolution happens in the
 * semantic node, per the architecture principle. The emitted call is contracted
 * as operation {@code bms.highlight.get} (runtime-operations.yaml,
 * template-runtime-requirements.yaml, feature {@code bms.field.highlight}).
 *
 * <p>The legacy {@code ExportWriteAccessorTo} ({@code moveHighlight(...)}) had no
 * live consumer: there is no {@code moveHighlight} signature in naca-rt (the
 * runtime method is {@code moveHighLighting}), {@code LegacyDataRenderer.renderWriteAccessor}
 * is only invoked from the FPac accessor backend (the FPac factory's
 * {@code NewEntityFieldHighlight} throws {@code NacaTransAssertException}, so no
 * FPac tree ever holds a highlight entity), and {@code CJavaFormAccessor}
 * delegates to its owner form, never to a field-highlight entity. MOVEs and
 * conditions on {@code <FIELD>-H} lower through the {@code CEntitySetHighligh} /
 * {@code CEntityIsFieldHighlight} action/condition entities built by the pure
 * semantic node's {@code GetSpecialAssignment}/{@code GetSpecialCondition}. So the
 * write accessor retires without a replacement, with no behavior change.
 */
class FieldHighlightRenderTest
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

    private static String render(CEntityFieldHighlight fieldHighlight)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(fieldHighlight, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("highlight reference renders getHighlighting(owner) (legacy ExportReference parity)")
    void referenceRendersGetHighlightingWrapper()
    {
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog(), owner());
        // fieldHighlightReferenceEntity renders getHighlighting(<entity.reference>):
        // the owner field wrapped in the runtime read, exactly what
        // CJavaFieldHighligh.ExportReference emitted.
        assertEquals("getHighlighting(" + OWNER_REFERENCE + ")",
            render(fieldHighlight).trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntityFieldHighlight fieldHighlight =
            factory.NewEntityFieldHighlight(1, "WS-FIELD-H", owner());
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityFieldHighlight.class, fieldHighlight.getClass());
        assertEquals("getHighlighting(" + OWNER_REFERENCE + ")",
            render(fieldHighlight).trim());
    }

    @Test
    @DisplayName("the legacy data-renderer bridge falls through to the recursive assembler")
    void legacyBridgeFallsThroughToAssembler()
    {
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog(), owner());
        // Production reference rendering enters through LegacyDataRenderer: the
        // reflective ExportReference lookup finds no generate.* override on the
        // pure semantic entity, so it falls through to renderRoot(REFERENCE).
        String bridged = LegacyDataRenderer.renderReference(fieldHighlight, 1);
        assertEquals("getHighlighting(" + OWNER_REFERENCE + ")", bridged.trim());
        assertEquals(render(fieldHighlight).trim(), bridged.trim());
    }

    @Test
    @DisplayName("the retired backend's semantic predicates are preserved on the pure entity")
    void semanticPredicatesPreserved()
    {
        MockDataEntity ownerField = owner();
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog(), ownerField);
        // Values exactly as the retired CJavaFieldHighligh backend declared them
        // (FIELD / has-accessors / no-val-needed — same shape as CEntityFieldLength).
        assertTrue(fieldHighlight.HasAccessors());
        assertFalse(fieldHighlight.isValNeeded());
        assertEquals(CDataEntity.CDataEntityType.FIELD, fieldHighlight.GetDataType());
        assertFalse(fieldHighlight.ignore());
        assertSame(ownerField, fieldHighlight.getReference());
    }

    @Test
    @DisplayName("production BMS lowering (InitDependences) registers a pure <FIELD>H pseudo-variable")
    void productionLoweringBuildsPureHighlightPseudoVar()
    {
        // CEntityResourceField.InitDependences is the production call the BMS
        // transcoder engine drives for every named map field (ONLINM1.bms fields
        // reach it through BmsArtifactContractTest end to end): it must now build
        // the pure semantic entity through the rewired factory.
        CObjectCatalog catalog = catalog();
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
        CJavaField field = new CJavaField(1, "NMMASQ", catalog, exporter);

        field.InitDependences(factory);

        CDataEntity pseudo = catalog.GetDataEntity("NMMASQH", "");
        CEntityFieldHighlight fieldHighlight =
            assertInstanceOf(CEntityFieldHighlight.class, pseudo);
        // The exact pure semantic class: no legacy CJava* controller subclass.
        assertEquals(CEntityFieldHighlight.class, fieldHighlight.getClass());
        assertSame(field, fieldHighlight.getReference());
        assertEquals(CDataEntity.CDataEntityType.FIELD, fieldHighlight.GetDataType());
        // Owner-field reference rendering stays on the owner's own backend until
        // the CJavaField retirement slice; this slice only owns the -H wrapper.
    }

    @Test
    @DisplayName("special assignment/condition analysis still builds the set/is highlight entities")
    void specialAssignmentAndConditionBranchesPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        MockDataEntity ownerField = owner();
        CEntityFieldHighlight fieldHighlight =
            new CEntityFieldHighlight(1, "WS-FIELD-H", catalog, ownerField);

        // MOVE 2 TO <FIELD>-H -> reverse highlight (semantic analysis unchanged).
        CBaseActionEntity reverse =
            CEntityFieldHighlight.intGetSpecialAssignment("2", ownerField, factory, 2);
        assertInstanceOf(CEntitySetHighligh.class, reverse);
        // MOVE 1 TO <FIELD>-H -> blink.
        CBaseActionEntity blink = fieldHighlight
            .GetSpecialAssignment(new MockTerminal("1"), factory, 3);
        assertInstanceOf(CEntitySetHighligh.class, blink);
        // Unsupported constant: no action entity (legacy behavior preserved).
        assertNull(CEntityFieldHighlight.intGetSpecialAssignment("9", ownerField, factory, 4));

        // IF <FIELD>-H = 4 -> is-underlined condition entity.
        CBaseEntityCondition underlined = fieldHighlight.GetSpecialCondition(
            5, "4", CBaseEntityCondition.EConditionType.IS_EQUAL, factory);
        assertInstanceOf(CEntityIsFieldHighlight.class, underlined);
        // Unsupported condition constant: no condition entity.
        assertNull(fieldHighlight.GetSpecialCondition(
            6, "3", CBaseEntityCondition.EConditionType.IS_EQUAL, factory));
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
