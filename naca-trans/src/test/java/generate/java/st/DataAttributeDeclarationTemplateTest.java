package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.CJavaAttribute;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import org.stringtemplate.v4.ST;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Validates the data-section declaration ST4 template
 * ({@code dataAttributeDeclaration}) against the fluent-builder form the direct
 * generator ({@code CJavaAttribute.DoExport}) produces. This is the building
 * block for migrating the data section to ST4.
 */
class DataAttributeDeclarationTemplateTest
{
    private static CObjectCatalog catalog() {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private String render(CJavaAttribute attr) {
        ST t = TemplateLoader.getTemplate("dataAttributeDeclaration");
        t.add("entity", attr);
        return t.render();
    }

    @Test
    void picXWithValueSpaces()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-CHAR", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeString(10);
        attr.SetInitialValueSpaces();
        String out = render(attr);
        assertTrue(out.contains("Var WS_CHAR = declare.level(05).picX(10).valueSpaces().var() ;"), out);
    }

    @Test
    void pic9Numeric()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-NUM", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeNum(5, 0);
        attr.SetInitialValueZeros();
        String out = render(attr);
        assertTrue(out.contains("Var WS_NUM = declare.level(05).pic9(5).valueZero().var() ;"), out);
    }

    @Test
    void comp3WithDecimals()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-PACKED", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeSigned(3, 2);
        attr.SetComp("Comp3");
        String out = render(attr);
        assertTrue(out.contains("declare.level(05).picS9(3,2).comp3()"), out);
    }
}
