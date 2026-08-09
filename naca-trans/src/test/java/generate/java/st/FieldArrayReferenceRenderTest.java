package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.LegacyDataRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprTerminal;
import semantic.forms.CEntityFieldArrayReference;
import utils.CObjectCatalog;

/**
 * BMS map-resource field array reference (an indexed read of an OCCURS-style map
 * field) rendering through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaFieldArrayReference}
 * rendered its reference as {@code <field reference>.getAt(<indexes>)}: its
 * {@code ExportReference} appended {@code .getAt(idx0, idx1, ...)} to
 * {@code LegacyDataRenderer.renderReference(reference)}. That output is
 * byte-for-byte the frozen {@code arrayReferenceEntity} template the COBOL
 * {@link semantic.CEntityArrayReference} already uses, so this slice reuses that
 * template (the same reuse the forms island applies to {@code valueReferenceEntity})
 * rather than duplicating it, and binds it through
 * {@code semantic-runtime-bindings.properties} (the sanctioned BMS forms island
 * manifest, never the concrete manifest that {@code FinalArchitectureContractTest}
 * pins to the COBOL/SQL/CICS tree). The template reads only {@code entity.*}
 * properties: {@code <entity.reference>} is the inherited owner slot
 * ({@link semantic.CBaseDataReference#getReference()}) and {@code <entity.indexes>}
 * the inherited {@link semantic.CEntityArrayReference#getIndexes()} list, both
 * recursively rendered by the assembler. No formatting, export or reference
 * resolution happens in the semantic node, per the architecture principle.
 *
 * <p>Like {@code cobol.data.array-reference}, the feature carries no
 * Codegen-Runtime Contract operation: {@code .getAt(...)} is the established
 * runtime shape already emitted by the COBOL array reference, so no new
 * runtime-operations.yaml / template-runtime-requirements.yaml entry is declared.
 *
 * <p>The retired backend's {@code ExportWriteAccessorTo} returned {@code ""}
 * (unused) and had no live consumer: {@code LegacyDataRenderer.renderWriteAccessor}
 * is only invoked from the FPac accessor backend, and the FPac factory's
 * {@code NewEntityFieldArrayReference} throws {@code NacaTransAssertException}, so
 * no FPac tree ever holds a field array reference. It retires without a replacement,
 * with no behavior change. The retired backend's {@code GetDataType() -> FIELD}
 * (versus the {@code CEntityArrayReference} base's {@code VAR}) is preserved on the
 * pure semantic entity.
 */
class FieldArrayReferenceRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityFieldArrayReference array)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(array, JavaTemplateRole.REFERENCE);
    }

    private CEntityFieldArrayReference array(String name, CBaseEntityExpression... indexes)
    {
        CEntityFieldArrayReference array = new CEntityFieldArrayReference(1, catalog);
        array.SetReference(new MockDataEntity(1, name));
        for (CBaseEntityExpression index : indexes)
        {
            array.AddIndex(index);
        }
        return array;
    }

    private CBaseEntityExpression terminal(String value)
    {
        return new CEntityExprTerminal(new MockDataEntity(1, value));
    }

    @Test
    @DisplayName("field array reference renders field.getAt(index) (legacy ExportReference parity)")
    void referenceRendersGetAtWrapper()
    {
        // arrayReferenceEntity renders <entity.reference>.getAt(<entity.indexes>):
        // exactly what CJavaFieldArrayReference.ExportReference emitted for a single
        // index (renderReference(reference) + ".getAt(" + idx + ")").
        assertEquals("MAP.WS-FIELD.getAt(1)", render(array("MAP.WS-FIELD", terminal("1"))));
    }

    @Test
    @DisplayName("multiple indexes render comma separated, as the retired backend concatenated them")
    void multipleIndexesRenderCommaSeparated()
    {
        // The retired backend looped over arrIndexes joining them with ", "; the
        // template's separator=", " reproduces that byte-for-byte.
        assertEquals("TABLE.getAt(2, 3)", render(array("TABLE", terminal("2"), terminal("3"))));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntityFieldArrayReference array = factory.NewEntityFieldArrayReference(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityFieldArrayReference.class, array.getClass());
        array.SetReference(new MockDataEntity(1, "MAP.WS-FIELD"));
        array.AddIndex(terminal("1"));
        assertEquals("MAP.WS-FIELD.getAt(1)", render(array));
    }

    @Test
    @DisplayName("both production factories build the pure semantic entity")
    void bothFactoriesReturnPureSemanticEntity()
    {
        // NewEntityFieldArrayReference is the production lowering seam: both the
        // direct and ST4 factories must now build the pure entity (CJavaEntityFactory
        // inherits the rewired CJavaEntityFactory method).
        assertEquals(CEntityFieldArrayReference.class,
            new CJavaEntityFactory(catalog, null).NewEntityFieldArrayReference(1).getClass());
        assertEquals(CEntityFieldArrayReference.class,
            new CJavaEntityFactory(catalog, null).NewEntityFieldArrayReference(1).getClass());
    }

    @Test
    @DisplayName("the legacy data-renderer bridge falls through to the recursive assembler")
    void legacyBridgeFallsThroughToAssembler()
    {
        CEntityFieldArrayReference array = array("MAP.WS-FIELD", terminal("1"));
        // Production reference rendering enters through LegacyDataRenderer: the
        // reflective ExportReference lookup finds no generate.* override on the
        // pure semantic entity, so it falls through to renderRoot(REFERENCE).
        String bridged = LegacyDataRenderer.renderReference(array, 1);
        assertEquals("MAP.WS-FIELD.getAt(1)", bridged);
        assertEquals(render(array), bridged);
    }

    @Test
    @DisplayName("the retired backend's semantic predicates are preserved on the pure entity")
    void semanticPredicatesPreserved()
    {
        MockDataEntity ownerField = new MockDataEntity(1, "MAP.WS-FIELD");
        CEntityFieldArrayReference array = new CEntityFieldArrayReference(1, catalog);
        array.SetReference(ownerField);
        array.AddIndex(terminal("1"));
        // Values exactly as the retired CJavaFieldArrayReference backend declared
        // them: FIELD data type (the base reports VAR), no accessors, val needed.
        assertEquals(CDataEntity.CDataEntityType.FIELD, array.GetDataType());
        assertFalse(array.HasAccessors());
        assertTrue(array.isValNeeded());
        assertSame(ownerField, array.getReference());
        assertEquals(1, array.getIndexes().size());
    }

    @Test
    @DisplayName("production BMS lowering builds a pure field array reference")
    void productionLoweringBuildsPureArrayReference()
    {
        // semantic.forms.CEntityFieldRedefine.GetArrayReference (an indexed read of
        // an OCCURS-style map field) and the pure node's own GetSpecialCondition are
        // the production callers; both route through factory.NewEntityFieldArrayReference,
        // which must now build the pure semantic entity through the rewired factory.
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntityFieldArrayReference array = factory.NewEntityFieldArrayReference(1);

        // The exact pure semantic class: no legacy CJava* controller subclass.
        assertEquals(CEntityFieldArrayReference.class, array.getClass());
        assertEquals(CDataEntity.CDataEntityType.FIELD, array.GetDataType());
        array.SetReference(new MockDataEntity(1, "NMMASQ"));
        array.AddIndex(terminal("2"));
        assertEquals("NMMASQ", ((MockDataEntity) array.getReference()).getMockReferenceValue());
        assertEquals("NMMASQ.getAt(2)", render(array));
    }
}
