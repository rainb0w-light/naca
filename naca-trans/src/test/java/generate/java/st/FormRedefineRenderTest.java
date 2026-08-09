package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.LegacyDataRenderer;
import generate.LegacyLanguageRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.forms.CEntityFormRedefine;
import semantic.forms.CEntityResourceForm;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaFormRedefine}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityFormRedefine} (BMS is a CICS screen-map DSL, never a COBOL
 * dialect). A form redefine is lowered from a BMS {@code MAP}/{@code MAPREDEFINE}
 * working-storage structure ({@code parser/Cobol/elements/CWorkingEntry
 * .DoSemanticAnalysisForMapRedefine}, which calls
 * {@code factory.NewEntityFormRedefine(line, name, ref, issaveMap)} — the {@code ref} being a
 * {@code CEntityValueReference} wrapping the origin form). This test pins:
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactory.NewEntityFormRedefine}
 *       (the inherited production factory path, via {@code BmsJavaEntities.formRedefine}) builds
 *       exactly the pure semantic entity, not a {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>reference byte-parity</b> — a data reference to the form redefine renders the
 *       target-formatted name through the {@code recursiveFormRedefineEntity} forms-island
 *       binding, both via the recursive assembler and via the
 *       {@code LegacyDataRenderer.renderReference} fall-through (the retired backend's
 *       {@code ExportReference == formatIdentifier(GetName())});</li>
 *   <li><b>declaration byte-parity</b> — driving the production legacy traversal protocol
 *       ({@code LegacyLanguageRenderer.invokeExport}, exactly what {@code CJavaFormContainer
 *       .DoExport} calls) renders
 *       {@code MapRedefine <name> = declare.level(1).redefinesMap(<origin ref>) ;}
 *       through the {@code recursiveFormRedefineDeclarationEntity} template, followed by the
 *       {@code { ... }} block over the form redefine's children;</li>
 *   <li><b>latent defect fix</b> — the constructor self-assignment ({@code eForm = eForm}) is
 *       fixed to {@code this.eForm = eForm}, so the origin form reference flows to the
 *       {@code redefinesMap(Form)} call instead of producing {@code [UNDEFINED]};</li>
 *   <li><b>no generate coupling</b> — the retired backend's generate-layer calls
 *       ({@code formatIdentifier}, the {@code writeLine/startBlock/exportChildren/endBlock}
 *       block) are supplied by neutral functions the generate-layer factory injects; a hand-built
 *       entity falls back to the neutral legacy normalization and a no-op declaration renderer;</li>
 *   <li><b>protocol preservation</b> — {@code FORM} data type, {@code isValNeeded() == false},
 *       {@code HasAccessors() == false}, no reachable write-accessor protocol, no type decl, and
 *       {@code DoXMLExport -> null}.</li>
 * </ul>
 */
class FormRedefineRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String renderReference(CEntityFormRedefine entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityFormRedefine -> recursiveFormRedefineEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("factory.NewEntityFormRedefine builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // Build an origin form to pass as the redefined entity (production passes a
        // CEntityValueReference wrapping the form; here we pass the form directly — the
        // constructor only stores the reference).
        CEntityResourceForm originForm = factory.NewEntityForm(1, "ORIG-MAP", false);
        CEntityFormRedefine entity = factory.NewEntityFormRedefine(1, "REDEF-MAP", originForm, false);

        // Exactly the pure semantic class, not the retired CJavaFormRedefine backend subclass.
        assertEquals(CEntityFormRedefine.class, entity.getClass());
        assertEquals(CDataEntity.CDataEntityType.FORM, entity.GetDataType());
    }

    @Test
    @DisplayName("form redefine reference renders the formatted name through the recursive assembler")
    void referenceRendersThroughRecursiveAssembler()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);
        CEntityResourceForm originForm = factory.NewEntityForm(1, "ORIG-MAP", false);
        CEntityFormRedefine entity = factory.NewEntityFormRedefine(1, "REDEF-MAP", originForm, false);

        // Byte-for-byte the retired backend's ExportReference: formatIdentifier(GetName()).
        // BmsJavaEntities.formRedefine injects output::FormatIdentifier; MockJavaExporter
        // inherits the neutral CBaseLanguageExporter.FormatIdentifier ('-'->'_').
        assertEquals(exporter.FormatIdentifier("REDEF-MAP"), renderReference(entity));
        assertEquals("REDEF_MAP", renderReference(entity));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler binding")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityResourceForm originForm = factory.NewEntityForm(1, "ORIG-MAP", false);
        CEntityFormRedefine entity = factory.NewEntityFormRedefine(1, "REDEF-MAP", originForm, false);

        // With the backend's reflective ExportReference gone, the semantic-declared path returns
        // null and falls through to the recursive assembler binding (recursiveFormRedefineEntity).
        assertEquals("REDEF_MAP", LegacyDataRenderer.renderReference(entity, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("driving the legacy traversal renders the MapRedefine declaration + child block byte-for-byte")
    void declarationAndBlockRenderThroughTraversal()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);

        // Build the origin form that the redefine re-views.
        CEntityResourceForm originForm = factory.NewEntityForm(1, "ORIG-MAP", false);
        CEntityFormRedefine entity = factory.NewEntityFormRedefine(1, "REDEF-MAP", originForm, false);
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.DECLARATION);
        // The declaration line: MapRedefine <name> = declare.level(1).redefinesMap(<ref>) ;
        // The origin form's reference renders through the recursiveFormEntity binding as "ORIG_MAP".
        assertTrue(output.contains(
            "MapRedefine REDEF_MAP = declare.level(1).redefinesMap(ORIG_MAP) ;"),
            "declaration line must render through recursiveFormRedefineDeclarationEntity, got:\n" + output);
    }

    @Test
    @DisplayName("the latent self-assignment fix flows the real origin form reference to redefinesMap")
    void selfAssignmentFixFlowsOriginFormReference()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);

        // The origin form is now correctly stored (the legacy constructor had eForm = eForm
        // self-assignment which nulled the field).
        CEntityResourceForm originForm = factory.NewEntityForm(1, "ORIG-MAP", false);
        CEntityFormRedefine entity = factory.NewEntityFormRedefine(1, "REDEF-MAP", originForm, false);

        // The getter returns the real origin form (not null as with the old self-assignment).
        assertEquals(originForm, entity.getForm());

        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.DECLARATION);
        assertFalse(output.contains("[UNDEFINED]"),
            "the origin form reference must not be [UNDEFINED] after the self-assignment fix, got:\n" + output);
        assertTrue(output.contains("redefinesMap(ORIG_MAP)"),
            "redefinesMap must receive the real origin form reference, got:\n" + output);
    }

    @Test
    @DisplayName("a hand-built entity (no factory) falls back to the neutral normalization and a no-op declaration")
    void handBuiltEntityUsesNeutralFallbacks()
    {
        CEntityFormRedefine bare = new CEntityFormRedefine(1, "A-B#C", catalog(), null, false);

        // LegacyLanguageRenderer.formatIdentifier's no-output fallback ('-'->'_', '#'->'$').
        assertEquals("A_B$C", bare.getFormattedName());
        assertEquals("", bare.getRedefinesReference());
        // No factory-injected renderer: DoExport is a no-op and never fails.
        LegacyLanguageRenderer.invokeExport(bare);
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityResourceForm originForm = factory.NewEntityForm(1, "ORIG-MAP", false);
        CEntityFormRedefine entity = factory.NewEntityFormRedefine(1, "REDEF-MAP", originForm, false);

        // A form redefine: FORM data type, never a declared val, never an accessor-bearing
        // variable, not ignored, an empty type decl, and contributes no XML/.res node of its own.
        assertEquals(CDataEntity.CDataEntityType.FORM, entity.GetDataType());
        assertFalse(entity.isValNeeded());
        assertFalse(entity.HasAccessors());
        assertFalse(entity.ignore());
        assertEquals("", entity.GetTypeDecl());
        assertNull(entity.DoXMLExport());
        // No write-accessor protocol is reachable (renderWriteAccessor finds no generate.*
        // ExportWriteAccessorTo override on the pure entity).
        assertNull(LegacyDataRenderer.renderWriteAccessor(entity, "X"));
    }
}
