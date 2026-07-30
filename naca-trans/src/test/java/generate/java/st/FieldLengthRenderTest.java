package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.LegacyDataRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldLength;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-length pseudo-variable ({@code <FIELD>-L-}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaFieldLength}
 * rendered its reference by delegating to the owner field
 * ({@code ExportReference(nLine) == renderReference(reference)}). The pure
 * semantic entity {@link CEntityFieldLength} carries that owner in the inherited
 * {@code reference} slot ({@link semantic.CBaseDataReference#getReference()}), so
 * the {@code semantic.forms.CEntityFieldLength=valueReferenceEntity} runtime
 * binding reproduces the legacy output exactly: {@code valueReferenceEntity}
 * renders {@code <entity.reference>}, i.e. the owner field reference. No
 * formatting, export or reference resolution happens in the semantic node — the
 * template only reads {@code entity.*} properties, per the architecture
 * principle. The binding lives in {@code semantic-runtime-bindings.properties}
 * (the sanctioned BMS forms island manifest), never in the concrete manifest
 * that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree.
 *
 * <p>The legacy {@code ExportWriteAccessorTo} ({@code moveLength(...)}) had no
 * live consumer in the recursive pipeline ({@code LegacyDataRenderer.renderWriteAccessor}
 * is only invoked from the FPac accessor backend and {@code CJavaFormAccessor},
 * never with a field-length entity), so it is retired without a replacement.
 */
class FieldLengthRenderTest
{
    private static final String OWNER_REFERENCE = "MAP.WS-FIELD-L";

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

    private static String render(CEntityFieldLength fieldLength)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(fieldLength, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("field-length reference renders the owner field reference (legacy ExportReference parity)")
    void referenceRendersOwnerField()
    {
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        // valueReferenceEntity renders <entity.reference>: the owner field,
        // exactly what CJavaFieldLength.ExportReference delegated to.
        assertEquals(OWNER_REFERENCE, render(fieldLength).trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntityFieldLength fieldLength =
            factory.NewEntityFieldLengh(1, "WS-FIELD-L", owner());
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityFieldLength.class, fieldLength.getClass());
        assertEquals(OWNER_REFERENCE, render(fieldLength).trim());
    }

    @Test
    @DisplayName("the legacy data-renderer bridge falls through to the recursive assembler")
    void legacyBridgeFallsThroughToAssembler()
    {
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        // Production reference rendering enters through LegacyDataRenderer: the
        // reflective ExportReference lookup finds no generate.* override on the
        // pure semantic entity, so it falls through to renderRoot(REFERENCE).
        String bridged = LegacyDataRenderer.renderReference(fieldLength, 1);
        assertEquals(OWNER_REFERENCE, bridged.trim());
        assertEquals(render(fieldLength).trim(), bridged.trim());
    }

    @Test
    @DisplayName("the retired backend's semantic predicates are preserved on the pure entity")
    void semanticPredicatesPreserved()
    {
        MockDataEntity ownerField = owner();
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), ownerField);
        assertTrue(fieldLength.HasAccessors());
        assertFalse(fieldLength.isValNeeded());
        assertEquals(CDataEntity.CDataEntityType.FIELD, fieldLength.GetDataType());
        assertSame(ownerField, fieldLength.getReference());
    }
}
