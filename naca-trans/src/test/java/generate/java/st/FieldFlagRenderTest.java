package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.LegacyDataRenderer;
import generate.java.forms.CJavaField;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityFieldFlag;
import semantic.forms.CEntityIsFieldFlag;
import semantic.forms.CEntitySetFlag;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * BMS map-resource field-flag pseudo-variable ({@code <FIELD>P}) rendering
 * through the recursive assembler (the production path).
 *
 * <p>The retired direct backend {@code generate.java.forms.CJavaFieldFlag}
 * rendered its reference as {@code <owner field reference>.getFlag()}
 * ({@code ExportReference} delegated to {@code LegacyDataRenderer.renderReference}
 * for the owner and appended {@code .getFlag()}). The replacement
 * {@code fieldFlagReferenceEntity} template bound through
 * {@code semantic-runtime-bindings.properties} (the sanctioned BMS forms island
 * manifest, never the concrete manifest that {@code FinalArchitectureContractTest}
 * pins to the COBOL/SQL/CICS tree) reads only {@code entity.*} properties:
 * {@code <entity.reference>} is the inherited owner slot
 * ({@link semantic.CBaseDataReference#getReference()}), recursively rendered by
 * the assembler, followed by the {@code .getFlag()} method call on the owner
 * field's {@code Edit} runtime object.
 *
 * <p>The legacy {@code ExportWriteAccessorTo} ({@code moveFlag(...)}) had no
 * live consumer in the recursive pipeline: MOVEs on {@code <FIELD>P} lower
 * through the {@code CEntitySetFlag} action entity built by the pure semantic
 * node's {@code GetSpecialAssignment}, and the FPac factory's
 * {@code NewEntityFieldFlag} throws {@code NacaTransAssertException} so no FPac
 * tree ever holds a flag entity. So the write accessor retires without a
 * replacement, with no behavior change.
 */
class FieldFlagRenderTest
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

    private static String render(CEntityFieldFlag fieldFlag)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(fieldFlag, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("flag reference renders <owner>.getFlag() (legacy ExportReference parity)")
    void referenceRendersGetFlagOnOwner()
    {
        CEntityFieldFlag fieldFlag =
            new CEntityFieldFlag(1, "WS-FIELDP", catalog(), owner());
        // fieldFlagReferenceEntity renders <entity.reference>.getFlag():
        // the owner field reference followed by the Edit.getFlag() call, exactly
        // what CJavaFieldFlag.ExportReference emitted.
        assertEquals(OWNER_REFERENCE + ".getFlag()",
            render(fieldFlag).trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntityFieldFlag fieldFlag =
            factory.NewEntityFieldFlag(1, "WS-FIELDP", owner());
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntityFieldFlag.class, fieldFlag.getClass());
        assertEquals(OWNER_REFERENCE + ".getFlag()",
            render(fieldFlag).trim());
    }

    @Test
    @DisplayName("the legacy data-renderer bridge falls through to the recursive assembler")
    void legacyBridgeFallsThroughToAssembler()
    {
        CEntityFieldFlag fieldFlag =
            new CEntityFieldFlag(1, "WS-FIELDP", catalog(), owner());
        // Production reference rendering enters through LegacyDataRenderer: the
        // reflective ExportReference lookup finds no generate.* override on the
        // pure semantic entity, so it falls through to renderRoot(REFERENCE).
        String bridged = LegacyDataRenderer.renderReference(fieldFlag, 1);
        assertEquals(OWNER_REFERENCE + ".getFlag()", bridged.trim());
        assertEquals(render(fieldFlag).trim(), bridged.trim());
    }

    @Test
    @DisplayName("the retired backend's semantic predicates are preserved on the pure entity")
    void semanticPredicatesPreserved()
    {
        MockDataEntity ownerField = owner();
        CEntityFieldFlag fieldFlag =
            new CEntityFieldFlag(1, "WS-FIELDP", catalog(), ownerField);
        // Values exactly as the retired CJavaFieldFlag backend declared them
        // (FIELD / has-accessors / no-val-needed).
        assertTrue(fieldFlag.HasAccessors());
        assertFalse(fieldFlag.isValNeeded());
        assertEquals(CDataEntity.CDataEntityType.FIELD, fieldFlag.GetDataType());
        assertFalse(fieldFlag.ignore());
        assertSame(ownerField, fieldFlag.getReference());
    }

    @Test
    @DisplayName("production BMS lowering (InitDependences) registers a pure <FIELD>P pseudo-variable")
    void productionLoweringBuildsPureFlagPseudoVar()
    {
        // CEntityResourceField.InitDependences is the production call the BMS
        // transcoder engine drives for every named map field (ONLINM1.bms fields
        // reach it through BmsArtifactContractTest end to end): it must now build
        // the pure semantic entity through the rewired factory.
        CObjectCatalog catalog = catalog();
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
        CJavaField field = new CJavaField(1, "NMMASQ", catalog, exporter);

        field.InitDependences(factory);

        CDataEntity pseudo = catalog.GetDataEntity("NMMASQP", "");
        CEntityFieldFlag fieldFlag =
            assertInstanceOf(CEntityFieldFlag.class, pseudo);
        // The exact pure semantic class: no legacy CJava* controller subclass.
        assertEquals(CEntityFieldFlag.class, fieldFlag.getClass());
        assertSame(field, fieldFlag.getReference());
        assertEquals(CDataEntity.CDataEntityType.FIELD, fieldFlag.GetDataType());
    }

    @Test
    @DisplayName("special assignment/condition analysis still builds the set/is flag entities")
    void specialAssignmentAndConditionBranchesPreserved()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory =
            new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        MockDataEntity ownerField = owner();
        CEntityFieldFlag fieldFlag =
            new CEntityFieldFlag(1, "WS-FIELDP", catalog, ownerField);

        // MOVE 1 TO <FIELD>P -> set flag "1" (semantic analysis unchanged).
        CBaseActionEntity setOne = fieldFlag
            .GetSpecialAssignment(new MockTerminal("1"), factory, 2);
        assertInstanceOf(CEntitySetFlag.class, setOne);
        // MOVE 0 TO <FIELD>P -> set flag "0".
        CBaseActionEntity setZero = fieldFlag
            .GetSpecialAssignment(new MockTerminal("0"), factory, 3);
        assertInstanceOf(CEntitySetFlag.class, setZero);
        // MOVE LOW-VALUE TO <FIELD>P -> reset flag.
        CBaseActionEntity reset = fieldFlag
            .GetSpecialAssignment(new MockTerminal("LOW-VALUE"), factory, 4);
        assertInstanceOf(CEntitySetFlag.class, reset);
        // Unsupported constant: no action entity (legacy behavior preserved).
        assertNull(fieldFlag.GetSpecialAssignment(new MockTerminal("X"), factory, 5));

        // IF <FIELD>P = 1 -> is-flag condition entity.
        CBaseEntityCondition isFlag = fieldFlag.GetSpecialCondition(
            6, "1", CBaseEntityCondition.EConditionType.IS_EQUAL, factory);
        assertInstanceOf(CEntityIsFieldFlag.class, isFlag);
        // IF <FIELD>P = LOW-VALUE -> is-flag-set with opposite (legacy behavior).
        CBaseEntityCondition isLowValue = fieldFlag.GetSpecialCondition(
            7, "LOW-VALUE", CBaseEntityCondition.EConditionType.IS_EQUAL, factory);
        assertInstanceOf(CEntityIsFieldFlag.class, isLowValue);
        // Unsupported condition constant: no condition entity.
        assertNull(fieldFlag.GetSpecialCondition(
            8, "X", CBaseEntityCondition.EConditionType.IS_EQUAL, factory));
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
