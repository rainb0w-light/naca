package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.LegacyDataRenderer;
import generate.java.forms.CJavaIsKeyPressed;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityGetKeyPressed;
import semantic.forms.CEntityKeyPressed;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource CICS get-key-pressed pseudo-variable reference rendering through the
 * recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaGetKeyPressed} carried two
 * output protocols. Its reference protocol {@code ExportReference(nLine) == "getKeyPressed()"}
 * is a no-argument program call (protected {@code BaseProgram.getKeyPressed()}, inherited by
 * generated program subclasses) — unlike the sibling {@code CJavaKeyPressed}'s static
 * constant reference. This slice de-abstracts the pure semantic entity
 * {@link semantic.forms.CEntityGetKeyPressed} (preserving the backend's
 * {@code isValNeeded() == false} protocol and its {@code GetSpecialCondition}/
 * {@code GetSpecialAssignment} lowerings) and binds it through the sanctioned BMS
 * forms-island manifest {@code semantic-runtime-bindings.properties} (never the concrete
 * manifest that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to the
 * {@code recursiveGetKeyPressedEntity} template, which emits the bare {@code getKeyPressed()}
 * call — byte-for-byte the retired backend's ExportReference. Because this is a runtime method
 * call (not a static constant), it is contracted as {@code bms.keyPressed.get}
 * (runtime-operations.yaml) and required by the template (template-runtime-requirements.yaml).
 *
 * <p>The reference is dead wiring in production: every real use of the pseudo-variable lowers
 * through {@code GetSpecialCondition}/{@code GetSpecialAssignment} into
 * {@code CEntityIsKeyPressed}/{@code CEntityResetKeyPressed}, so {@code getKeyPressed()} has no
 * live caller; it is preserved for completeness and proven here to still render through the
 * binding. The backend's {@code ExportWriteAccessorTo(value) == "setKeyPressed("+value+") ;"}
 * write accessor had no live consumer — {@code LegacyDataRenderer.renderWriteAccessor} is only
 * reached from the FPac accessor backend, whose factory throws {@code NacaTransAssertException}
 * for this BMS-only entity — and retired without a replacement (the reflective lookup now finds
 * no {@code generate.*} method and returns null).
 *
 * <p>Production construction: {@code CJavaEntityFactory.NewEntityGetKeyPressed} (inherited by
 * the {@code CJavaEntityFactoryST} production factory) calls {@code BmsJavaEntities.getKeyPressed},
 * which now builds the pure {@link CEntityGetKeyPressed}. Production consumption of the lowering:
 * {@code CEntityGetKeyPressed.GetSpecialCondition} builds the still-direct
 * {@code CJavaIsKeyPressed} condition whose {@code Export} renders the inner console-key
 * reference through the sibling {@code recursiveKeyPressedEntity} binding. The end-to-end test
 * drives that exact parser-driven lowering through the ST factory.
 */
class GetKeyPressedRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntityGetKeyPressed entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityGetKeyPressed -> recursiveGetKeyPressedEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production construction path: {@code CJavaEntityFactory.NewEntityGetKeyPressed}
     * (inherited by the ST factory) calls {@code BmsJavaEntities.getKeyPressed}; the rewired
     * factory must build the pure semantic entity, not the retired {@code CJava*} subclass.
     */
    private static CEntityGetKeyPressed lowerGetKeyPressed(
        CJavaEntityFactoryST factory, String name)
    {
        return factory.NewEntityGetKeyPressed(name);
    }

    @Test
    @DisplayName("factory.NewEntityGetKeyPressed builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        CEntityGetKeyPressed entity = lowerGetKeyPressed(factory, "KEYPRESSED");

        // Exactly the pure semantic class, not the retired CJavaGetKeyPressed backend subclass.
        assertEquals(CEntityGetKeyPressed.class, entity.getClass());
    }

    @Test
    @DisplayName("get-key-pressed reference renders getKeyPressed() through the recursive assembler")
    void referenceRendersThroughRecursiveAssembler()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        // Byte-for-byte the retired backend's ExportReference: "getKeyPressed()".
        assertEquals("getKeyPressed()", render(lowerGetKeyPressed(factory, "KEYPRESSED")));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityGetKeyPressed entity = lowerGetKeyPressed(factory, "KEYPRESSED");

        // With the backend's reflective ExportReference gone, the semantic-declared path
        // finds no generate.* method and falls through to the recursive assembler binding.
        assertEquals("getKeyPressed()", LegacyDataRenderer.renderReference(entity, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("the retired setKeyPressed write accessor has no live consumer and renders null")
    void writeAccessorRetiredWithoutReplacement()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityGetKeyPressed entity = lowerGetKeyPressed(factory, "KEYPRESSED");

        // LegacyDataRenderer.renderWriteAccessor is reflection-only with no ST4 fall-through;
        // with the generate.* ExportWriteAccessorTo gone it finds no method and returns null
        // (the only caller is the FPac accessor backend, whose factory throws for this entity).
        assertNull(LegacyDataRenderer.renderWriteAccessor(entity, "KeyPressed.ENTER"));
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityGetKeyPressed entity = lowerGetKeyPressed(factory, "KEYPRESSED");

        // The backend forced isValNeeded() == false and inherited HasAccessors() == true,
        // ignore() == false and GetConstantValue() == "".
        assertFalse(entity.isValNeeded());
        assertTrue(entity.HasAccessors());
        assertFalse(entity.ignore());
        assertEquals("", entity.GetConstantValue());
    }

    @Test
    @DisplayName("end-to-end: IF get-key-pressed = <key> lowers through the ST factory to isKeyPressed(KeyPressed.<key>)")
    void endToEndConditionLowersThroughSTFactory()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        // Production construction of the console key (keyPressed rule path) and of the
        // get-key-pressed pseudo-variable (this slice's rewired factory bridge).
        CEntityKeyPressed key = factory.NewEntityKeyPressed("PF1", "PF1");
        CEntityGetKeyPressed getKeyPressed = lowerGetKeyPressed(factory, "KEYPRESSED");

        // Parser-driven lowering of a comparison against a console key
        // (CEntityGetKeyPressed.GetSpecialCondition -> factory.NewEntityIsKeyPressed ->
        // isKeyPressed(key)); pure semantic analysis building the condition sub-entity.
        CBaseEntityCondition condition = getKeyPressed.GetSpecialCondition(
            0, key, CBaseEntityCondition.EConditionType.IS_EQUAL, factory);

        // The condition is the still-direct CJavaIsKeyPressed backend (a separate future
        // retirement item); its Export renders the inner console-key reference through the
        // sibling recursiveKeyPressedEntity binding via LegacyDataRenderer.renderReference.
        CJavaIsKeyPressed isKeyPressed = assertInstanceOf(CJavaIsKeyPressed.class, condition);
        assertEquals("isKeyPressed(KeyPressed.PF1)", isKeyPressed.Export());

        // IS_DIFFERENT lowers the negated condition around the same reference.
        CBaseEntityCondition notCondition = getKeyPressed.GetSpecialCondition(
            0, key, CBaseEntityCondition.EConditionType.IS_DIFFERENT, factory);
        assertEquals("isNotKeyPressed(KeyPressed.PF1)",
            assertInstanceOf(CJavaIsKeyPressed.class, notCondition).Export());
    }
}
