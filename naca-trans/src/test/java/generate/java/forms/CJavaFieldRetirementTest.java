package generate.java.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import semantic.forms.CEntityResourceField;
import semantic.forms.CResourceStrings;
import utils.CObjectCatalog;

/** Retirement proof for the final BMS entry-field direct backend. */
class CJavaFieldRetirementTest
{
    @Test
    void bothFactoriesBuildPureSemanticEntryField()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityResourceField.class,
            new CJavaEntityFactory(catalog, null).NewEntityEntryField(1, "CUSTOMER-FIELD")
                .getClass());
        assertEquals(CEntityResourceField.class,
            new CJavaEntityFactory(catalog, null).NewEntityEntryField(1, "CUSTOMER-FIELD")
                .getClass());
    }

    @Test
    void declarationAndXmlArtifactReadTheSameNeutralModel() throws Exception
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityResourceField field = BmsJavaEntities.entryField(
            1, "CUSTOMER-FIELD", catalog, null);
        CResourceStrings resources = new CResourceStrings(24, 80);
        resources.SetResourceText(2, 4, "Customer", "G", "CUSTOMER-LABEL", 8);
        field.resourceStrings = resources;
        field.csInitialValue = "CUSTOMER-LABEL";
        field.SetDisplayName("CUSTOMER-DISPLAY");
        field.SetTypeString(12);
        field.move(4, 2);
        field.SetFillValue("SPACE");
        field.SetRightJustified(true);
        field.setDevelopable("D");
        field.setFormat("XX-XX");

        String declaration = TemplateLoader.getRecursiveAssembler()
            .renderRoot(field, JavaTemplateRole.DECLARATION).trim();
        assertTrue(declaration.contains(
            "LocalizedString CUSTOMER_LABEL = declare.localizedString()"
                + ".text(LanguageCode.EN, \"Customer\");"));
        assertTrue(declaration.contains(
            "Edit CUSTOMER_FIELD = declare.edit(\"CUSTOMER_DISPLAY\", 12)"
                + ".initialValue(CUSTOMER_LABEL)"
                + ".justifyFill(MapFieldAttrFill.SPACE).justifyRight()"
                + ".setDevelopableMark(\"D\").format(\"XX-XX\").edit() ;"), declaration);
        assertEquals("CUSTOMER_FIELD", TemplateLoader.getRecursiveAssembler()
            .renderRoot(field, JavaTemplateRole.REFERENCE));

        Document document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().newDocument();
        Element xml = field.DoXMLExport(document, resources);
        assertEquals("edit", xml.getNodeName());
        assertEquals("CUSTOMER_DISPLAY", xml.getAttribute("name"));
        assertEquals("CUSTOMER_FIELD", xml.getAttribute("namecopy"));
        assertEquals("12", xml.getAttribute("length"));
        assertEquals("2", xml.getAttribute("line"));
        assertEquals("4", xml.getAttribute("col"));
        assertEquals("right", xml.getAttribute("justify"));
        assertEquals("space", xml.getAttribute("fill"));
        assertEquals("Customer", xml.getElementsByTagName("text").item(0).getTextContent());
    }
}
