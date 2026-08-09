package generate.java.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import semantic.forms.CResourceStrings;
import utils.CObjectCatalog;

/** Retirement proof for the BMS localized-resource backend. */
class CJavaResourceStringsRetirementTest
{
    @Test
    void bothFactoriesBuildTheConcreteNeutralResourceModel()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CResourceStrings.class,
            new CJavaEntityFactory(catalog, null).NewResourceString(24, 80).getClass());
        assertEquals(CResourceStrings.class,
            new CJavaEntityFactory(catalog, null).NewResourceString(24, 80).getClass());
    }

    @Test
    void orderedSemanticEntriesDriveJavaTemplateAndXmlResource() throws Exception
    {
        CResourceStrings resources = new CResourceStrings(24, 80);
        resources.SetResourceText(1, 1, " Bonjour ", "F", "WELCOME", 9);
        resources.SetResourceText(1, 1, "Hello", "G", "WELCOME", 9);
        resources.FormatResource("WELCOME");

        assertEquals(
            "LocalizedString welcome = declare.localizedString()"
                + ".text(LanguageCode.FR, \"Bonjour\")"
                + ".text(LanguageCode.EN, \"Hello\");",
            BmsJavaEntities.renderLocalizedStringDeclaration(
                resources, "WELCOME", "welcome"));

        Document document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().newDocument();
        Element texts = (Element) resources.exportResource("WELCOME", document);
        assertEquals("FR", ((Element) texts.getFirstChild()).getAttribute("lang"));
        assertEquals("Bonjour", texts.getFirstChild().getTextContent());
        assertEquals("EN", ((Element) texts.getLastChild()).getAttribute("lang"));
        assertEquals("Hello", texts.getLastChild().getTextContent());
    }
}
