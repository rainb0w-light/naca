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
import semantic.forms.CEntityFieldColor;
import semantic.forms.CEntitySetColor;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource color action ({@code MOVE <val> TO <FIELD>-C}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaSetColor} emitted,
 * from its {@code DoExport}, one protected {@code OnlineProgram.moveColor(<color>,
 * <field>)} call selected from exactly two semantic slots, with this branch
 * precedence: a set color constant emitted {@code moveColor(MapFieldAttrColor.<name>,
 * <field>) ;}, else a moved color variable emitted {@code moveColor(<variable>,
 * <field>) ;}, else the fall-through emitted {@code moveColor(MapFieldAttrColor.NEUTRAL,
 * <field>) ;}. This slice de-abstracts the pure semantic entity
 * {@link semantic.forms.CEntitySetColor} with read-only getters over those slots
 * ({@code getField()}, {@code getColorVariable()}, {@code getColorConstant()}) and
 * binds it through the sanctioned BMS forms-island manifest
 * {@code semantic-runtime-bindings.properties} (never the concrete manifest that
 * {@code FinalArchitectureContractTest} pins to the COBOL/SQL/CICS tree) to the
 * {@code recursiveSetColorEntity} template, which reads only {@code entity.*}
 * properties: {@code <entity.field>} and {@code <entity.colorVariable>} are data
 * references that unfold recursively through the assembler, and the color constant
 * name is a pure entity getter (a static {@code nacaLib.mapSupport.MapFieldAttrColor}
 * field reference). The emitted calls are contracted as operations
 * {@code bms.color.move.attr} / {@code bms.color.move.edit} / {@code bms.color.move.var}
 * (the three protected {@code moveColor} runtime overloads; runtime-operations.yaml,
 * template-runtime-requirements.yaml, feature {@code bms.color}).
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldColor.GetSpecialAssignment}
 * is the parser-driven construction of this entity ({@code MOVE "<code>" TO <FIELD>-C}
 * maps a BMS color code to its {@code CFieldColor} constant — "1"=BLUE, "2"=RED,
 * "3"=PINK, "4"=GREEN, "5"=TURQUOISE, "6"=YELLOW, "7"=NEUTRAL — and an unrecognized
 * code falls through to NEUTRAL; {@code MOVE <attr> TO <FIELD>-C} moves a color
 * attribute variable). It now builds the pure entity through the rewired factory
 * ({@code CJavaEntityFactory.NewEntitySetColor}, inherited by
 * {@code CJavaEntityFactoryST}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 *
 * <p>This slice also repairs two latent self-assignment defects the retired backend
 * masked: the {@code CEntitySetColor} constructor stored {@code field = field}
 * (never assigning the field slot) and {@code ReplaceVariable} compared/assigned the
 * shadowing parameter instead of {@code this.field}; both now use {@code this.field}
 * so the field reference reaches the template.
 */
class SetColorRenderTest
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

    private static String render(CEntitySetColor setColor)
    {
        // Statements unfold through the assembler exactly as the procedure
        // templates render their activeChildren: REFERENCE role, resolved by the
        // runtime manifest binding semantic.forms.CEntitySetColor ->
        // recursiveSetColorEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(setColor, JavaTemplateRole.REFERENCE);
    }

    private static CEntityFieldColor fieldColor()
    {
        return new CEntityFieldColor(1, "WS-FIELD-C", catalog(), owner());
    }

    /**
     * The production analysis path for a color code: {@code MOVE "<code>" TO
     * <FIELD>-C} drives
     * {@link CEntityFieldColor#GetSpecialAssignment(parser.expression.CTerminal, semantic.CBaseEntityFactory, int)},
     * which builds the color action around a {@link CEntityFieldAttributeReference}
     * to the owner field.
     */
    private static CEntitySetColor lowerColorCode(
        String code, CJavaEntityFactoryST factory)
    {
        CBaseActionEntity action =
            fieldColor().GetSpecialAssignment(new MockTerminal(code), factory, 7);
        return assertInstanceOf(CEntitySetColor.class, action);
    }

    @Test
    @DisplayName("a color code renders moveColor(MapFieldAttrColor.<name>, field) (legacy DoExport parity)")
    void colorCodeRendersMoveColorConstant()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // "2" lowers to the RED color constant.
        CEntitySetColor setColor = lowerColorCode("2", factory);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetColor.class, setColor.getClass());
        // The field slot is the production attribute reference wrapping the owner.
        assertInstanceOf(CEntityFieldAttributeReference.class, setColor.getField());
        assertNull(setColor.getColorVariable());
        assertEquals("MapFieldAttrColor.RED", setColor.getColorConstant());
        assertEquals("moveColor(MapFieldAttrColor.RED, " + OWNER_REFERENCE + ") ;",
            render(setColor).trim());
    }

    @Test
    @DisplayName("every BMS color code renders its MapFieldAttrColor constant")
    void everyColorCodeRendersItsConstant()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        String[] codes = { "1", "2", "3", "4", "5", "6", "7" };
        String[] names = { "BLUE", "RED", "PINK", "GREEN", "TURQUOISE", "YELLOW", "NEUTRAL" };
        for (int i = 0; i < codes.length; i++)
        {
            CEntitySetColor setColor = lowerColorCode(codes[i], factory);
            assertEquals(CEntitySetColor.class, setColor.getClass());
            assertEquals("MapFieldAttrColor." + names[i], setColor.getColorConstant(),
                "color constant for code " + codes[i]);
            assertEquals(
                "moveColor(MapFieldAttrColor." + names[i] + ", " + OWNER_REFERENCE + ") ;",
                render(setColor).trim());
        }
    }

    @Test
    @DisplayName("an unrecognized color code falls through to moveColor(MapFieldAttrColor.NEUTRAL, field)")
    void unrecognizedColorCodeRendersNeutral()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // "9" (and "") map to no CFieldColor: the legacy DoExport else-branch emits
        // the NEUTRAL constant rather than dropping the statement.
        CEntitySetColor setColor = lowerColorCode("9", factory);
        assertEquals(CEntitySetColor.class, setColor.getClass());
        assertNull(setColor.getColorVariable());
        assertEquals("MapFieldAttrColor.NEUTRAL", setColor.getColorConstant());
        assertEquals("moveColor(MapFieldAttrColor.NEUTRAL, " + OWNER_REFERENCE + ") ;",
            render(setColor).trim());
    }

    @Test
    @DisplayName("a moved color attribute renders moveColor(variable, field) (production lowering)")
    void movedColorVariableRendersMoveColor()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // MOVE <attr> TO <FIELD>-C drives GetSpecialAssignment(CDataEntity) with a
        // FIELD_ATTRIBUTE source, selecting the moved-variable branch.
        CBaseActionEntity action = fieldColor().GetSpecialAssignment(
            new ColorAttributeEntity("WS-OTHER-C"), factory, 8);

        CEntitySetColor setColor = assertInstanceOf(CEntitySetColor.class, action);
        assertEquals(CEntitySetColor.class, setColor.getClass());
        assertNull(setColor.getColorConstant());
        assertInstanceOf(CDataEntity.class, setColor.getColorVariable());
        assertEquals("moveColor(WS-OTHER-C, " + OWNER_REFERENCE + ") ;",
            render(setColor).trim());
    }

    @Test
    @DisplayName("a non-color MOVE source lowers to no color action")
    void nonColorSourceLowersToNothing()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // A plain (non FIELD_ATTRIBUTE) source is rejected by the legacy
        // GetSpecialAssignment(CDataEntity) guard, which returns null.
        assertNull(fieldColor().GetSpecialAssignment(owner(), factory, 9));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        MockDataEntity field = owner();

        CEntitySetColor setColor = factory.NewEntitySetColor(1, field);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetColor.class, setColor.getClass());
        // The repaired constructor stores the field slot (legacy self-assignment
        // left it null); it reaches the template as the rendered field reference.
        assertSame(field, setColor.getField());
        // Bare factory construction: no color constant and no moved variable -> the
        // NEUTRAL fall-through, exactly the legacy DoExport else-branch.
        assertEquals("moveColor(MapFieldAttrColor.NEUTRAL, " + OWNER_REFERENCE + ") ;",
            render(setColor).trim());
    }

    @Test
    @DisplayName("the retired backend's branch precedence is preserved")
    void legacyBranchPrecedencePreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySetColor setColor = factory.NewEntitySetColor(1, owner());

        // Legacy DoExport tested the color constant first: even with a moved color
        // variable already set, a color constant selects the constant branch.
        setColor.SetColor(new ColorAttributeEntity("WS-OTHER-C"));
        setColor.SetColor(CEntityFieldColor.CFieldColor.BLUE);
        assertEquals("MapFieldAttrColor.BLUE", setColor.getColorConstant());
        assertEquals("moveColor(MapFieldAttrColor.BLUE, " + OWNER_REFERENCE + ") ;",
            render(setColor).trim());

        // Without a color constant the moved variable selects the variable branch.
        CEntitySetColor moved = factory.NewEntitySetColor(1, owner());
        moved.SetColor(new ColorAttributeEntity("WS-OTHER-C"));
        assertNull(moved.getColorConstant());
        assertEquals("moveColor(WS-OTHER-C, " + OWNER_REFERENCE + ") ;",
            render(moved).trim());
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());

        // No field reference -> ignored (never rendered).
        CEntitySetColor noField = factory.NewEntitySetColor(1, null);
        assertTrue(noField.ignore());

        // An ignored color variable -> ignored (legacy second clause).
        CEntitySetColor ignoredValue = factory.NewEntitySetColor(1, owner());
        ignoredValue.SetColor(new IgnoredDataEntity());
        assertTrue(ignoredValue.ignore());

        // A live field and a live color constant -> rendered.
        CEntitySetColor live = factory.NewEntitySetColor(1, owner());
        live.SetColor(CEntityFieldColor.CFieldColor.GREEN);
        assertFalse(live.ignore());
    }

    /** A FIELD_ATTRIBUTE data reference standing in for a moved color attribute. */
    private static final class ColorAttributeEntity extends MockDataEntity
    {
        private ColorAttributeEntity(String referenceValue)
        {
            super(2, referenceValue);
        }

        @Override
        public CDataEntityType GetDataType()
        {
            return CDataEntityType.FIELD_ATTRIBUTE;
        }
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
