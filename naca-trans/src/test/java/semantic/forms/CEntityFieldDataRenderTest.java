package semantic.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaFieldData}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityFieldData} (BMS is a CICS screen-map DSL, never a COBOL dialect).
 *
 * <p>This is the <b>dead-wiring</b> tier (the same tier as {@code CEntityLabelField} /
 * {@code CEntityResourceFieldArray}): the entity is factory-wired through
 * {@code CBaseEntityFactory.NewEntityFieldData} but has no live production caller —
 * {@code NewEntityFieldData} is reached only from
 * {@link CEntityFieldData#GetArrayReference(java.util.Vector, semantic.CBaseEntityFactory)}, so
 * building a {@code CEntityFieldData} requires one to already exist; no BMS parser node
 * bootstraps it and {@code ONLINM1.bms} never produces one. It emits no Java and no XML of its
 * own (the retired backend's {@code DoExport} was empty), so there is no ST4 template/binding for
 * it. This test therefore pins the semantic-entity contract and the production construction path
 * (the factory method), not a parser-node lowering:
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactory.NewEntityFieldData} (the BMS
 *       production factory path, via {@code BmsJavaEntities.fieldData}) builds exactly the pure
 *       semantic entity for BOTH the direct and the ST4 factory, not a
 *       {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>data-entity protocol parity</b> — {@code GetDataType() == FIELD},
 *       {@code HasAccessors() == false}, {@code isValNeeded() == true},
 *       {@code ExportWriteAccessorTo -> ""}, all preserved from the retired backend;</li>
 *   <li><b>reference rendering without generate coupling</b> — the retired backend's single
 *       generate-layer call ({@code LegacyDataRenderer.renderReference(reference, getLine())},
 *       the owner field's reference) is supplied by a neutral {@code BiFunction} the
 *       generate-layer factory injects; a hand-built entity falls back to the owner's raw name
 *       (or {@code [UNDEFINED]} when unset), mirroring {@code LegacyDataRenderer.renderReference}'s
 *       null handling, and {@code semantic.forms.CEntityFieldData} carries no {@code generate.*}
 *       token.</li>
 * </ul>
 */
class CEntityFieldDataRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    @Test
    @DisplayName("factory.NewEntityFieldData builds the pure semantic entity (both factories)")
    void factoryReturnsPureSemanticEntity()
    {
        CEntityFieldData direct =
            new CJavaEntityFactory(catalog(), new MockJavaExporter())
                .NewEntityFieldData(1, "FLD", null);
        CEntityFieldData st4 =
            new CJavaEntityFactory(catalog(), null)
                .NewEntityFieldData(1, "FLD", null);

        // Exactly the pure semantic class, not the retired CJavaFieldData backend subclass.
        assertEquals(CEntityFieldData.class, direct.getClass());
        assertEquals(CEntityFieldData.class, st4.getClass());
    }

    @Test
    @DisplayName("data-entity protocols preserve the retired backend (FIELD / no accessors / val needed / empty write accessor)")
    void dataEntityProtocolParity()
    {
        CEntityFieldData entity =
            new CJavaEntityFactory(catalog(), new MockJavaExporter())
                .NewEntityFieldData(1, "FLD", null);

        assertEquals(CDataEntity.CDataEntityType.FIELD, entity.GetDataType());
        assertFalse(entity.HasAccessors());
        assertTrue(entity.isValNeeded());
        assertEquals("", TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.DECLARATION));
    }

    @Test
    @DisplayName("a hand-built entity (no factory) reads the neutral reference fallback: owner raw name, [UNDEFINED] when unset")
    void handBuiltEntityUsesNeutralFallback()
    {
        // No owner -> mirrors LegacyDataRenderer.renderReference's null handling.
        CEntityFieldData bare = new CEntityFieldData(1, "FLD", catalog(), null);
        assertEquals("[UNDEFINED]", renderReference(bare));

        // With an owner -> the owner's raw name (no formatting, no generate coupling).
        CEntityFieldData owner = new CEntityFieldData(2, "MY-OWNER", catalog(), null);
        CEntityFieldData withOwner = new CEntityFieldData(1, "FLD", catalog(), owner);
        assertEquals("MY-OWNER", renderReference(withOwner));
    }

    @Test
    @DisplayName("factory-built field data renders its owner recursively")
    void factoryBuiltReferenceRendersRecursively()
    {
        CEntityFieldData entity =
            new CJavaEntityFactory(catalog(), new MockJavaExporter())
                .NewEntityFieldData(1, "FLD", null);

        // Factory-installed renderer delegates to LegacyDataRenderer.renderReference: a null
        // owner renders to [UNDEFINED] exactly as the retired backend's generate-layer call did,
        // without any generate.* token living in semantic.forms.CEntityFieldData.
        assertEquals("[UNDEFINED]", renderReference(entity));

        CEntityFieldData owner = new CEntityFieldData(7, "OWN", catalog(), null);
        CEntityFieldData injected = new CEntityFieldData(3, "FLD", catalog(), owner);
        assertEquals("OWN", renderReference(injected));
    }

    private static String renderReference(CEntityFieldData entity)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }
}
