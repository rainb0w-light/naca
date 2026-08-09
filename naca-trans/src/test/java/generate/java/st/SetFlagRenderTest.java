package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldAttributeReference;
import semantic.forms.CEntityFieldFlag;
import semantic.forms.CEntitySetFlag;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource flag action ({@code MOVE <val> TO <FIELD>P}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaSetFlag}
 * emitted, from its {@code DoExport}, one of the protected {@code OnlineProgram}
 * flag calls selected from exactly two semantic slots: a non-null flag value
 * emitted {@code moveFlag("<value>", <field>) ;} with the constant quoted
 * verbatim, else {@code resetFlag(<field>) ;}. This slice de-abstracts the pure
 * semantic entity {@link semantic.forms.CEntitySetFlag} with read-only getters
 * over those slots ({@code getField()}, {@code getFlagValue()},
 * {@code isMoveFlag()}) and binds it through the sanctioned BMS forms-island
 * manifest {@code semantic-runtime-bindings.properties} (never the concrete
 * manifest that {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS
 * tree) to the {@code recursiveSetFlagEntity} template, which reads only
 * {@code entity.*} properties: {@code <entity.field>} is a data reference that
 * unfolds recursively through the assembler. The emitted calls are contracted
 * as operations {@code bms.flag.move} (the {@code OnlineProgram.moveFlag(String,
 * Edit)} overload) / {@code bms.flag.reset} ({@code OnlineProgram.resetFlag(Edit)};
 * runtime-operations.yaml, template-runtime-requirements.yaml, feature
 * {@code bms.flag.set}).
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldFlag.GetSpecialAssignment}
 * is the parser-driven construction of this entity ({@code MOVE 1 TO <FIELD>P}
 * lowers to flag value "1", {@code MOVE 0}/ZERO/SPACE family to "0",
 * {@code MOVE LOW-VALUE} to the reset branch); it now builds the pure entity
 * through the rewired factory ({@code CJavaEntityFactory.NewEntitySetFlag},
 * inherited by {@code CJavaEntityFactory}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class SetFlagRenderTest
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

    private static String render(CEntitySetFlag setFlag)
    {
        // Statements unfold through the assembler exactly as the procedure
        // templates render their activeChildren: REFERENCE role, resolved by the
        // runtime manifest binding semantic.forms.CEntitySetFlag ->
        // recursiveSetFlagEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(setFlag, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production analysis path: {@code MOVE <constant> TO <FIELD>P} drives
     * {@link CEntityFieldFlag#GetSpecialAssignment(parser.expression.CTerminal, semantic.CBaseEntityFactory, int)},
     * which builds the flag action around a {@link CEntityFieldAttributeReference}
     * to the owner field.
     */
    private static CEntitySetFlag lowerMoveToFieldFlag(
        String constant, CJavaEntityFactory factory)
    {
        CEntityFieldFlag fieldFlag =
            new CEntityFieldFlag(1, "WS-FIELDP", catalog(), owner());
        CBaseActionEntity action =
            fieldFlag.GetSpecialAssignment(new MockTerminal(constant), factory, 7);
        return assertInstanceOf(CEntitySetFlag.class, action);
    }

    @Test
    @DisplayName("MOVE 1 TO <FIELD>P renders moveFlag(\"1\", field) (legacy DoExport parity)")
    void oneConstantRendersMoveFlag()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntitySetFlag setFlag = lowerMoveToFieldFlag("1", factory);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetFlag.class, setFlag.getClass());
        // The field slot is the production attribute reference wrapping the owner.
        assertInstanceOf(CEntityFieldAttributeReference.class, setFlag.getField());
        assertTrue(setFlag.isMoveFlag());
        assertEquals("1", setFlag.getFlagValue());
        assertEquals("moveFlag(\"1\", " + OWNER_REFERENCE + ") ;",
            render(setFlag).trim());
    }

    @Test
    @DisplayName("MOVE 0/ZERO/SPACE family TO <FIELD>P normalizes to moveFlag(\"0\", field)")
    void zeroConstantFamilyRendersMoveFlagZero()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        for (String zero : new String[] { "0", "ZERO", "ZEROS", "ZEROES", "SPACE", "SPACES" })
        {
            CEntitySetFlag setFlag = lowerMoveToFieldFlag(zero, factory);
            assertEquals(CEntitySetFlag.class, setFlag.getClass());
            assertEquals("0", setFlag.getFlagValue(), "normalized flag value for " + zero);
            assertTrue(setFlag.isMoveFlag());
            assertEquals("moveFlag(\"0\", " + OWNER_REFERENCE + ") ;",
                render(setFlag).trim());
        }
    }

    @Test
    @DisplayName("MOVE LOW-VALUE TO <FIELD>P renders resetFlag(field)")
    void lowValueConstantRendersResetFlag()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        for (String low : new String[] { "LOW-VALUE", "LOW-VALUES" })
        {
            CEntitySetFlag setFlag = lowerMoveToFieldFlag(low, factory);
            assertEquals(CEntitySetFlag.class, setFlag.getClass());
            assertNull(setFlag.getFlagValue());
            assertFalse(setFlag.isMoveFlag());
            assertEquals("resetFlag(" + OWNER_REFERENCE + ") ;",
                render(setFlag).trim());
        }
    }

    @Test
    @DisplayName("an unsupported constant still lowers to no flag action")
    void unsupportedConstantLowersToNothing()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityFieldFlag fieldFlag =
            new CEntityFieldFlag(1, "WS-FIELDP", catalog(), owner());
        // MOVE X TO <FIELD>P lowers to no action entity (legacy
        // GetSpecialAssignment behavior preserved — no silent drop: the parser
        // reports the unsupported assignment, the semantic tree just carries no
        // action for it, exactly as before the retirement).
        assertNull(fieldFlag.GetSpecialAssignment(new MockTerminal("X"), factory, 10));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog, new MockJavaExporter());
        MockDataEntity field = owner();

        CEntitySetFlag setFlag = factory.NewEntitySetFlag(1, field);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetFlag.class, setFlag.getClass());
        assertSame(field, setFlag.getField());
        // Bare factory construction: no flag value -> the default resetFlag
        // branch, exactly the legacy DoExport else-branch.
        assertEquals("resetFlag(" + OWNER_REFERENCE + ") ;", render(setFlag).trim());
    }

    @Test
    @DisplayName("the retired backend's branch selection is preserved")
    void legacyBranchSelectionPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog, new MockJavaExporter());
        MockDataEntity field = owner();

        // Legacy DoExport tested flagValue != null first: a set flag value
        // selects moveFlag even after a prior reset.
        CEntitySetFlag setFlag = factory.NewEntitySetFlag(1, field);
        setFlag.ResetFlag();
        setFlag.SetFlag("1");
        assertEquals("moveFlag(\"1\", " + OWNER_REFERENCE + ") ;",
            render(setFlag).trim());

        // ResetFlag clears the value -> the resetFlag branch.
        setFlag.ResetFlag();
        assertNull(setFlag.getFlagValue());
        assertEquals("resetFlag(" + OWNER_REFERENCE + ") ;", render(setFlag).trim());
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog, new MockJavaExporter());

        // No field reference -> ignored (never rendered), exactly the legacy
        // CEntitySetFlag.ignore() contract the retired backend relied on.
        CEntitySetFlag noField = factory.NewEntitySetFlag(1, null);
        assertTrue(noField.ignore());

        // A live field -> rendered.
        CEntitySetFlag live = factory.NewEntitySetFlag(1, owner());
        assertFalse(live.ignore());

        // IgnoreVariable drops the field reference -> the action becomes ignored
        // (legacy writing-action deregistration preserved).
        MockDataEntity field = owner();
        CEntitySetFlag ignored = factory.NewEntitySetFlag(1, field);
        assertTrue(ignored.IgnoreVariable(field));
        assertTrue(ignored.ignore());
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
