package semantic.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import semantic.CDataEntity;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaFieldArray}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityResourceFieldArray} (BMS is a CICS screen-map DSL, never a
 * COBOL dialect).
 *
 * <p>A field array emits <b>no Java of its own</b> (the retired backend's {@code DoExport}
 * only traversed its child motif fields), so there is no ST4 template/binding for it — exactly
 * like {@code CEntityLabelField}; its only own output protocol is the BMS XML/.res artifact
 * {@code DoXMLExport}, which stays target-neutral in the semantic entity. This test pins the
 * semantic-entity contract; the production parser-node lowering
 * ({@code CFieldArray.DoSemanticAnalysis} -> {@code factory.NewEntityFieldArray()}) is pinned
 * by {@code parser.BMS.CFieldArrayLoweringTest}.
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactory.NewEntityFieldArray} (the
 *       BMS production factory path, via {@code BmsJavaEntities.fieldArray}) builds exactly the
 *       pure semantic entity for BOTH the direct and the ST4 factory, not a
 *       {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>latent self-assignment fix</b> — {@code SetArray(nbItems, nbCol, vert)} now binds the
 *       parser-resolved {@code nbItems}/{@code isverticalFilling} fields (the legacy body
 *       assigned two parameters to themselves, pinning {@code nbItems=0}/{@code vert=false});</li>
 *   <li><b>XML byte-parity</b> — {@code DoXMLExport} reproduces the retired backend's
 *       {@code <array nbCol nbItems vert line col><item>...} output, aggregating each child
 *       motif field's XML.</li>
 * </ul>
 */
class CEntityFieldArrayRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static Document newDocument() throws Exception
    {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    /** Minimal resource strings: any non-empty resource name yields a {@code <texts/>} node. */
    private static CResourceStrings stubResources()
    {
        return new CResourceStrings(24, 80)
        {
            @Override
            public void FormatResource(String name)
            {
                // no-op
            }

            @Override
            public Node exportResource(String name, Document doc)
            {
                return doc.createElement("texts");
            }
        };
    }

    @Test
    @DisplayName("factory.NewEntityFieldArray builds the pure semantic entity (both factories)")
    void factoryReturnsPureSemanticEntity()
    {
        CEntityResourceFieldArray direct =
            new CJavaEntityFactory(catalog(), new MockJavaExporter()).NewEntityFieldArray();
        CEntityResourceFieldArray st4 =
            new CJavaEntityFactory(catalog(), null).NewEntityFieldArray();

        // Exactly the pure semantic class, not the retired CJavaFieldArray backend subclass.
        assertEquals(CEntityResourceFieldArray.class, direct.getClass());
        assertEquals(CEntityResourceFieldArray.class, st4.getClass());

        // Retired backend data-entity protocols preserved.
        assertFalse(direct.IsEntryField());
        assertEquals(CDataEntity.CDataEntityType.FIELD, direct.GetDataType());
        assertFalse(direct.isValNeeded());
        assertEquals("", direct.GetTypeDecl());
        assertEquals("", TemplateLoader.getRecursiveAssembler()
            .renderRoot(direct, JavaTemplateRole.DECLARATION));
    }

    @Test
    @DisplayName("SetArray binds the parser-resolved nbItems/vertical-filling (latent self-assignment fixed)")
    void setArrayBindsResolvedValues()
    {
        CEntityResourceFieldArray array =
            new CJavaEntityFactory(catalog(), new MockJavaExporter()).NewEntityFieldArray();

        array.SetArray(3, 2, true);

        // The legacy body assigned the parameters to themselves (nbItems = nbItems;
        // bVerticalFilling = bVerticalFilling;), leaving nbItems==0 and vertical==false. The
        // resolved values must now flow through.
        assertEquals(3, array.getNbItems());
        assertEquals(2, array.getNbColumns());
        assertTrue(array.isVerticalFilling());
    }

    @Test
    @DisplayName("array XML reproduces the retired backend's <array><item> output, with the true nbItems")
    void arrayXmlParity() throws Exception
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityResourceFieldArray array = factory.NewEntityFieldArray();
        array.SetArray(3, 3, false);
        array.SetPosition(2, 4);
        // One child motif field, aggregated under the single <item> exactly as the backend did.
        CEntityLabelField motif = (CEntityLabelField) factory.NewEntityLabelField(1);
        motif.nLength = 3;
        array.AddChild(motif);

        Element el = array.DoXMLExport(newDocument(), stubResources());

        assertEquals("array", el.getNodeName());
        assertEquals("3", el.getAttribute("nbCol"));
        // The fixed nbItems (the buggy backend always emitted "0").
        assertEquals("3", el.getAttribute("nbItems"));
        assertEquals("false", el.getAttribute("vert"));
        assertEquals("2", el.getAttribute("line"));
        assertEquals("4", el.getAttribute("col"));

        // A single <item> wrapping each child field's XML.
        assertEquals(1, el.getChildNodes().getLength());
        Element item = (Element) el.getFirstChild();
        assertEquals("item", item.getNodeName());
        assertEquals(1, item.getChildNodes().getLength());
        assertEquals("label", item.getFirstChild().getNodeName());
    }

    @Test
    @DisplayName("a hand-built entity (no factory) stays well-formed: neutral defaults, XML still target-neutral")
    void handBuiltEntityIsWellFormed() throws Exception
    {
        CEntityResourceFieldArray bare = new CEntityResourceFieldArray(1, "", catalog());

        // No factory -> no generate-layer renderer; defaults are neutral and never fail.
        assertEquals(0, bare.getNbItems());
        assertEquals(0, bare.getNbColumns());
        assertFalse(bare.isVerticalFilling());

        // The XML protocol needs no generate coupling: a bare entity still exports an <array>.
        Element el = bare.DoXMLExport(newDocument(), stubResources());
        assertEquals("array", el.getNodeName());
        assertEquals("0", el.getAttribute("nbItems"));
    }
}
