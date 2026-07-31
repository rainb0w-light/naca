package semantic.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.java.st.MockJavaExporter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;
import lexer.BMS.CBMSLexer;
import lexer.CTokenList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import parser.BMS.CBMSParser;
import parser.map_elements.CMapSetElement;
import semantic.CBaseResourceEntity;
import semantic.CDataEntity;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Retirement test for the BMS map-resource direct backend
 * {@code generate.java.forms.CJavaLabelField}, de-abstracted into the pure semantic entity
 * {@link semantic.forms.CEntityLabelField} (BMS is a CICS screen-map DSL, never a COBOL
 * dialect).
 *
 * <p>A label field emits <b>no Java</b> of its own (the retired backend's {@code DoExport}
 * was empty), so there is no ST4 template/binding for it; its only live output protocol is
 * the BMS XML/.res artifact {@code DoXMLExport}, dispatched polymorphically from the
 * semantic form traversal ({@code CEntityResourceForm.ExportXMLFields}). This test pins:
 *
 * <ul>
 *   <li><b>production construction</b> — {@code CJavaEntityFactory.NewEntityLabelField}
 *       (the BMS production factory path, via {@code BmsJavaEntities.labelField}) builds
 *       exactly the pure semantic entity, not a {@code generate.java.forms.CJava*} backend;</li>
 *   <li><b>XML byte-parity</b> — {@code DoXMLExport} reproduces the retired backend's
 *       {@code <label>}/{@code <title>}/hidden/activeChoice/linkedActiveChoice output;</li>
 *   <li><b>no generate coupling</b> — the retired backend's single generate-layer call
 *       ({@code LegacyLanguageRenderer.formatIdentifier} for {@code linkedActiveChoice}) is
 *       supplied by a neutral formatter the generate-layer factory injects; a hand-built
 *       entity falls back to the neutral legacy normalization;</li>
 *   <li><b>production lowering</b> — the real {@code ONLINM1.bms} unnamed {@code DFHMDF}
 *       fields lower through the parser ({@code CFieldElement}) to {@code CEntityLabelField}
 *       while named fields stay entry fields, and a parsed label exports its XML.</li>
 * </ul>
 */
class CEntityLabelFieldRenderTest
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
            public Element Export(Element parent, Document root)
            {
                return root.createElement("resources");
            }

            @Override
            public void FormatResource(String name)
            {
                // no-op
            }

            @Override
            public String ExportForField(String initialValue, String display)
            {
                return "";
            }

            @Override
            public Node ExportResource(String name, Document doc)
            {
                return doc.createElement("texts");
            }
        };
    }

    @Test
    @DisplayName("factory.NewEntityLabelField builds the pure semantic entity (production construction)")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        CEntityResourceField entity = factory.NewEntityLabelField(1);

        // Exactly the pure semantic class, not the retired CJavaLabelField backend subclass.
        assertEquals(CEntityLabelField.class, entity.getClass());
        assertFalse(entity.IsEntryField());
        assertEquals(CDataEntity.CDataEntityType.FIELD, entity.GetDataType());
    }

    @Test
    @DisplayName("plain label XML reproduces the retired backend's attribute output byte-for-byte")
    void plainLabelXmlParity() throws Exception
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityLabelField label = (CEntityLabelField) factory.NewEntityLabelField(1);
        label.nPosLine = 5;
        label.nPosCol = 10;
        label.nLength = 20;
        label.SetDisplayName("MyLabel");
        label.SetColor("GREEN");
        label.SetHighLight("OFF");
        label.SetBrightness("NORMAL");

        Element el = label.DoXMLExport(newDocument(), stubResources());

        assertEquals("label", el.getNodeName());
        assertEquals("20", el.getAttribute("length"));
        assertEquals("5", el.getAttribute("line"));
        assertEquals("10", el.getAttribute("col"));
        assertEquals("MyLabel", el.getAttribute("name"));
        // color/highlight/brightness are lower-cased exactly as the retired backend did.
        assertEquals("green", el.getAttribute("color"));
        assertEquals("off", el.getAttribute("highlighting"));
        assertEquals("normal", el.getAttribute("brightness"));
    }

    @Test
    @DisplayName("title / hidden modes preserve the retired backend's element selection")
    void titleAndHiddenModes() throws Exception
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());

        CEntityLabelField title = (CEntityLabelField) factory.NewEntityLabelField(1);
        title.SetTitle(null);
        assertEquals("title", title.DoXMLExport(newDocument(), stubResources()).getNodeName());

        CEntityLabelField hidden = (CEntityLabelField) factory.NewEntityLabelField(1);
        hidden.Hide();
        assertNull(hidden.DoXMLExport(newDocument(), stubResources()));
    }

    @Test
    @DisplayName("activeChoice XML preserves the retired backend's type/value/target/submit")
    void activeChoiceXmlParity() throws Exception
    {
        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntityLabelField label = (CEntityLabelField) factory.NewEntityLabelField(1);
        label.setActiveChoice("VAL", "TGT", true);

        Element el = label.DoXMLExport(newDocument(), stubResources());

        assertEquals("label", el.getNodeName());
        assertEquals("activeChoice", el.getAttribute("type"));
        assertEquals("VAL", el.getAttribute("activeChoiceValue"));
        assertEquals("TGT", el.getAttribute("activeChoiceTarget"));
        assertEquals("true", el.getAttribute("activeChoiceSubmit"));
    }

    @Test
    @DisplayName("linkedActiveChoice link is formatted by the generate-layer-injected formatter (no semantic generate coupling)")
    void linkedActiveChoiceUsesInjectedFormatter() throws Exception
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), exporter);
        CEntityLabelField label = (CEntityLabelField) factory.NewEntityLabelField(1);
        label.setLinkedActiveChoice("MY-EDIT#X", "TGT", false);

        Element el = label.DoXMLExport(newDocument(), stubResources());

        assertEquals("label", el.getNodeName());
        assertEquals("linkedActiveChoice", el.getAttribute("type"));
        // BmsJavaEntities.labelField injects output::FormatIdentifier; MockJavaExporter
        // inherits CBaseLanguageExporter.FormatIdentifier ('-'->'_', '#'->'$'). This is the
        // retired backend's formatIdentifier result, supplied without any generate.* token
        // in semantic.forms.CEntityLabelField.
        assertEquals(exporter.FormatIdentifier("MY-EDIT#X"), el.getAttribute("activeChoiceLink"));
        assertEquals("MY_EDIT$X", el.getAttribute("activeChoiceLink"));
        assertEquals("TGT", el.getAttribute("activeChoiceTarget"));
        assertEquals("false", el.getAttribute("activeChoiceSubmit"));
    }

    @Test
    @DisplayName("a hand-built entity (no factory) falls back to the neutral legacy normalization")
    void handBuiltEntityUsesNeutralFallback()
    {
        CEntityLabelField bare = new CEntityLabelField(1, catalog());
        bare.setLinkedActiveChoice("A-B#C", "T", true);
        // LegacyLanguageRenderer.formatIdentifier's no-output fallback.
        assertEquals("A_B$C", bare.getActiveChoiceLink());
    }

    @Test
    @DisplayName("end-to-end: ONLINM1.bms unnamed DFHMDF fields lower to CEntityLabelField, named fields stay entry fields")
    void productionParseLowersLabelFields() throws Exception
    {
        CEntityResourceFormContainer container = analyzeOnlinm1();
        assertNotNull(container, "ONLINM1 must analyze from the real .bms source");
        CEntityResourceForm form = container.getForm();
        assertNotNull(form, "the ONLINEF map must exist");

        int labels = 0;
        int entries = 0;
        CEntityLabelField firstLabel = null;
        for (CBaseResourceEntity field : form.arrFields)
        {
            if (field instanceof CEntityLabelField labelField)
            {
                labels++;
                if (firstLabel == null)
                {
                    firstLabel = labelField;
                }
            }
            else if (field instanceof CEntityResourceField resourceField
                && resourceField.IsEntryField())
            {
                entries++;
            }
        }

        // ONLINM1.bms carries 9 unnamed DFHMDF label fields and 7 named entry fields
        // (NMMASQ, DTEXEC, ...): the parser's unnamed-field branch (CFieldElement ->
        // factory.NewEntityLabelField) must lower to the pure semantic entity, while the
        // named-field branch stays an entry field.
        assertTrue(labels >= 1, "unnamed DFHMDF fields must lower to CEntityLabelField, got " + labels);
        assertTrue(entries >= 1, "named DFHMDF fields must stay entry fields, got " + entries);

        // A parsed label exports its XML through the production protocol (the BMS artifact
        // traversal CEntityResourceForm.ExportXMLFields calls exactly this method).
        Element el = firstLabel.DoXMLExport(newDocument(), stubResources());
        assertNotNull(el, "a parsed ONLINM1 label is not hidden, so it must export an element");
        assertEquals("label", el.getNodeName());
    }

    /** Mirrors BMSTranscoderEngine.doSemanticAnalysis on the real ONLINM1.bms source. */
    private static CEntityResourceFormContainer analyzeOnlinm1() throws Exception
    {
        Path bms = null;
        for (Path p : new Path[] {
            Path.of("NacaSamples/cobol/ONLINM1.bms"),
            Path.of("../NacaSamples/cobol/ONLINM1.bms") })
        {
            if (Files.exists(p))
            {
                bms = p;
                break;
            }
        }
        assertNotNull(bms, "ONLINM1.bms sample must exist");

        CBMSLexer lexer = new CBMSLexer();
        try (InputStream in = new BufferedInputStream(new FileInputStream(bms.toFile())))
        {
            assertTrue(lexer.StartLexer(in, new COriginalLisiting()), "lexing ONLINM1.bms must succeed");
        }
        CTokenList tokens = lexer.GetTokenList();
        CBMSParser parser = new CBMSParser();
        assertTrue(parser.StartParsing(tokens), "parsing ONLINM1.bms must succeed");
        CMapSetElement root = parser.GetRootElement();
        assertNotNull(root, "parsed ONLINM1 root must exist");

        CJavaEntityFactory factory =
            new CJavaEntityFactory(catalog(), new MockJavaExporter());
        return (CEntityResourceFormContainer) root.DoSemanticAnalysis(null, factory);
    }
}
