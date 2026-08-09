package parser.map_elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.java.st.MockJavaExporter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import semantic.CBaseLanguageEntity;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceFieldArray;
import semantic.forms.CResourceStrings;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Production-lowering fixture for the BMS field-array parser node
 * {@code parser.map_elements.CFieldArray}, the node the retired direct backend
 * {@code generate.java.forms.CJavaFieldArray} used to render (BMS is a CICS screen-map DSL,
 * never a COBOL dialect).
 *
 * <p>{@code CMapElement.ManageArray} collapses a run of indexed BMS fields
 * ({@code FLD(1)..FLD(3)}) into a {@code CFieldArray} motif via {@link CFieldArray#ReadField},
 * and {@code CMapElement.DoSemanticAnalysis} (the {@code EBMSElementType.ARRAY} branch) invokes
 * {@link CFieldArray#DoSemanticAnalysis}, which builds the semantic entity through the
 * production seam {@code factory.NewEntityFieldArray()}. This test drives that exact node and
 * seam — the same calls {@code ManageArray}/{@code DoSemanticAnalysis} make — and pins:
 *
 * <ul>
 *   <li><b>pure semantic lowering</b> — {@code DoSemanticAnalysis} lowers through
 *       {@code factory.NewEntityFieldArray()} to exactly
 *       {@link semantic.forms.CEntityResourceFieldArray}, not a {@code CJava*} backend;</li>
 *   <li><b>motif resolution</b> — the parser-resolved occurrence/column counts and fill
 *       direction flow through the fixed {@code SetArray} (the legacy self-assignment pinned
 *       {@code nbItems=0}/{@code vert=false}), and the single motif field is attached with
 *       {@code nOccurs} set;</li>
 *   <li><b>XML artifact</b> — the lowered array exports the {@code <array nbCol nbItems vert
 *       line col><item>...} node the BMS artifact traversal ({@code CEntityResourceForm
 *       .exportXMLFields}) dispatches polymorphically.</li>
 * </ul>
 */
class CFieldArrayLoweringTest
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

    /** A motif field exactly as {@code CMapElement.ManageArray} feeds to {@code ReadField}. */
    private static CFieldElement field(String name, int line, int col, int length)
    {
        CFieldElement field = new CFieldElement(name, 1);
        field.posLine = line;
        field.posCol = col;
        field.length = length;
        return field;
    }

    private static CEntityResourceFieldArray lowerHorizontalMotif(CJavaEntityFactory factory)
    {
        CFieldArray array = new CFieldArray();
        // Three same-length fields on one line, indexed (1)..(3): a horizontal 3-column motif.
        assertTrue(array.ReadField(field("FLD(1)", 1, 1, 3)));
        assertTrue(array.ReadField(field("FLD(2)", 1, 5, 3)));
        assertTrue(array.ReadField(field("FLD(3)", 1, 9, 3)));
        return (CEntityResourceFieldArray) array.DoSemanticAnalysis(null, factory);
    }

    @Test
    @DisplayName("CFieldArray.DoSemanticAnalysis lowers through factory.NewEntityFieldArray to the pure semantic entity")
    void lowersToPureSemanticEntity()
    {
        CEntityResourceFieldArray array =
            lowerHorizontalMotif(new CJavaEntityFactory(catalog(), new MockJavaExporter()));

        assertNotNull(array);
        assertEquals(CEntityResourceFieldArray.class, array.getClass(),
            "the production seam factory.NewEntityFieldArray() must build the pure semantic entity");
        assertFalse(array.IsEntryField());
    }

    @Test
    @DisplayName("the parser-resolved motif layout flows through the fixed SetArray")
    void motifResolutionFlowsThrough()
    {
        CEntityResourceFieldArray array =
            lowerHorizontalMotif(new CJavaEntityFactory(catalog(), new MockJavaExporter()));

        // ManageArray resolved a 3-occurrence, 3-column horizontal motif at (line 1, col 1); the
        // fixed SetArray binds those (the buggy backend pinned nbItems=0/vert=false).
        assertEquals(3, array.getNbItems());
        assertEquals(3, array.getNbColumns());
        assertFalse(array.isVerticalFilling());
        assertEquals(1, array.getPosLine());
        assertEquals(1, array.getPosCol());

        // One motif field (the (1) field, renamed to the array base name) is attached, carrying
        // the occurrence count, exactly as CFieldArray.DoSemanticAnalysis sets it.
        java.util.List<CBaseLanguageEntity> children = array.getChildren();
        assertEquals(1, children.size(), "a horizontal motif keeps a single motif field");
        CEntityResourceField motif = (CEntityResourceField) children.get(0);
        assertTrue(motif.IsEntryField(), "the named motif field lowers to an entry field");
        assertEquals("FLD", motif.GetName());
        assertEquals(3, motif.nOccurs);
    }

    @Test
    @DisplayName("the lowered array exports the <array><item> XML artifact with the true motif layout")
    void loweredArrayExportsXml() throws Exception
    {
        CEntityResourceFieldArray array =
            lowerHorizontalMotif(new CJavaEntityFactory(catalog(), new MockJavaExporter()));

        Element el = array.DoXMLExport(newDocument(), stubResources());

        assertEquals("array", el.getNodeName());
        assertEquals("3", el.getAttribute("nbCol"));
        assertEquals("3", el.getAttribute("nbItems"));
        assertEquals("false", el.getAttribute("vert"));
        assertEquals("1", el.getAttribute("line"));
        assertEquals("1", el.getAttribute("col"));

        // A single <item> aggregating the motif field's own XML.
        assertEquals(1, el.getChildNodes().getLength());
        Element item = (Element) el.getFirstChild();
        assertEquals("item", item.getNodeName());
        assertEquals(1, item.getChildNodes().getLength(),
            "the single motif field exports one child node under <item>");
    }
}
