package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldAttributeReference;
import semantic.forms.CEntityFieldLength;
import semantic.forms.CEntitySetCursor;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource cursor action ({@code MOVE ... TO <FIELD>-L}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaSetCursor}
 * emitted, from its {@code DoExport}, one of the protected {@code OnlineProgram}
 * cursor calls selected from exactly three semantic slots, with this branch
 * precedence: a set reference value emitted {@code moveCursor(<value>, <field>) ;},
 * else the remove flag emitted {@code removeCursor(<field>) ;}, else
 * {@code setCursor(<field>) ;}. This slice de-abstracts the pure semantic entity
 * {@link semantic.forms.CEntitySetCursor} with read-only getters over those slots
 * ({@code getField()}, {@code getReferenceValue()}, {@code isMoveCursor()},
 * {@code isRemoveCursor()}) and binds it through the sanctioned BMS forms-island
 * manifest {@code semantic-runtime-bindings.properties} (never the concrete
 * manifest that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS
 * tree) to the {@code recursiveSetCursorEntity} template, which reads only
 * {@code entity.*} properties: {@code <entity.field>} and
 * {@code <entity.referenceValue>} are data references that unfold recursively
 * through the assembler. The emitted calls are contracted as operations
 * {@code bms.cursor.set} / {@code bms.cursor.remove} / {@code bms.cursor.move.edit}
 * / {@code bms.cursor.move.var} (both {@code moveCursor} runtime overload
 * families, selected by the rendered static type of the MOVE source;
 * runtime-operations.yaml, template-runtime-requirements.yaml, feature
 * {@code bms.cursor}).
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldLength.GetSpecialAssignment}
 * is the parser-driven construction of this entity ({@code MOVE x TO <FIELD>-L}
 * lowers to a moved-value cursor action, {@code MOVE -1} to the bare
 * {@code setCursor}, {@code MOVE 0}/ZERO to {@code removeCursor}); it now builds
 * the pure entity through the rewired factory ({@code CJavaEntityFactory.NewEntitySetCursor},
 * inherited by {@code CJavaEntityFactoryST}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class SetCursorRenderTest
{
    private static final String OWNER_REFERENCE = "MAP.WS-FIELD";

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

    private static String render(CEntitySetCursor setCursor)
    {
        // Statements unfold through the assembler exactly as the procedure
        // templates render their activeChildren: REFERENCE role, resolved by the
        // runtime manifest binding semantic.forms.CEntitySetCursor ->
        // recursiveSetCursorEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(setCursor, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production analysis path: {@code MOVE <value> TO <FIELD>-L} drives
     * {@link CEntityFieldLength#GetSpecialAssignment(CDataEntity, semantic.CBaseEntityFactory, int)},
     * which builds the cursor action around a {@link CEntityFieldAttributeReference}
     * to the owner field.
     */
    private static CEntitySetCursor lowerMoveToFieldLength(
        CDataEntity movedValue, CJavaEntityFactoryST factory)
    {
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        CBaseActionEntity action =
            fieldLength.GetSpecialAssignment(movedValue, factory, 7);
        return assertInstanceOf(CEntitySetCursor.class, action);
    }

    @Test
    @DisplayName("a moved value renders moveCursor(value, field) (legacy DoExport parity)")
    void movedValueRendersMoveCursor()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntitySetCursor setCursor =
            lowerMoveToFieldLength(new MockDataEntity(2, "WS-POS"), factory);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetCursor.class, setCursor.getClass());
        // The field slot is the production attribute reference wrapping the owner.
        assertInstanceOf(CEntityFieldAttributeReference.class, setCursor.getField());
        assertTrue(setCursor.isMoveCursor());
        assertFalse(setCursor.isRemoveCursor());
        assertEquals("moveCursor(WS-POS, " + OWNER_REFERENCE + ") ;",
            render(setCursor).trim());
    }

    @Test
    @DisplayName("MOVE -1 TO <FIELD>-L renders setCursor(field)")
    void minusOneConstantRendersSetCursor()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());

        CBaseActionEntity action =
            fieldLength.GetSpecialAssignment(new MockTerminal("-1"), factory, 8);

        CEntitySetCursor setCursor = assertInstanceOf(CEntitySetCursor.class, action);
        assertEquals(CEntitySetCursor.class, setCursor.getClass());
        assertNull(setCursor.getReferenceValue());
        assertFalse(setCursor.isMoveCursor());
        assertFalse(setCursor.isRemoveCursor());
        assertEquals("setCursor(" + OWNER_REFERENCE + ") ;", render(setCursor).trim());
    }

    @Test
    @DisplayName("MOVE 0/ZERO TO <FIELD>-L renders removeCursor(field)")
    void zeroConstantRendersRemoveCursor()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        for (String zero : new String[] { "0", "ZERO", "ZEROS", "ZEROES" })
        {
            CEntityFieldLength fieldLength =
                new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
            CBaseActionEntity action =
                fieldLength.GetSpecialAssignment(new MockTerminal(zero), factory, 9);
            CEntitySetCursor setCursor = assertInstanceOf(CEntitySetCursor.class, action);
            assertEquals(CEntitySetCursor.class, setCursor.getClass());
            assertTrue(setCursor.isRemoveCursor(), "remove flag for constant " + zero);
            assertFalse(setCursor.isMoveCursor());
            assertEquals("removeCursor(" + OWNER_REFERENCE + ") ;",
                render(setCursor).trim());
        }
    }

    @Test
    @DisplayName("an unsupported constant still lowers to no cursor action")
    void unsupportedConstantLowersToNothing()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());
        // MOVE 1 TO <FIELD>-L lowers to a set-attribute (modified) action, never a
        // cursor action (legacy GetSpecialAssignment behavior preserved).
        assertFalse(fieldLength.GetSpecialAssignment(new MockTerminal("1"), factory, 10)
            instanceof CEntitySetCursor);
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        MockDataEntity field = owner();

        CEntitySetCursor setCursor = factory.NewEntitySetCursor(1, field);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetCursor.class, setCursor.getClass());
        assertSame(field, setCursor.getField());
        // Bare factory construction: no reference value, no remove flag -> the
        // default setCursor branch, exactly the legacy DoExport else-branch.
        assertEquals("setCursor(" + OWNER_REFERENCE + ") ;", render(setCursor).trim());
    }

    @Test
    @DisplayName("the retired backend's branch precedence is preserved")
    void legacyBranchPrecedencePreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        MockDataEntity field = owner();
        CEntitySetCursor setCursor = factory.NewEntitySetCursor(1, field);

        // Legacy DoExport tested referenceValue first: even with the remove flag
        // set, a reference value selects moveCursor.
        setCursor.removeCursor();
        setCursor.SetReference(new MockDataEntity(2, "WS-POS"));
        assertEquals("moveCursor(WS-POS, " + OWNER_REFERENCE + ") ;",
            render(setCursor).trim());

        // Without a reference value the remove flag selects removeCursor.
        CEntitySetCursor removed = factory.NewEntitySetCursor(1, field);
        removed.removeCursor();
        assertEquals("removeCursor(" + OWNER_REFERENCE + ") ;", render(removed).trim());
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());

        // No field reference -> ignored (never rendered).
        CEntitySetCursor noField = factory.NewEntitySetCursor(1, null);
        assertTrue(noField.ignore());

        // An ignored reference value -> ignored (legacy second clause).
        CEntitySetCursor ignoredValue = factory.NewEntitySetCursor(1, owner());
        ignoredValue.SetReference(new IgnoredDataEntity());
        assertTrue(ignoredValue.ignore());

        // A live field and a live value -> rendered.
        CEntitySetCursor live = factory.NewEntitySetCursor(1, owner());
        live.SetReference(new MockDataEntity(2, "WS-POS"));
        assertFalse(live.ignore());
    }

    /** A data reference the analysis marks as ignored ({@code ignore()} true). */
    private static final class IgnoredDataEntity extends MockDataEntity
    {
        private IgnoredDataEntity()
        {
            super(2, "WS-IGNORED");
        }

        @Override
        public boolean ignore()
        {
            return true;
        }
    }

    /** Minimal terminal standing in for the lexer-produced constant token. */
    private static final class MockTerminal extends parser.expression.CTerminal
    {
        private final String value;

        private MockTerminal(String value)
        {
            this.value = value;
        }

        @Override
        public String GetValue()
        {
            return value;
        }

        @Override
        public boolean IsReference()
        {
            return false;
        }

        @Override
        public void ExportTo(org.w3c.dom.Element e, org.w3c.dom.Document root)
        {
            // not exercised by semantic analysis
        }

        @Override
        public CDataEntity GetDataEntity(int nLine, semantic.CBaseEntityFactory factory)
        {
            return null;
        }

        @Override
        public boolean IsNumber()
        {
            return false;
        }
    }
}
