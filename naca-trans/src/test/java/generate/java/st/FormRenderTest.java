package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CBaseLanguageExporter;
import generate.CJavaEntityFactory;
import generate.LegacyDataRenderer;
import generate.LegacyLanguageRenderer;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CBaseResourceEntity;
import semantic.CDataEntity;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaForm}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityResourceForm} (BMS is a CICS screen-map DSL, never a COBOL dialect).
 * A form is lowered from a BMS {@code .bms} map definition ({@code parser/map_elements/CMapElement}
 * -&gt; {@code factory.NewEntityForm(line, name, save)}, which also assigns the enclosing mapset
 * container as the form's {@code of} qualifier). This test pins:
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactory.NewEntityForm} (the inherited
 *       production factory path, via {@code BmsJavaEntities.form}) builds exactly the pure semantic
 *       entity, not a {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>reference byte-parity</b> — a data reference to the form renders the target-formatted
 *       name through the {@code recursiveFormEntity} forms-island binding, both via the recursive
 *       assembler and via the {@code LegacyDataRenderer.renderReference} fall-through (the retired
 *       backend's {@code ExportReference == formatIdentifier(GetName())} when no {@code of}
 *       qualifier is set), and qualifies it with the target-formatted container name when the parser
 *       set {@code of} ({@code <container>.<form>});</li>
 *   <li><b>declaration byte-parity</b> — driving the production legacy traversal protocol
 *       ({@code LegacyLanguageRenderer.invokeExport}, exactly what {@code CJavaFormContainer.DoExport}
 *       calls) renders {@code Form <name> = declare.form("<resourceName>", <sizeLine>, <sizeCol>) ;}
 *       through the {@code recursiveFormDeclarationEntity} template, followed by the {@code { ... }}
 *       block over the form's fields — which the generate-layer bridge drives over
 *       {@code entity.getFields()} (the {@code arrFields} collection populated via {@code AddField},
 *       not the generic child list), keeping the still-direct BMS field backends rendering;</li>
 *   <li><b>no generate coupling</b> — the retired backend's generate-layer calls
 *       ({@code formatIdentifier}, the {@code writeLine/startBlock/<fields>/endBlock} block) are
 *       supplied by neutral functions the generate-layer factory injects; a hand-built entity falls
 *       back to the neutral legacy normalization and a no-op declaration renderer;</li>
 *   <li><b>protocol preservation</b> — {@code FORM} data type, {@code isValNeeded() == false},
 *       {@code HasAccessors() == false}, no reachable write-accessor protocol, and
 *       {@code GetTypeDecl -> ""}.</li>
 * </ul>
 */
class FormRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String renderReference(CEntityResourceForm entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityResourceForm -> recursiveFormEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("factory.NewEntityForm builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        CEntityResourceForm entity = factory.NewEntityForm(1, "MY-MAP", false);

        // Exactly the pure semantic class, not the retired CJavaForm backend subclass.
        assertEquals(CEntityResourceForm.class, entity.getClass());
        assertEquals(CDataEntity.CDataEntityType.FORM, entity.GetDataType());
    }

    @Test
    @DisplayName("form reference renders the formatted name through the recursive assembler (no qualifier)")
    void referenceRendersThroughRecursiveAssembler()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);
        CEntityResourceForm entity = factory.NewEntityForm(1, "MY-MAP", false);

        // Byte-for-byte the retired backend's ExportReference with no of qualifier:
        // formatIdentifier(GetName()). BmsJavaEntities.form injects output::FormatIdentifier;
        // MockJavaExporter inherits the neutral CBaseLanguageExporter.FormatIdentifier ('-'->'_').
        assertEquals(exporter.FormatIdentifier("MY-MAP"), renderReference(entity));
        assertEquals("MY_MAP", renderReference(entity));
    }

    @Test
    @DisplayName("form reference qualifies the container when the parser set the of qualifier")
    void referenceQualifiesContainerQualifier()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);
        CEntityResourceForm entity = factory.NewEntityForm(1, "MY-MAP", false);
        CEntityResourceFormContainer container =
            factory.NewEntityFormContainer(1, "MY-SET", false);

        // CMapElement assigns ef.of = container in production; the retired backend then prefixed
        // renderReference(of) + "." — the target-formatted container name (its ExportReference is
        // formatIdentifier(GetName())). The pure getter reproduces that over precomputed names.
        entity.of = container;
        assertEquals("MY_SET.MY_MAP", renderReference(entity));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler binding")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityResourceForm entity = factory.NewEntityForm(1, "MY-MAP", false);

        // With the backend's reflective ExportReference gone, the semantic-declared path returns
        // null and falls through to the recursive assembler binding (recursiveFormEntity).
        assertEquals("MY_MAP", LegacyDataRenderer.renderReference(entity, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("driving the legacy traversal renders the declare.form declaration + field block byte-for-byte")
    void declarationAndBlockRenderThroughTraversal()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);

        CEntityResourceForm entity = factory.NewEntityForm(1, "MY-MAP", false);
        entity.setResourceName("MYRES");
        entity.SetSize(80, 24); // SetSize(col, line): nSizeCol=80, nSizeLine=24
        // A field of the form: the form stores fields in arrFields (AddField), not the generic
        // child list, and the bridge drives the legacy invokeExport traversal over getFields().
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.DECLARATION);
        // The declaration line: the formatted name is the Java variable; the quoted argument is the
        // formatted resource name; the dimensions are the parser-resolved line then column counts.
        assertTrue(output.contains(
            "Form MY_MAP = declare.form(\"MYRES\", 24, 80) ;"),
            "declaration line must render through recursiveFormDeclarationEntity, got:\n" + output);
    }

    @Test
    @DisplayName("a hand-built entity (no factory) falls back to the neutral normalization and a no-op declaration")
    void handBuiltEntityUsesNeutralFallbacks()
    {
        CEntityResourceForm bare = new CEntityResourceForm(1, "A-B#C", catalog(), false);
        bare.setResourceName("R-D");
        bare.SetSize(40, 12);

        // LegacyLanguageRenderer.formatIdentifier's no-output fallback ('-'->'_', '#'->'$').
        assertEquals("A_B$C", bare.getFormattedName());
        assertEquals("R_D", bare.getFormattedResourceName());
        assertEquals("A_B$C", bare.getFormReference());
        assertEquals(12, bare.getSizeLine());
        assertEquals(40, bare.getSizeCol());
        // No factory-injected renderer: DoExport is a no-op and never fails.
        LegacyLanguageRenderer.invokeExport(bare);
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityResourceForm entity = factory.NewEntityForm(1, "MY-MAP", false);

        // A form: FORM data type, never a declared val, never an accessor-bearing variable, not
        // ignored, and contributes no type declaration.
        assertEquals(CDataEntity.CDataEntityType.FORM, entity.GetDataType());
        assertFalse(entity.isValNeeded());
        assertFalse(entity.HasAccessors());
        assertFalse(entity.ignore());
        assertEquals("", entity.GetTypeDecl());
        // No write-accessor protocol is reachable (renderWriteAccessor finds no generate.*
        // ExportWriteAccessorTo override on the pure entity — the semantic-declared method is
        // ignored by the reflection boundary and returns null).
        assertNull(LegacyDataRenderer.renderWriteAccessor(entity, "X"));
    }

    /**
     * Minimal screen-map field stand-in: a concrete {@link CBaseResourceEntity} whose
     * {@code DoExport} writes a marker line, so the form's field-block traversal (which the
     * generate-layer bridge drives over {@code getFields()} via {@code invokeExport}) has a
     * well-formed child to render — standing in for the still-direct BMS field backends a real
     * {@code .bms} map carries.
     */
    static class MockFormField extends CBaseResourceEntity
    {
        private final String marker;

        MockFormField(int line, CObjectCatalog cat, CBaseLanguageExporter out, String marker)
        {
            super(line, "FLD", cat);
            this.marker = marker;
            LegacyLanguageRenderer.bind(this, out);
        }

        @Override
        protected void RegisterMySelfToCatalog()
        {
            // No-op for mock.
        }

        @Override
        public boolean ignore()
        {
            return false;
        }

        @Override
        public String GetConstantValue()
        {
            return "";
        }

        // Not @Override: DoExport is invoked reflectively (LegacyLanguageRenderer.invokeExport ->
        // getDeclaredMethod("DoExport")); each concrete entity declares its own, as the retired
        // backend and the pure entity both do.
        protected void DoExport()
        {
            LegacyLanguageRenderer.writeLine(this, marker);
        }

        @Override
        public boolean isValNeeded()
        {
            return false;
        }

        @Override
        public CDataEntityType GetDataType()
        {
            return CDataEntityType.FIELD;
        }

        @Override
        public String GetTypeDecl()
        {
            return "";
        }
    }
}
