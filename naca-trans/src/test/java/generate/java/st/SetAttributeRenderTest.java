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
import semantic.forms.CEntityFieldAttribute;
import semantic.forms.CEntityFieldAttributeReference;
import semantic.forms.CEntityFieldLength;
import semantic.forms.CEntitySetAttribute;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource attribute action ({@code MOVE <attr> TO <FIELD>},
 * {@code MOVE 1 TO <FIELD>-L}) rendering through the recursive assembler (the
 * production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaSetAttribute}
 * emitted, from its {@code DoExport}, one or more protected {@code OnlineProgram}
 * {@code moveAttribute(<attr>, <field>)} calls selected from the semantic slots,
 * with this precedence: a set attribute value emitted the single
 * {@code moveAttribute(<value>, <field>) ;} and returned immediately; otherwise up
 * to three calls were emitted, one per attribute group, each selecting its
 * {@code nacaLib.mapSupport} constant in the legacy else-if order — protection
 * ({@code MapFieldAttrProtection.AUTOSKIP/NUMERIC/PROTECTED/UNPROTECTED}),
 * intensity ({@code MapFieldAttrIntensity.BRIGHT/DARK/NORMAL}) and modified
 * ({@code MapFieldAttrModified.MODIFIED/UNMODIFIED}). This slice de-abstracts the
 * pure semantic entity {@link semantic.forms.CEntitySetAttribute} with read-only
 * getters over those slots ({@code getField()}, {@code getAttributeValue()},
 * {@code getProtectionConstant()}, {@code getIntensityConstant()},
 * {@code getModifiedConstant()}) and binds it through the sanctioned BMS
 * forms-island manifest {@code semantic-runtime-bindings.properties} (never the
 * concrete manifest that {@code FinalArchitectureContractTest} pins to the
 * COBOL/SQL/CICS tree) to the {@code recursiveSetAttributeEntity} template, which
 * reads only {@code entity.*} properties: {@code <entity.field>} and
 * {@code <entity.attributeValue>} are data references that unfold recursively
 * through the assembler, and the constant names are pure entity getters. The
 * emitted calls are contracted as operations {@code bms.attribute.protection} /
 * {@code bms.attribute.intensity} / {@code bms.attribute.modified} /
 * {@code bms.attribute.move.var} (the four protected {@code moveAttribute}
 * runtime overloads; runtime-operations.yaml, template-runtime-requirements.yaml,
 * feature {@code bms.attribute}).
 *
 * <p>Production lowering: {@code semantic.forms.CEntityFieldAttribute.intGetSpecialAssignment}
 * maps a single attribute character to a group combination ({@code MOVE "A" TO
 * <FIELD>} lowers to UNPROTECTED + NORMAL + MODIFIED), and
 * {@code semantic.forms.CEntityFieldLength.GetSpecialAssignment} maps {@code MOVE 1
 * TO <FIELD>-L} to the bare MODIFIED action; both build the pure entity through the
 * rewired factory ({@code CJavaEntityFactory.NewEntitySetAttribute}, inherited by
 * {@code CJavaEntityFactoryST}). The FPac factory throws
 * {@code NacaTransAssertException} for this entity, so no FPac tree ever holds it.
 */
class SetAttributeRenderTest
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

    private static String render(CEntitySetAttribute setAttribute)
    {
        // Statements unfold through the assembler exactly as the procedure
        // templates render their activeChildren: REFERENCE role, resolved by the
        // runtime manifest binding semantic.forms.CEntitySetAttribute ->
        // recursiveSetAttributeEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(setAttribute, JavaTemplateRole.REFERENCE);
    }

    /**
     * The production analysis path for a single attribute character:
     * {@code MOVE "<char>" TO <FIELD>} drives
     * {@link CEntityFieldAttribute#intGetSpecialAssignment(CDataEntity, parser.expression.CTerminal, semantic.CBaseEntityFactory, int)},
     * which builds the attribute action around a {@link CEntityFieldAttributeReference}
     * to the owner field.
     */
    private static CEntitySetAttribute lowerAttributeChar(
        String attrChar, CJavaEntityFactoryST factory)
    {
        CBaseActionEntity action = CEntityFieldAttribute.intGetSpecialAssignment(
            owner(), new MockTerminal(attrChar), factory, 7);
        return assertInstanceOf(CEntitySetAttribute.class, action);
    }

    @Test
    @DisplayName("an attribute char renders one moveAttribute per group (legacy DoExport parity)")
    void attributeCharRendersThreeGroupCalls()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // "A" lowers to UNPROTECTED + NORMAL + MODIFIED: three attribute groups,
        // three moveAttribute calls in the legacy protection/intensity/modified order.
        CEntitySetAttribute setAttribute = lowerAttributeChar("A", factory);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetAttribute.class, setAttribute.getClass());
        // The field slot is the production attribute reference wrapping the owner.
        assertInstanceOf(CEntityFieldAttributeReference.class, setAttribute.getField());
        assertNull(setAttribute.getAttributeValue());
        assertEquals("MapFieldAttrProtection.UNPROTECTED", setAttribute.getProtectionConstant());
        assertEquals("MapFieldAttrIntensity.NORMAL", setAttribute.getIntensityConstant());
        assertEquals("MapFieldAttrModified.MODIFIED", setAttribute.getModifiedConstant());
        assertEquals(
            "moveAttribute(MapFieldAttrProtection.UNPROTECTED, " + OWNER_REFERENCE + ") ;\n"
                + "moveAttribute(MapFieldAttrIntensity.NORMAL, " + OWNER_REFERENCE + ") ;\n"
                + "moveAttribute(MapFieldAttrModified.MODIFIED, " + OWNER_REFERENCE + ") ;",
            render(setAttribute).trim());
    }

    @Test
    @DisplayName("an attribute char without a modified flag renders only the set groups")
    void attributeCharRendersProtectionAndIntensityOnly()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        // "D" lowers to UNPROTECTED + NORMAL (the modified setter is commented out
        // in the legacy table): exactly two calls, no blank line for the absent group.
        CEntitySetAttribute setAttribute = lowerAttributeChar("D", factory);
        assertEquals("MapFieldAttrProtection.UNPROTECTED", setAttribute.getProtectionConstant());
        assertEquals("MapFieldAttrIntensity.NORMAL", setAttribute.getIntensityConstant());
        assertNull(setAttribute.getModifiedConstant());
        assertEquals(
            "moveAttribute(MapFieldAttrProtection.UNPROTECTED, " + OWNER_REFERENCE + ") ;\n"
                + "moveAttribute(MapFieldAttrIntensity.NORMAL, " + OWNER_REFERENCE + ") ;",
            render(setAttribute).trim());
    }

    @Test
    @DisplayName("MOVE 1 TO <FIELD>-L renders the single MODIFIED call (production lowering)")
    void moveToFieldLengthRendersModified()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityFieldLength fieldLength =
            new CEntityFieldLength(1, "WS-FIELD-L", catalog(), owner());

        CBaseActionEntity action =
            fieldLength.GetSpecialAssignment(new MockTerminal("1"), factory, 8);

        CEntitySetAttribute setAttribute = assertInstanceOf(CEntitySetAttribute.class, action);
        assertEquals(CEntitySetAttribute.class, setAttribute.getClass());
        assertNull(setAttribute.getAttributeValue());
        assertNull(setAttribute.getProtectionConstant());
        assertNull(setAttribute.getIntensityConstant());
        assertEquals("MapFieldAttrModified.MODIFIED", setAttribute.getModifiedConstant());
        assertEquals("moveAttribute(MapFieldAttrModified.MODIFIED, " + OWNER_REFERENCE + ") ;",
            render(setAttribute).trim());
    }

    @Test
    @DisplayName("a moved attribute value renders the single moveAttribute(value, field) and stops")
    void movedAttributeValueRendersMoveAttribute()
    {
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntitySetAttribute setAttribute = factory.NewEntitySetAttribute(1, owner());
        setAttribute.SetAttribute(new MockDataEntity(2, "WS-ATTR"));

        assertEquals("moveAttribute(WS-ATTR, " + OWNER_REFERENCE + ") ;",
            render(setAttribute).trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        MockDataEntity field = owner();

        CEntitySetAttribute setAttribute = factory.NewEntitySetAttribute(1, field);

        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySetAttribute.class, setAttribute.getClass());
        assertSame(field, setAttribute.getField());
        // Bare factory construction: no attribute value and no group flags -> nothing
        // to emit, exactly the legacy DoExport fall-through (no writeLine reached).
        assertEquals("", render(setAttribute).trim());
    }

    @Test
    @DisplayName("the retired backend's early-return precedence is preserved")
    void legacyBranchPrecedencePreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySetAttribute setAttribute = factory.NewEntitySetAttribute(1, owner());

        // Legacy DoExport tested attributeValue first and returned: even with every
        // group flag set, a moved attribute value emits the single moveAttribute.
        setAttribute.SetProtected();
        setAttribute.SetBright();
        setAttribute.SetModified();
        setAttribute.SetAttribute(new MockDataEntity(2, "WS-ATTR"));
        assertEquals("moveAttribute(WS-ATTR, " + OWNER_REFERENCE + ") ;",
            render(setAttribute).trim());

        // Without an attribute value the protection group keeps its else-if
        // precedence (PROTECTED wins over any later protection flag).
        CEntitySetAttribute protection = factory.NewEntitySetAttribute(1, owner());
        protection.SetAutoSkip();
        protection.SetProtected();
        assertEquals("MapFieldAttrProtection.AUTOSKIP", protection.getProtectionConstant());
        assertEquals("moveAttribute(MapFieldAttrProtection.AUTOSKIP, " + OWNER_REFERENCE + ") ;",
            render(protection).trim());
    }

    @Test
    @DisplayName("the retired backend's ignore semantics are preserved")
    void ignoreSemanticsPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());

        // No field reference -> ignored (never rendered).
        CEntitySetAttribute noField = factory.NewEntitySetAttribute(1, null);
        assertTrue(noField.ignore());

        // An ignored attribute value -> ignored (legacy second clause).
        CEntitySetAttribute ignoredValue = factory.NewEntitySetAttribute(1, owner());
        ignoredValue.SetAttribute(new IgnoredDataEntity());
        assertTrue(ignoredValue.ignore());

        // A live field and a live value -> rendered.
        CEntitySetAttribute live = factory.NewEntitySetAttribute(1, owner());
        live.SetAttribute(new MockDataEntity(2, "WS-ATTR"));
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
