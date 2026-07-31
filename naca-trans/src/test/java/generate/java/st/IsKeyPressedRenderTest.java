package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityGetKeyPressed;
import semantic.forms.CEntityIsKeyPressed;
import semantic.forms.CEntityKeyPressed;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource CICS AID-key test condition ({@code IF KEYPRESSED = <key>} /
 * {@code <> <key>}) rendering through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaIsKeyPressed} carried a single
 * output protocol: {@code Export() == "is" + (bIsNot ? "Not" : "") + "KeyPressed(" +
 * renderReference(keyPressed, line) + ")"} — i.e. {@code isKeyPressed(KeyPressed.PF1)} or
 * {@code isNotKeyPressed(KeyPressed.PF1)}. The two forms are two distinct protected
 * {@code nacaLib.basePrgEnv.BaseProgram} condition calls
 * ({@code isKeyPressed(nacaLib.misc.KeyPressed)} / {@code isNotKeyPressed(...)}), selected by the
 * {@code bIsNot} flag — not a {@code !(...)} wrapping. This slice de-abstracts the pure semantic
 * entity {@link semantic.forms.CEntityIsKeyPressed} (implementing the inherited
 * {@code GetPriorityLevel() == 7} / {@code GetOppositeCondition()} protocols and exposing the pure
 * read-only getters {@code isOpposite()} — the precomputed {@code bIsNot} flag — and
 * {@code getKeyPressed()} — the already-resolved console-key sub-entity) and binds it through the
 * sanctioned BMS forms-island manifest {@code semantic-runtime-bindings.properties} (never the
 * concrete manifest that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to
 * the {@code recursiveIsKeyPressedEntity} template. The template branches on {@code entity.opposite}
 * to select the {@code is}/{@code isNot} call and reads {@code entity.keyPressed}, which unfolds
 * recursively through the assembler (REFERENCE role -&gt; {@code recursiveKeyPressedEntity} -&gt;
 * {@code KeyPressed.<constant>}) — byte-for-byte the retired backend's Export. Both runtime calls
 * are contracted as {@code bms.keyPressed.is} / {@code bms.keyPressed.isNot}
 * (runtime-operations.yaml) and required by the template (template-runtime-requirements.yaml).
 *
 * <p>Production construction: {@code CEntityGetKeyPressed.GetSpecialCondition} (the parser-driven
 * lowering of a comparison of the get-key-pressed pseudo-variable against a console key) calls
 * {@code factory.NewEntityIsKeyPressed()} (which fires the {@code addImportDeclaration("KEYPRESSED")}
 * side effect) and populates {@code isKeyPressed(key)} / {@code isNotKeyPressed(key)}; the rewired
 * {@code BmsJavaEntities.isKeyPressed} now builds the pure {@link CEntityIsKeyPressed} (the
 * {@code CJavaEntityFactoryST} production factory inherits {@code NewEntityIsKeyPressed}; the FPac
 * factory throws {@code NacaTransAssertException}, so no FPac tree ever holds it).
 *
 * <p>Production consumption: the condition renders through the recursive assembler in the REFERENCE
 * role exactly as the program root's {@code <entity.condition>} does — the
 * {@code semantic.CEntityCondition.getCondition()} sub-entity unfolds through the model adaptor,
 * which resolves this slice's binding. Before this slice the condition was dead wiring: constructable
 * through the factory but carrying no binding, so any program actually rendering it would have failed
 * closed in the assembler; the backend's {@code Export} was only ever exercised by hand-built tests.
 */
class IsKeyPressedRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String renderCondition(CBaseEntityCondition condition)
    {
        // The condition unfolds through the assembler exactly as the program root's
        // <entity.condition> does: REFERENCE role, resolved by the forms-island manifest
        // binding semantic.forms.CEntityIsKeyPressed -> recursiveIsKeyPressedEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production construction path: {@code CEntityGetKeyPressed.GetSpecialCondition} calls
     * {@code factory.NewEntityIsKeyPressed()} and populates it; the rewired factory must build the
     * pure semantic entity, not the retired {@code CJava*} subclass.
     */
    private static CBaseEntityCondition lowerCondition(
        CJavaEntityFactoryST factory, CBaseEntityCondition.EConditionType type)
    {
        CEntityKeyPressed key = factory.NewEntityKeyPressed("PF1", "PF1");
        CEntityGetKeyPressed getKeyPressed = factory.NewEntityGetKeyPressed("KEYPRESSED");
        return getKeyPressed.GetSpecialCondition(0, key, type, factory);
    }

    @Test
    @DisplayName("factory.NewEntityIsKeyPressed builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        CEntityIsKeyPressed entity = factory.NewEntityIsKeyPressed();

        // Exactly the pure semantic class, not the retired CJavaIsKeyPressed backend subclass.
        assertEquals(CEntityIsKeyPressed.class, entity.getClass());
    }

    @Test
    @DisplayName("IF KEYPRESSED = <key> renders isKeyPressed(KeyPressed.<key>) through the recursive assembler")
    void positiveConditionRendersThroughRecursiveAssembler()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        // Byte-for-byte the retired backend's Export for the positive test.
        CBaseEntityCondition condition =
            lowerCondition(factory, CBaseEntityCondition.EConditionType.IS_EQUAL);
        assertEquals("isKeyPressed(KeyPressed.PF1)", renderCondition(condition));
    }

    @Test
    @DisplayName("IF KEYPRESSED <> <key> renders isNotKeyPressed(KeyPressed.<key>) through the recursive assembler")
    void negatedConditionRendersThroughRecursiveAssembler()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        // Byte-for-byte the retired backend's Export for the negated test (a distinct
        // BaseProgram.isNotKeyPressed call, not a !(isKeyPressed(...)) wrapping).
        CBaseEntityCondition condition =
            lowerCondition(factory, CBaseEntityCondition.EConditionType.IS_DIFFERENT);
        assertEquals("isNotKeyPressed(KeyPressed.PF1)", renderCondition(condition));
    }

    @Test
    @DisplayName("GetOppositeCondition flips the is/isNot call and re-renders through the assembler")
    void oppositeConditionFlipsTheCall()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        CBaseEntityCondition condition =
            lowerCondition(factory, CBaseEntityCondition.EConditionType.IS_EQUAL);
        assertEquals("isKeyPressed(KeyPressed.PF1)", renderCondition(condition));

        // The inherited GetOppositeCondition (relocated to the pure entity) negates bIsNot and
        // re-registers the value access; the opposite renders the isNot form around the same key.
        CBaseEntityCondition opposite = condition.GetOppositeCondition();
        assertEquals("isNotKeyPressed(KeyPressed.PF1)", renderCondition(opposite));
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's condition protocols")
    void preservesLegacyConditionProtocols()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());

        CBaseEntityCondition condition =
            lowerCondition(factory, CBaseEntityCondition.EConditionType.IS_EQUAL);
        CEntityIsKeyPressed entity = assertInstanceOf(CEntityIsKeyPressed.class, condition);

        // Preserved from the retired backend: a priority-7 binary condition, never ignored, with
        // no condition reference of its own (the console key rides in the keyPressed slot).
        assertEquals(7, entity.GetPriorityLevel());
        assertTrue(entity.isBinaryCondition());
        assertFalse(entity.ignore());
        assertNull(entity.GetConditionReference());
        assertFalse(entity.isOpposite());
        assertEquals("KeyPressed.PF1",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(entity.getKeyPressed(), JavaTemplateRole.REFERENCE));
    }
}
