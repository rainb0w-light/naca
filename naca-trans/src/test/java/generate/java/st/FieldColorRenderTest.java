package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.LegacyDataRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldColor;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-color pseudo-variable ({@code <FIELD>-C-}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaFieldColor}
 * rendered its reference by delegating to the owner field
 * ({@code ExportReference(nLine) == renderReference(reference)}). The pure
 * semantic entity {@link CEntityFieldColor} carries that owner in the inherited
 * {@code reference} slot ({@link semantic.CBaseDataReference#getReference()}), so
 * the {@code semantic.forms.CEntityFieldColor=valueReferenceEntity} runtime
 * binding reproduces the legacy output exactly: {@code valueReferenceEntity}
 * renders {@code <entity.reference>}, i.e. the owner field reference. No
 * formatting, export or reference resolution happens in the semantic node — the
 * template only reads {@code entity.*} properties, per the architecture
 * principle. The binding lives in {@code semantic-runtime-bindings.properties}
 * (the sanctioned BMS forms island manifest), never in the concrete manifest
 * that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree.
 *
 * <p>The legacy {@code ExportWriteAccessorTo} ({@code moveColor(...)}) had no
 * live consumer in the recursive pipeline: {@code LegacyDataRenderer.renderWriteAccessor}
 * is only invoked from the FPac accessor backend (the FPac factory's
 * {@code NewEntityFieldColor} throws {@code NacaTransAssertException}, so no FPac
 * tree ever holds a color entity) and from {@code CJavaFormAccessor}, which
 * delegates to its owner form, never to a field-color entity. The COBOL pipeline
 * lowers a data-reference MOVE onto a color pseudo-variable through
 * {@code recursiveAssignWithAccessorEntity} (unsupported marker branch), which
 * never calls the legacy write-accessor protocol. So it is retired without a
 * replacement, with no behavior change.
 */
class FieldColorRenderTest
{
    private static final String OWNER_REFERENCE = "MAP.WS-FIELD-C";

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

    private static String render(CEntityFieldColor fieldColor)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(fieldColor, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("field-color reference renders the owner field reference (legacy ExportReference parity)")
    void referenceRendersOwnerField()
    {
        CEntityFieldColor fieldColor =
            new CEntityFieldColor(1, "WS-FIELD-C", catalog(), owner());
        // valueReferenceEntity renders <entity.reference>: the owner field,
        // exactly what CJavaFieldColor.ExportReference delegated to.
        assertEquals(OWNER_REFERENCE, render(fieldColor).trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntityFieldColor fieldColor =
            factory.NewEntityFieldColor(1, "WS-FIELD-C", owner());
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityFieldColor.class, fieldColor.getClass());
        assertEquals(OWNER_REFERENCE, render(fieldColor).trim());
    }

    @Test
    @DisplayName("the legacy data-renderer bridge falls through to the recursive assembler")
    void legacyBridgeFallsThroughToAssembler()
    {
        CEntityFieldColor fieldColor =
            new CEntityFieldColor(1, "WS-FIELD-C", catalog(), owner());
        // Production reference rendering enters through LegacyDataRenderer: the
        // reflective ExportReference lookup finds no generate.* override on the
        // pure semantic entity, so it falls through to renderRoot(REFERENCE).
        String bridged = LegacyDataRenderer.renderReference(fieldColor, 1);
        assertEquals(OWNER_REFERENCE, bridged.trim());
        assertEquals(render(fieldColor).trim(), bridged.trim());
    }

    @Test
    @DisplayName("the retired backend's semantic predicates are preserved on the pure entity")
    void semanticPredicatesPreserved()
    {
        MockDataEntity ownerField = owner();
        CEntityFieldColor fieldColor =
            new CEntityFieldColor(1, "WS-FIELD-C", catalog(), ownerField);
        assertTrue(fieldColor.HasAccessors());
        assertTrue(fieldColor.isValNeeded());
        assertEquals(CDataEntity.CDataEntityType.FIELD_ATTRIBUTE, fieldColor.GetDataType());
        assertSame(ownerField, fieldColor.getReference());
    }
}
