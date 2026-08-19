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
 * {@code generate.java.forms.CJavaFormContainer}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityResourceFormContainer} (BMS is a CICS screen-map DSL, never a COBOL
 * dialect). A mapset is lowered from a BMS {@code .bms} {@code MAPSET} definition
 * ({@code parser/map_elements/CMapSetElement} -&gt; {@code factory.NewEntityFormContainer(line, name,
 * save)}). It is the mapset root: it emits a whole {@code class <name> extends Map} skeleton and then
 * unfolds its maps inside the class body. This test pins:
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactory.NewEntityFormContainer} (the
 *       inherited production factory path, via {@code BmsJavaEntities.formContainer}) builds exactly
 *       the pure semantic entity, not a {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>reference byte-parity</b> — a data reference to the mapset renders the target-formatted name
 *       through the {@code recursiveFormContainerEntity} forms-island binding, both via the recursive
 *       assembler and via the {@code LegacyDataRenderer.renderReference} fall-through (the retired
 *       backend's {@code ExportReference == formatIdentifier(GetName())});</li>
 *   <li><b>skeleton byte-parity</b> — driving the production legacy traversal protocol
 *       ({@code LegacyLanguageRenderer.invokeExport}, exactly what the BMS transcoder calls on the
 *       mapset root) renders the imports, the {@code class <name> extends Map &#123;&#125;} header through the
 *       {@code recursiveFormContainerDeclarationEntity} template (reading the RAW {@code GetName()} —
 *       the retired backend did NOT format the class name), the two {@code Copy} factory methods and
 *       the constructor, then the class-body {@code { ... }} block over the mapset's maps — which the
 *       generate-layer bridge drives over {@code entity.getForms()} (the {@code arrForm} collection
 *       populated via {@code AddForm}, not the generic child list), keeping the maps rendering through
 *       their own ST4 bindings/backends;</li>
 *   <li><b>the XML/.res artifact name is untouched (the retry fix)</b> — {@code getFormattedName()}
 *       stays the neutral {@code CBaseLanguageEntity} normalization even after the factory injects a
 *       target-specific identifier formatter; only the Java reference/declaration getters
 *       ({@code getContainerReference}/{@code getMapClassName}) consume that formatter. This is what
 *       keeps {@code MakeXMLOutput}'s {@code <form name=... title=...>} attributes case-preserving
 *       exactly as before the retirement;</li>
 *   <li><b>protocol preservation</b> — {@code FORM} data type, {@code isValNeeded() == false},
 *       {@code HasAccessors() == false}, {@code IsNeedDeclarationInClass() == true}, and the qualified
 *       mapset {@code GetTypeDecl}.</li>
 * </ul>
 */
class FormContainerRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String renderReference(CEntityResourceFormContainer entity)
    {
        // The reference unfolds through the assembler exactly as LegacyDataRenderer's
        // fall-through does: REFERENCE role, resolved by the forms-island manifest binding
        // semantic.forms.CEntityResourceFormContainer -> recursiveFormContainerEntity.
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("factory.NewEntityFormContainer builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        CEntityResourceFormContainer entity = factory.NewEntityFormContainer(1, "MY-SET", false);

        // Exactly the pure semantic class, not the retired CJavaFormContainer backend subclass.
        assertEquals(CEntityResourceFormContainer.class, entity.getClass());
        assertEquals(CDataEntity.CDataEntityType.FORM, entity.GetDataType());
    }

    @Test
    @DisplayName("mapset reference renders the formatted name through the recursive assembler")
    void referenceRendersThroughRecursiveAssembler()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);
        CEntityResourceFormContainer entity = factory.NewEntityFormContainer(1, "MY-SET", false);

        // Byte-for-byte the retired backend's ExportReference: formatIdentifier(GetName()).
        // BmsJavaEntities.formContainer injects output::FormatIdentifier; MockJavaExporter inherits the
        // neutral CBaseLanguageExporter.FormatIdentifier ('-'->'_').
        assertEquals(exporter.FormatIdentifier("MY-SET"), renderReference(entity));
        assertEquals("MY_SET", renderReference(entity));
    }

    @Test
    @DisplayName("LegacyDataRenderer.renderReference falls through to the recursive assembler binding")
    void referenceRendersThroughLegacyDataRendererFallThrough()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityResourceFormContainer entity = factory.NewEntityFormContainer(1, "MY-SET", false);

        // With the backend's reflective ExportReference gone, the semantic-declared path returns
        // null and falls through to the recursive assembler binding (recursiveFormContainerEntity).
        assertEquals("MY_SET", LegacyDataRenderer.renderReference(entity, 0));
        // A null reference still yields the legacy [UNDEFINED] sentinel (behavior preserved).
        assertEquals("[UNDEFINED]", LegacyDataRenderer.renderReference(null, 0));
    }

    @Test
    @DisplayName("driving the legacy traversal renders the mapset class skeleton + map block byte-for-byte")
    void skeletonAndBlockRenderThroughTraversal()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);

        CEntityResourceFormContainer entity = factory.NewEntityFormContainer(1, "MY-SET", false);
        // A map of the mapset: the container stores maps in arrForm (AddForm), not the generic child
        // list, and the bridge drives the legacy invokeExport traversal over getForms().
        CEntityResourceForm form = factory.NewEntityForm(1, "MY-MAP", false);
        form.setResourceName("MYRES");
        form.SetSize(80, 24); // SetSize(col, line): nSizeCol=80, nSizeLine=24
        entity.AddForm(form);

        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(entity, JavaTemplateRole.ROOT);
        // The fixed imports the retired backend emitted verbatim.
        assertTrue(output.contains("import nacaLib.mapSupport.* ;"), "got:\n" + output);
        assertTrue(output.contains("import nacaLib.varEx.* ;"), "got:\n" + output);
        assertTrue(output.contains("import nacaLib.program.* ;"), "got:\n" + output);
        assertTrue(output.contains("import nacaLib.basePrgEnv.* ;"), "got:\n" + output);
        // The class header renders through recursiveFormContainerDeclarationEntity using the RAW
        // GetName() (the retired backend did NOT format the class name — the '-' is preserved).
        assertTrue(output.contains("class MY-SET extends Map {"),
            "class header must use the raw name via recursiveFormContainerDeclarationEntity, got:\n"
                + output);
        // The two Copy factory methods + the constructor, over the raw class name.
        assertTrue(output.contains("static MY-SET Copy(BaseProgram program) {"), "got:\n" + output);
        assertTrue(output.contains("return new MY-SET(program);"), "got:\n" + output);
        assertTrue(output.contains("static MY-SET Copy(BaseProgram program, CopyReplacing rep)  {"),
            "got:\n" + output);
        assertTrue(output.contains("Assert(\"Unimplemented replacing for MAPs\") ;"), "got:\n" + output);
        assertTrue(output.contains("return null ;"), "got:\n" + output);
        assertTrue(output.contains("MY-SET(BaseProgram program) {"), "got:\n" + output);
        assertTrue(output.contains("super(program);"), "got:\n" + output);
        // The map renders inside the class body, through the legacy traversal over getForms().
        assertTrue(output.contains("Form MY_MAP = declare.form(\"MYRES\", 24, 80) ;"),
            "map must render through the legacy block traversal over getForms(), got:\n" + output);
        // Ordering: imports -> class header -> Copy/constructor -> map block.
        assertTrue(output.indexOf("import nacaLib.mapSupport.* ;")
            < output.indexOf("class MY-SET extends Map {"), "got:\n" + output);
        assertTrue(output.indexOf("class MY-SET extends Map {")
            < output.indexOf("Form MY_MAP"), "got:\n" + output);
    }

    @Test
    @DisplayName("the XML/.res artifact name stays neutral: the injected formatter never touches getFormattedName()")
    void formattedNameStaysNeutralForXmlArtifact()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);
        CEntityResourceFormContainer entity = factory.NewEntityFormContainer(1, "ONLINM1", false);

        // Install an aggressive target-specific formatter (lowercasing) — standing in for a real
        // CJavaExporter.FormatIdentifier that camelCases/lowercases. It drives ONLY the Java
        // reference/declaration getters...
        entity.setIdentifierFormatter(String::toLowerCase);
        assertEquals("onlinm1", entity.getContainerReference());

        // ...but getFormattedName() — the getter MakeXMLOutput reads for the .res <form name=...
        // title=...> attributes — stays the neutral CBaseLanguageEntity normalization (displayName /
        // '-'->'_', '#'->'$'), unaffected by the injected formatter. This is the retry fix: the
        // retirement must not hijack the XML/.res artifact name.
        assertEquals("ONLINM1", entity.getFormattedName());

        // And with a name carrying the neutral normalization's special characters, the artifact name
        // still normalizes neutrally ('-'->'_', '#'->'$') rather than through the target formatter.
        CEntityResourceFormContainer special =
            new CEntityResourceFormContainer(1, "A-B#C", catalog(), false);
        special.setIdentifierFormatter(String::toLowerCase);
        assertEquals("a-b#c", special.getContainerReference());
        assertEquals("A_B$C", special.getFormattedName());
    }

    @Test
    @DisplayName("a hand-built entity (no factory) falls back to the neutral normalization and a no-op skeleton")
    void handBuiltEntityUsesNeutralFallbacks()
    {
        CEntityResourceFormContainer bare =
            new CEntityResourceFormContainer(1, "A-B#C", catalog(), false);

        // LegacyLanguageRenderer.formatIdentifier's no-output fallback ('-'->'_', '#'->'$').
        assertEquals("A_B$C", bare.getContainerReference());
        // The class name is the RAW name (the retired backend did not format it).
        assertEquals("A-B#C", bare.getMapClassName());
        assertTrue(bare.getForms().isEmpty());
        // No factory-injected renderer: DoExport is a no-op and never fails.
        LegacyLanguageRenderer.invokeExport(bare);
    }

    @Test
    @DisplayName("pure entity preserves the retired backend's data-entity protocols")
    void preservesLegacyDataEntityProtocols()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityResourceFormContainer entity = factory.NewEntityFormContainer(1, "MY-SET", false);

        // A mapset: FORM data type, never a declared val, never an accessor-bearing variable, needed
        // as an in-class declaration, not ignored.
        assertEquals(CDataEntity.CDataEntityType.FORM, entity.GetDataType());
        assertFalse(entity.isValNeeded());
        assertFalse(entity.HasAccessors());
        assertTrue(entity.IsNeedDeclarationInClass());
        assertFalse(entity.ignore());
        // The mapset type declaration: the raw name with '-' -> '_' (no owner here).
        assertEquals("MY_SET", entity.GetTypeDecl());
        // No write-accessor protocol is reachable (renderWriteAccessor finds no generate.*
        // ExportWriteAccessorTo override on the pure entity — the semantic-declared method is ignored
        // by the reflection boundary and returns null).
        assertNull(LegacyDataRenderer.renderWriteAccessor(entity, "X"));
    }

    /**
     * Minimal screen-map field stand-in: a concrete {@link CBaseResourceEntity} whose
     * {@code DoExport} writes a marker line, so the map's field-block traversal (which the
     * generate-layer form bridge drives over {@code getFields()} via {@code invokeExport}) has a
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
        // getDeclaredMethod("DoExport")); each concrete entity declares its own.
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
