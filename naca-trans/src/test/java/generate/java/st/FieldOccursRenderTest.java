package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.LegacyDataRenderer;
import generate.LegacyLanguageRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldOccurs;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaFieldOccurs}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityFieldOccurs} (BMS is a CICS screen-map DSL, never a COBOL
 * dialect). The occurs group is lowered from a COBOL group item carrying {@code OCCURS} inside
 * a BMS {@code MAP}/{@code MAPREDEFINE} working-storage structure
 * ({@code parser/Cobol/elements/CWorkingEntry.DoSemanticAnalysisForMapRedefine}, the
 * {@code le.occurs != null} branch at line 1345, which calls
 * {@code factory.NewEntityFieldOccurs(line, name)} then {@code SetFieldOccurs(formalLevel, occ)}
 * and attaches the group's child fields). This test pins:
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactoryST.NewEntityFieldOccurs}
 *       (the inherited production factory path, via {@code BmsJavaEntities.fieldOccurs}) builds
 *       exactly the pure semantic entity, not a {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>reference byte-parity</b> — a data reference to the group renders the target-formatted
 *       group name through the {@code recursiveFieldOccursEntity} forms-island binding, both via
 *       the recursive assembler and via the {@code LegacyDataRenderer.renderReference}
 *       fall-through (the retired backend's {@code ExportReference == formatIdentifier(GetName())});</li>
 *   <li><b>declaration byte-parity</b> — driving the production legacy traversal protocol
 *       ({@code LegacyLanguageRenderer.invokeExport}, exactly what {@code CJavaForm.DoExport} /
 *       {@code CJavaFieldRedefine.DoExport} call) renders
 *       {@code Edit <name> = declare.level(<int level>).editOccurs(<occurs ref>, "<name>") ;}
 *       through the {@code recursiveFieldOccursDeclarationEntity} template, with the
 *       {@code OCCURS} reference pre-rendered through the exact legacy
 *       {@code LegacyDataRenderer.renderReference} protocol, followed by the {@code { ... }}
 *       block over the group's child fields (which keep rendering through the still-direct BMS
 *       field backends);</li>
 *   <li><b>no generate coupling</b> — the retired backend's generate-layer calls
 *       ({@code formatIdentifier}, {@code renderReference}, the {@code writeLine/startBlock/
 *       exportChildren/endBlock} block) are supplied by neutral functions the generate-layer
 *       factory injects; a hand-built entity falls back to the neutral legacy normalization and
 *       a no-op declaration renderer;</li>
 *   <li><b>protocol preservation</b> — {@code IsEntryField() == false}, {@code FIELD} data type,
 *       {@code isValNeeded() == false}, {@code HasAccessors() == false}, no reachable
 *       write-accessor protocol, and {@code DoXMLExport -> null}.</li>
 * </ul>
 */
class FieldOccursRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String renderReference(CEntityFieldOccurs entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityFieldOccurs -> recursiveFieldOccursEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("factory.NewEntityFieldOccurs builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        CEntityFieldOccurs entity = factory.NewEntityFieldOccurs(1, "MY-GRP");
        entity.SetFieldOccurs("05", new MockDataEntity(1, "WS_COUNT"));

        // Exactly the pure semantic class, not the retired CJavaFieldOccurs backend subclass.
        assertEquals(CEntityFieldOccurs.class, entity.getClass());
        assertFalse(entity.IsEntryField());
        assertEquals(CDataEntity.CDataEntityType.FIELD, entity.GetDataType());
    }

    @Test
    @DisplayName("group reference renders the formatted name through the recursive assembler")
    void referenceRendersThroughRecursiveAssembler()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog(), exporter);
        CEntityFieldOccurs entity = factory.NewEntityFieldOccurs(1, "MY-GRP");
        entity.SetFieldOccurs("05", new MockDataEntity(1, "WS_COUNT"));

        // Byte-for-byte the retired backend's ExportReference: formatIdentifier(GetName()).
        // BmsJavaEntities.fieldOccurs injects output::FormatIdentifier; MockJavaExporter
        // inherits the neutral CBaseLanguageExporter.FormatIdentifier ('-'->'_', '#'->'$').
        assertEquals(exporter.FormatIdentifier("MY-GRP"), renderReference(entity));
        assertEquals("MY_GRP", renderReference(entity));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler binding")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityFieldOccurs entity = factory.NewEntityFieldOccurs(1, "MY-GRP");
        entity.SetFieldOccurs("05", new MockDataEntity(1, "WS_COUNT"));

        // With the backend's reflective ExportReference gone, the semantic-declared path returns
        // null and falls through to the recursive assembler binding (recursiveFieldOccursEntity).
        assertEquals("MY_GRP", LegacyDataRenderer.renderReference(entity, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("driving the legacy traversal renders the editOccurs declaration + child block byte-for-byte")
    void declarationAndBlockRenderThroughTraversal()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog(), exporter);

        CEntityFieldOccurs entity = factory.NewEntityFieldOccurs(1, "MY-GRP");
        // The OCCURS reference (the OCCURS DEPENDING ON counter the parser resolved). The mock
        // declares ExportReference in a generate.* class, so LegacyDataRenderer.renderReference
        // uses it directly — the exact legacy protocol the generate-layer bridge preserves.
        entity.SetFieldOccurs("05", new MockDataEntity(1, "WS_COUNT"));
        // A child field of the occurs group: still rendered through the legacy traversal's
        // exportChildren (the child backends are a separate, later retirement tier).
        entity.AddChild(new MockDataEntity(1, catalog(), exporter, "Edit CHILD = declare.level(10).edit() ;"));

        // The exact production traversal protocol: CJavaForm.DoExport / CJavaFieldRedefine.DoExport
        // reflectively invoke DoExport on each field; for the pure entity that dispatches to the
        // generate-layer declaration renderer injected by BmsJavaEntities.fieldOccurs.
        LegacyLanguageRenderer.invokeExport(entity);

        String output = exporter.getCapturedOutput();
        // The declaration line, byte-for-byte the retired backend's DoExport: the same formatted
        // name is the Java variable and the quoted editOccurs argument; the level is parsed to int.
        assertTrue(output.contains(
            "Edit MY_GRP = declare.level(5).editOccurs(WS_COUNT, \"MY_GRP\") ;"),
            "declaration line must render through recursiveFieldOccursDeclarationEntity, got:\n" + output);
        // The child renders inside the block (after the declaration), through the legacy traversal.
        assertTrue(output.contains("Edit CHILD = declare.level(10).edit() ;"),
            "child field must render through the legacy block traversal, got:\n" + output);
        assertTrue(output.indexOf("Edit MY_GRP") < output.indexOf("Edit CHILD"),
            "the declaration precedes its child block, got:\n" + output);
    }

    @Test
    @DisplayName("a hand-built entity (no factory) falls back to the neutral normalization and a no-op declaration")
    void handBuiltEntityUsesNeutralFallbacks()
    {
        CEntityFieldOccurs bare = new CEntityFieldOccurs(1, "A-B#C", catalog());
        bare.SetFieldOccurs("01", new MockDataEntity(1, "N"));

        // LegacyLanguageRenderer.formatIdentifier's no-output fallback ('-'->'_', '#'->'$').
        assertEquals("A_B$C", bare.getFormattedName());
        assertEquals(1, bare.getLevel());
        // No factory-injected renderer: DoExport is a no-op and never fails.
        LegacyLanguageRenderer.invokeExport(bare);
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityFieldOccurs entity = factory.NewEntityFieldOccurs(1, "MY-GRP");
        entity.SetFieldOccurs("05", new MockDataEntity(1, "WS_COUNT"));

        // An occurs group: never an entry field, FIELD data type, never a declared val, never an
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
