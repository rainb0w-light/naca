package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import generate.CJavaEntityFactory;
import generate.LegacyDataRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityGetKeyPressed;
import semantic.forms.CEntityIsKeyPressed;
import semantic.forms.CEntityKeyPressed;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource CICS AID-key pseudo-variable ({@code keyPressed} transcoder rules)
 * reference rendering through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaKeyPressed} had exactly
 * one live output protocol: {@code ExportReference(nLine) == "KeyPressed." + csPublicName}
 * — a static constant reference into {@code nacaLib.misc.KeyPressed} (e.g.
 * {@code KeyPressed.PF1}), not a runtime method call. This slice de-abstracts the pure
 * semantic entity {@link semantic.forms.CEntityKeyPressed} (precomputing the rule
 * {@code keyName} as {@code csPublicName}, exposing the pure read-only getter
 * {@code getPublicName()} and preserving the backend's {@code HasAccessors() == false} /
 * {@code isValNeeded() == false} protocols) and binds it through the sanctioned BMS
 * forms-island manifest {@code semantic-runtime-bindings.properties} (never the concrete
 * manifest that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to
 * the {@code recursiveKeyPressedEntity} template, which reads only {@code entity.publicName}
 * and prepends the target-specific {@code KeyPressed.} qualifier — byte-for-byte the
 * retired backend's ExportReference. No runtime operation is contracted (a static constant
 * reference, same treatment as {@code fieldValidatedReferenceEntity}'s legacy shape).
 *
 * <p>Production construction: {@code CobolTranscoderEngine} reads the {@code keyPressed}
 * rules ({@code keyName} -> constant, {@code CICSAlias} -> name) and calls
 * {@code factory.NewEntityKeyPressed(alias, key)}; the rewired
 * {@code BmsJavaEntities.keyPressed} now builds the pure {@link CEntityKeyPressed} (the
 * {@code CJavaEntityFactory} production factory inherits {@code NewEntityKeyPressed};
 * the FPac factory throws {@code NacaTransAssertException}, so no FPac tree ever holds it).
 *
 * <p>Production consumption: the reference is read through the recursive assembler — e.g. the
 * AID-key test condition that {@code CEntityGetKeyPressed.GetSpecialCondition} lowers from a
 * comparison of the get-key-pressed pseudo-variable against a console key (a pure
 * {@code semantic.forms.CEntityIsKeyPressed}, since the {@code generate.java.forms.CJavaIsKeyPressed}
 * backend was retired onto the {@code recursiveIsKeyPressedEntity} binding) unfolds its inner
 * console-key sub-entity through this slice's {@code recursiveKeyPressedEntity} binding. With the
 * backend's reflective {@code ExportReference} gone, {@code LegacyDataRenderer.renderReference}
 * falls through to the recursive assembler (REFERENCE role), which resolves the new binding. The
 * end-to-end test drives that exact parser-driven lowering and asserts the inner reference renders
 * through this slice's template.
 */
class KeyPressedRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntityKeyPressed entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityKeyPressed -> recursiveKeyPressedEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    private static String renderCondition(CBaseEntityCondition condition)
    {
        // Conditions render through the assembler in the REFERENCE role exactly as the
        // program root's <entity.condition> does: resolved by the forms-island manifest
        // binding semantic.forms.CEntityIsKeyPressed -> recursiveIsKeyPressedEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production construction path: {@code CobolTranscoderEngine}'s {@code keyPressed}
     * rule loop calls {@code factory.NewEntityKeyPressed(CICSAlias, keyName)}; the rewired
     * factory must build the pure semantic entity, not a legacy {@code CJava*} subclass.
     */
    private static CEntityKeyPressed lowerKeyPressed(
        CJavaEntityFactory factory, String alias, String keyName)
    {
        return factory.NewEntityKeyPressed(alias, keyName);
    }

    @Test
    @DisplayName("factory.NewEntityKeyPressed builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        CEntityKeyPressed entity = lowerKeyPressed(factory, "PF1", "PF1");

        // Exactly the pure semantic class, not the retired CJavaKeyPressed backend subclass.
        assertEquals(CEntityKeyPressed.class, entity.getClass());
        assertEquals("PF1", entity.getPublicName());
        assertEquals(CDataEntity.CDataEntityType.CONSOLE_KEY, entity.GetDataType());
    }

    @Test
    @DisplayName("KeyPressed reference renders KeyPressed.<key> through the recursive assembler")
    void referenceRendersThroughRecursiveAssembler()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // Byte-for-byte the retired backend's ExportReference: "KeyPressed." + csPublicName.
        assertEquals("KeyPressed.PF1", render(lowerKeyPressed(factory, "PF1", "PF1")));
        assertEquals("KeyPressed.ENTER", render(lowerKeyPressed(factory, "ENTER", "ENTER")));
        assertEquals("KeyPressed.PF12", render(lowerKeyPressed(factory, "PF12", "PF12")));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityKeyPressed entity = lowerKeyPressed(factory, "PF3", "PF3");

        // The LegacyDataRenderer.renderReference compatibility boundary: with the retired
        // backend's reflective ExportReference gone, the semantic-declared path returns null
        // and falls through to the recursive assembler binding (the same binding the
        // recursiveIsKeyPressedEntity condition template reaches through the model adaptor).
        assertEquals("KeyPressed.PF3", LegacyDataRenderer.renderReference(entity, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("end-to-end: IF get-key-pressed = <key> renders isKeyPressed(KeyPressed.<key>)")
    void endToEndConditionRendersInnerReferenceThroughTemplate()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // Production construction of the console key (keyPressed rule path).
        CEntityKeyPressed key = lowerKeyPressed(factory, "PF1", "PF1");
        // Production construction of the get-key-pressed pseudo-variable.
        CEntityGetKeyPressed getKeyPressed = factory.NewEntityGetKeyPressed("KEYPRESSED");

        // Parser-driven lowering of a comparison against a console key
        // (CEntityGetKeyPressed.GetSpecialCondition -> factory.NewEntityIsKeyPressed ->
        // isKeyPressed(key)); this also fires the addImportDeclaration("KEYPRESSED")
        // side effect through CEntityKeyPressed.RegisterValueAccess.
        CBaseEntityCondition condition = getKeyPressed.GetSpecialCondition(
            0, key, CBaseEntityCondition.EConditionType.IS_EQUAL, factory);

        // The condition is the pure semantic.forms.CEntityIsKeyPressed (the
        // generate.java.forms.CJavaIsKeyPressed backend was retired onto the
        // recursiveIsKeyPressedEntity binding); it renders through the recursive assembler,
        // unfolding the inner console-key reference through THIS slice's
        // recursiveKeyPressedEntity binding.
        CEntityIsKeyPressed isKeyPressed = assertInstanceOf(CEntityIsKeyPressed.class, condition);
        assertEquals("isKeyPressed(KeyPressed.PF1)", renderCondition(isKeyPressed));

        // IS_DIFFERENT lowers the negated condition around the same reference.
        CBaseEntityCondition notCondition = getKeyPressed.GetSpecialCondition(
            0, key, CBaseEntityCondition.EConditionType.IS_DIFFERENT, factory);
        assertEquals("isNotKeyPressed(KeyPressed.PF1)",
            renderCondition(assertInstanceOf(CEntityIsKeyPressed.class, notCondition)));
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityKeyPressed entity = lowerKeyPressed(factory, "PF1", "PF1");

        // A console key is a pseudo-constant reference: never a declared val, never an
        // accessor-bearing variable, never ignored, and not a literal constant.
        assertFalse(entity.HasAccessors());
        assertFalse(entity.isValNeeded());
        assertFalse(entity.ignore());
        assertEquals("", entity.GetConstantValue());
        // No write-accessor protocol is reachable (renderWriteAccessor finds no
        // generate.* ExportWriteAccessorTo override on the pure entity).
        assertNull(LegacyDataRenderer.renderWriteAccessor(entity, "X"));
    }
}
