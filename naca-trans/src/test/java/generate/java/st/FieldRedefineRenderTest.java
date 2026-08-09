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
import semantic.forms.CEntityFieldRedefine;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaFieldRedefine}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityFieldRedefine} (BMS is a CICS screen-map DSL, never a COBOL
 * dialect). The redefining edit field is lowered from a BMS {@code MAP}/{@code MAPREDEFINE}
 * working-storage structure ({@code parser/Cobol/elements/CWorkingEntry
 * .DoSemanticAnalysisForMapRedefine}, which calls
 * {@code factory.NewEntityFieldRedefine(line, name, formalLevel)} — the {@code $edit} split
 * field and the {@code CheckRadical} redefining field — then attaches the field's child
 * attributes and types it through the {@code ITypableEntity} setters). This test pins:
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactory.NewEntityFieldRedefine}
 *       (the inherited production factory path, via {@code BmsJavaEntities.fieldRedefine}) builds
 *       exactly the pure semantic entity, not a {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>reference byte-parity</b> — a data reference to the redefining field renders the
 *       target-formatted field name through the {@code recursiveFieldRedefineEntity} forms-island
 *       binding, both via the recursive assembler and via the
 *       {@code LegacyDataRenderer.renderReference} fall-through (the retired backend's
 *       {@code ExportReference == formatIdentifier(GetName())});</li>
 *   <li><b>declaration byte-parity</b> — driving the production legacy traversal protocol
 *       ({@code LegacyLanguageRenderer.invokeExport}, exactly what {@code CJavaForm.DoExport}
 *       calls) renders
 *       {@code Edit <name> = declare.level(<int level>)[.pic("<pic>")][.justifyRight()][.blankWhenZero()].edit() ;}
 *       through the {@code recursiveFieldRedefineDeclarationEntity} template across every
 *       optional-clause branch, followed by the {@code { ... }} block over the field's child
 *       attributes (which keep rendering through the still-direct BMS field backends);</li>
 *   <li><b>no generate coupling</b> — the retired backend's generate-layer calls
 *       ({@code formatIdentifier}, the {@code writeLine/startBlock/exportChildren/endBlock}
 *       block) are supplied by neutral functions the generate-layer factory injects; a hand-built
 *       entity falls back to the neutral legacy normalization and a no-op declaration renderer;</li>
 *   <li><b>protocol preservation</b> — {@code IsEntryField() == true} (a redefining edit field
 *       is always an entry field), {@code FIELD} data type, {@code isValNeeded() == false},
 *       {@code HasAccessors() == false}, no reachable write-accessor protocol, no type decl, and
 *       {@code DoXMLExport -> null}.</li>
 * </ul>
 */
class FieldRedefineRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String renderReference(CEntityFieldRedefine entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityFieldRedefine -> recursiveFieldRedefineEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("factory.NewEntityFieldRedefine builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        CEntityFieldRedefine entity = factory.NewEntityFieldRedefine(1, "FLD$edit", "10");

        // Exactly the pure semantic class, not the retired CJavaFieldRedefine backend subclass.
        assertEquals(CEntityFieldRedefine.class, entity.getClass());
        assertTrue(entity.IsEntryField());
        assertEquals(CDataEntity.CDataEntityType.FIELD, entity.GetDataType());
    }

    @Test
    @DisplayName("field reference renders the formatted name through the recursive assembler")
    void referenceRendersThroughRecursiveAssembler()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);
        CEntityFieldRedefine entity = factory.NewEntityFieldRedefine(1, "MY-FLD$edit", "10");

        // Byte-for-byte the retired backend's ExportReference: formatIdentifier(GetName()).
        // BmsJavaEntities.fieldRedefine injects output::FormatIdentifier; MockJavaExporter
        // inherits the neutral CBaseLanguageExporter.FormatIdentifier ('-'->'_', '#'->'$').
        assertEquals(exporter.FormatIdentifier("MY-FLD$edit"), renderReference(entity));
        assertEquals("MY_FLD$edit", renderReference(entity));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler binding")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityFieldRedefine entity = factory.NewEntityFieldRedefine(1, "MY-FLD$edit", "10");

        // With the backend's reflective ExportReference gone, the semantic-declared path returns
        // null and falls through to the recursive assembler binding (recursiveFieldRedefineEntity).
        assertEquals("MY_FLD$edit", LegacyDataRenderer.renderReference(entity, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("driving the legacy traversal renders the plain .edit() declaration + child block byte-for-byte")
    void declarationAndBlockRenderThroughTraversal()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);

        // An untyped redefining field (SetTypeString leaves type == ""): the declaration carries
        // no .pic(...) clause — exactly the retired backend's empty-type branch.
        CEntityFieldRedefine entity = factory.NewEntityFieldRedefine(1, "FLD$edit", "10");
        entity.SetTypeString(4);
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.DECLARATION);
        // The declaration line, byte-for-byte the retired backend's DoExport: the level is parsed
        // to an int and .edit() ; closes the chain (note the legacy space before the semicolon).
        assertTrue(output.contains("Edit FLD$edit = declare.level(10).edit() ;"),
            "declaration line must render through recursiveFieldRedefineDeclarationEntity, got:\n" + output);
    }

    @Test
    @DisplayName("the pic9/justifyRight/blankWhenZero declaration branches render byte-for-byte")
    void declarationRendersOptionalClauses()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);

        // The fully-decorated branch: a numeric picture with decimals, right-justified and
        // blank-when-zero — exercises every optional clause of the retired backend's DoExport.
        CEntityFieldRedefine decorated = factory.NewEntityFieldRedefine(1, "NUM$edit", "05");
        decorated.SetTypeNum(3, 2);
        decorated.SetRightJustified(true);
        decorated.SetBlankWhenZero(true);

        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(decorated, JavaTemplateRole.DECLARATION);
        assertTrue(output.contains(
            "Edit NUM$edit = declare.level(5).pic(\"999.99\").justifyRight().blankWhenZero().edit() ;"),
            "decorated declaration must render the pic/justifyRight/blankWhenZero clauses, got:\n" + output);

        // The integer pic9 branch (no decimals): no '.' in the picture.
        CEntityFieldRedefine integer = factory.NewEntityFieldRedefine(1, "INT$edit", "05");
        integer.SetTypeNum(4, 0);
        String integerOutput = TemplateLoader.getRecursiveAssembler()
            .renderRoot(integer, JavaTemplateRole.DECLARATION);
        assertTrue(integerOutput.contains(
            "Edit INT$edit = declare.level(5).pic(\"9999\").edit() ;"),
            "integer pic9 declaration must omit the decimal point, got:\n"
                + integerOutput);
    }

    @Test
    @DisplayName("a hand-built entity (no factory) falls back to the neutral normalization and a no-op declaration")
    void handBuiltEntityUsesNeutralFallbacks()
    {
        CEntityFieldRedefine bare = new CEntityFieldRedefine(1, "A-B#C", catalog(), "01");

        // LegacyLanguageRenderer.formatIdentifier's no-output fallback ('-'->'_', '#'->'$').
        assertEquals("A_B$C", bare.getFormattedName());
        assertEquals(1, bare.getLevel());
        assertEquals("", bare.getPicClause());
        assertEquals("", bare.getJustifyRightClause());
        assertEquals("", bare.getBlankWhenZeroClause());
        // No factory-injected renderer: DoExport is a no-op and never fails.
        LegacyLanguageRenderer.invokeExport(bare);
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityFieldRedefine entity = factory.NewEntityFieldRedefine(1, "FLD$edit", "10");

        // A redefining edit field: always an entry field, FIELD data type, never a declared val,
        // never an accessor-bearing variable, not ignored, an empty type decl, and contributes no
        // XML/.res node of its own.
        assertTrue(entity.IsEntryField());
        assertEquals(CDataEntity.CDataEntityType.FIELD, entity.GetDataType());
        assertFalse(entity.isValNeeded());
        assertFalse(entity.HasAccessors());
        assertFalse(entity.ignore());
        assertEquals("", entity.GetTypeDecl());
        assertNull(entity.DoXMLExport(null, null));
        // No write-accessor protocol is reachable (renderWriteAccessor finds no generate.*
        // ExportWriteAccessorTo override on the pure entity).
        assertNull(LegacyDataRenderer.renderWriteAccessor(entity, "X"));
    }
}
