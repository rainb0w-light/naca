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
import semantic.forms.CEntitySkipFields;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaSkipField}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntitySkipFields} (BMS is a CICS screen-map DSL, never a COBOL dialect).
 * A skip field is lowered from a BMS {@code MAP}/{@code MAPREDEFINE} working-storage structure
 * ({@code parser/Cobol/elements/CWorkingEntry.DoSemanticAnalysisForMapRedefine}, the skip-field
 * branches, which call {@code factory.NewEntityWorkingSkipField(line, name, nbFields, level)}
 * with the byte count resolved by {@code eForm.ConsumeFieldsAsBytes(le.length)} and attach the
 * entity as a child of the enclosing form/redefine). This test pins:
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactory.NewEntityWorkingSkipField}
 *       (the inherited production factory path, via {@code BmsJavaEntities.skipFields}) builds
 *       exactly the pure semantic entity, not a {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>reference byte-parity</b> — a data reference to the skip field renders the
 *       target-formatted name through the {@code recursiveSkipFieldEntity} forms-island binding,
 *       both via the recursive assembler and via the {@code LegacyDataRenderer.renderReference}
 *       fall-through (the retired backend's {@code ExportReference == formatIdentifier(GetName())});</li>
 *   <li><b>declaration byte-parity</b> — driving the production legacy traversal protocol
 *       ({@code LegacyLanguageRenderer.invokeExport}, exactly what {@code CJavaForm.DoExport} /
 *       {@code CJavaFieldRedefine.DoExport} call) renders
 *       {@code Edit <name> = declare.level(<int level>).editSkip(<nbFields>) ;} through the
 *       {@code recursiveSkipFieldDeclarationEntity} template, followed by the {@code { ... }}
 *       block over any child fields (which keep rendering through the still-direct BMS field
 *       backends);</li>
 *   <li><b>latent self-assignment fix</b> — the retired constructor assigned the parameter to
 *       itself ({@code nbFields = nbFields}), so the field stayed 0 and every legacy emission was
 *       {@code editSkip(0)}; the pure entity binds the real count, so {@code getNbFields()} and
 *       the rendered {@code editSkip(<n>)} carry the parser-resolved byte count;</li>
 *   <li><b>no generate coupling</b> — the retired backend's generate-layer calls
 *       ({@code formatIdentifier}, the {@code writeLine/startBlock/exportChildren/endBlock} block)
 *       are supplied by neutral functions the generate-layer factory injects; a hand-built entity
 *       falls back to the neutral legacy normalization and a no-op declaration renderer;</li>
 *   <li><b>protocol preservation</b> — {@code IsEntryField() == false}, {@code FIELD} data type,
 *       {@code isValNeeded() == false}, {@code HasAccessors() == false}, no reachable
 *       write-accessor protocol, and {@code DoXMLExport -> null}.</li>
 * </ul>
 */
class SkipFieldRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String renderReference(CEntitySkipFields entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntitySkipFields -> recursiveSkipFieldEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("factory.NewEntityWorkingSkipField builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        CEntitySkipFields entity = factory.NewEntityWorkingSkipField(1, "MY-SKIP", 3, "05");

        // Exactly the pure semantic class, not the retired CJavaSkipField backend subclass.
        assertEquals(CEntitySkipFields.class, entity.getClass());
        assertFalse(entity.IsEntryField());
        assertEquals(CDataEntity.CDataEntityType.FIELD, entity.GetDataType());
    }

    @Test
    @DisplayName("skip-field reference renders the formatted name through the recursive assembler")
    void referenceRendersThroughRecursiveAssembler()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);
        CEntitySkipFields entity = factory.NewEntityWorkingSkipField(1, "MY-SKIP", 3, "05");

        // Byte-for-byte the retired backend's ExportReference: formatIdentifier(GetName()).
        // BmsJavaEntities.skipFields injects output::FormatIdentifier; MockJavaExporter inherits
        // the neutral CBaseLanguageExporter.FormatIdentifier ('-'->'_', '#'->'$').
        assertEquals(exporter.FormatIdentifier("MY-SKIP"), renderReference(entity));
        assertEquals("MY_SKIP", renderReference(entity));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler binding")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntitySkipFields entity = factory.NewEntityWorkingSkipField(1, "MY-SKIP", 3, "05");

        // With the backend's reflective ExportReference gone, the semantic-declared path returns
        // null and falls through to the recursive assembler binding (recursiveSkipFieldEntity).
        assertEquals("MY_SKIP", LegacyDataRenderer.renderReference(entity, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("driving the legacy traversal renders the editSkip declaration + child block byte-for-byte")
    void declarationAndBlockRenderThroughTraversal()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);

        CEntitySkipFields entity = factory.NewEntityWorkingSkipField(1, "MY-SKIP", 3, "05");
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.DECLARATION);
        // The declaration line: the formatted name is the Java variable; the level is parsed to
        // int; editSkip carries the parser-resolved consumed-field count (3), not the legacy 0.
        assertTrue(output.contains(
            "Edit MY_SKIP = declare.level(5).editSkip(3) ;"),
            "declaration line must render through recursiveSkipFieldDeclarationEntity, got:\n" + output);
    }

    @Test
    @DisplayName("the latent constructor self-assignment is fixed: the real field count flows through")
    void selfAssignmentFixedSoRealCountFlows()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // The parser passes the byte count it resolved (ConsumeFieldsAsBytes); pre-fix the
        // constructor dropped it (nbFields = nbFields) and getNbFields()/editSkip saw 0.
        CEntitySkipFields entity = factory.NewEntityWorkingSkipField(1, "S", 7, "10");
        assertEquals(7, entity.getNbFields());
        assertEquals(10, entity.getLevel());
    }

    @Test
    @DisplayName("a hand-built entity (no factory) falls back to the neutral normalization and a no-op declaration")
    void handBuiltEntityUsesNeutralFallbacks()
    {
        CEntitySkipFields bare = new CEntitySkipFields(1, "A-B#C", catalog(), 4, "01");

        // LegacyLanguageRenderer.formatIdentifier's no-output fallback ('-'->'_', '#'->'$').
        assertEquals("A_B$C", bare.getFormattedName());
        assertEquals(1, bare.getLevel());
        assertEquals(4, bare.getNbFields());
        // No factory-injected renderer: DoExport is a no-op and never fails.
        LegacyLanguageRenderer.invokeExport(bare);
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntitySkipFields entity = factory.NewEntityWorkingSkipField(1, "MY-SKIP", 3, "05");

        // A skip field: never an entry field, FIELD data type, never a declared val, never an
        // accessor-bearing variable, not ignored, and contributes no XML/.res node of its own.
        assertFalse(entity.IsEntryField());
        assertEquals(CDataEntity.CDataEntityType.FIELD, entity.GetDataType());
        assertFalse(entity.isValNeeded());
        assertFalse(entity.HasAccessors());
        assertFalse(entity.ignore());
        assertNull(entity.DoXMLExport(null, null));
        // No write-accessor protocol is reachable (renderWriteAccessor finds no generate.*
        // ExportWriteAccessorTo override on the pure entity).
        assertNull(LegacyDataRenderer.renderWriteAccessor(entity, "X"));
    }
}
