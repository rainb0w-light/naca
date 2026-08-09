package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import lexer.Cobol.CCobolConstantList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.forms.CEntityGetKeyPressed;
import semantic.forms.CEntityResetKeyPressed;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource CICS reset-key-pressed action rendering through the recursive assembler
 * (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaResetKeyPressed} carried a
 * single output protocol: its {@code DoExport} wrote the line {@code resetKeyPressed();} — the
 * protected no-argument {@code nacaLib.basePrgEnv.BaseProgram.resetKeyPressed()} call. This
 * slice de-abstracts the pure semantic action {@link semantic.forms.CEntityResetKeyPressed}
 * and binds it through the sanctioned BMS forms-island manifest
 * {@code semantic-runtime-bindings.properties} (never the concrete manifest that
 * {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to the
 * {@code recursiveResetKeyPressedEntity} template, which emits the bare
 * {@code resetKeyPressed();} statement byte-for-byte as the retired backend did. Like the
 * sibling {@code CEntitySetCursor} action, the entity carries no {@code DoExport} of its own:
 * the statement renders purely through the recursive assembler's REFERENCE role. The call is
 * contracted as {@code bms.keyPressed.reset} (runtime-operations.yaml) and required by the
 * template (template-runtime-requirements.yaml).
 *
 * <p>The action is dead wiring in production: it is built only by
 * {@code CEntityGetKeyPressed.GetSpecialAssignment} for a {@code MOVE SPACE TO KEYPRESSED},
 * which no shipped BMS map-resource program performs, so {@code resetKeyPressed();} has no
 * live caller; it is preserved for completeness and proven here to still render through the
 * binding. The end-to-end test drives that exact parser-driven lowering through the ST factory.
 */
class ResetKeyPressedRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntityResetKeyPressed entity)
    {
        // The statement unfolds through the assembler exactly as the program root's executable
        // children do: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityResetKeyPressed -> recursiveResetKeyPressedEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production construction path: {@code CJavaEntityFactory.NewEntityResetKeyPressed}
     * (inherited by the ST factory) calls {@code BmsJavaEntities.resetKeyPressed}; the rewired
     * factory must build the pure semantic entity, not the retired {@code CJava*} subclass.
     */
    private static CEntityResetKeyPressed lowerResetKeyPressed(
        CJavaEntityFactory factory, int line)
    {
        return factory.NewEntityResetKeyPressed(line);
    }

    /** A minimal SPACE terminal driving the {@code GetSpecialAssignment} lowering. */
    private static CTerminal terminalWithValue(String value)
    {
        return new CTerminal()
        {
            public String GetValue()
            {
                return value;
            }
            public boolean IsReference()
            {
                return false;
            }
            public void ExportTo(Element e, Document root)
            {
                // not exercised
            }
            public CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory)
            {
                return null;
            }
            public boolean IsNumber()
            {
                return false;
            }
        };
    }

    @Test
    @DisplayName("factory.NewEntityResetKeyPressed builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        CEntityResetKeyPressed entity = lowerResetKeyPressed(factory, 0);

        // Exactly the pure semantic class, not the retired CJavaResetKeyPressed backend subclass.
        assertEquals(CEntityResetKeyPressed.class, entity.getClass());
    }

    @Test
    @DisplayName("reset-key-pressed action renders resetKeyPressed(); through the recursive assembler")
    void statementRendersThroughRecursiveAssembler()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // Byte-for-byte the retired backend's DoExport line: "resetKeyPressed();".
        assertEquals("resetKeyPressed();", render(lowerResetKeyPressed(factory, 0)));
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's action protocol (ignore() == false)")
    void preservesLegacyActionProtocol()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityResetKeyPressed entity = lowerResetKeyPressed(factory, 0);

        // The retired backend inherited ignore() == false; the action is never skipped.
        assertFalse(entity.ignore());
    }

    @Test
    @DisplayName("end-to-end: MOVE SPACE TO KEYPRESSED lowers through the ST factory to resetKeyPressed();")
    void endToEndAssignmentLowersThroughSTFactory()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        // Production construction of the get-key-pressed pseudo-variable (the sibling slice's
        // rewired factory bridge); its GetSpecialAssignment lowering builds this slice's action.
        CEntityGetKeyPressed getKeyPressed = factory.NewEntityGetKeyPressed("KEYPRESSED");

        // Parser-driven lowering of an assignment of SPACE
        // (CEntityGetKeyPressed.GetSpecialAssignment -> factory.NewEntityResetKeyPressed);
        // pure semantic analysis building the action sub-entity.
        CBaseActionEntity action = getKeyPressed.GetSpecialAssignment(
            terminalWithValue(CCobolConstantList.SPACE.name), factory, 0);

        // The action is the pure semantic.forms.CEntityResetKeyPressed (the
        // generate.java.forms.CJavaResetKeyPressed backend was retired onto the
        // recursiveResetKeyPressedEntity binding); it renders through the recursive assembler.
        CEntityResetKeyPressed reset = assertInstanceOf(CEntityResetKeyPressed.class, action);
        assertEquals("resetKeyPressed();", render(reset));

        // Any other assigned value does not lower to the reset action (no silent alternative).
        assertNull(getKeyPressed.GetSpecialAssignment(
            terminalWithValue("OTHER"), factory, 0));
    }
}
