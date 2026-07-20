package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.CJavaAttribute;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
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
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(attr, JavaTemplateRole.DECLARATION);
    }

    private String renderDirect(CJavaAttribute attr, MockJavaExporter exporter) {
        attr.StartExport();
        return exporter.getCapturedOutput().strip();
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
        assertEquals(renderDirect(attr, exporter), out.strip());
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
        assertEquals(renderDirect(attr, exporter), out.strip());
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
        assertEquals(renderDirect(attr, exporter), out.strip());
    }

    @Test
    void editedPictureIsEscapedByTheTemplateRenderer()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-EDITED", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeEdited("ZZ\\\"9");
        String out = render(attr);
        assertTrue(out.contains("declare.level(05).pic(\"ZZ\\\\\\\"9\")"), out);
    }

    @Test
    void theSameAttributeUsesReferenceAndDeclarationRoles()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-ROLE", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeString(4);

        assertEquals("WS_ROLE", TemplateLoader.getRecursiveAssembler().renderRoot(attr));
        assertTrue(render(attr).contains("Var WS_ROLE = declare.level(05).picX(4)"));
    }

    @Test
    void blankWhenZeroNumericIsDeclaredAsEditedPictureWithoutMutatingTheTree()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-BWZ", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeNum(4, 2);
        attr.SetBlankWhenZero(true);

        String out = render(attr);
        assertTrue(out.contains("declare.level(05).pic(\"9999.99\").blankWhenZero().var() ;"), out);
        assertEquals(renderDirect(attr, exporter), out.strip());

        // The declaration is derived read-only: the raw type/format are untouched,
        // so a second direct export is identical (idempotent, no tree mutation).
        assertEquals("pic9", attr.getType());
        exporter.clearOutput();
        assertEquals(renderDirect(attr, exporter), out.strip());
        assertEquals("pic9", attr.getType());
    }

    @Test
    void syncClause()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-SYNC", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeString(4);
        attr.SetSync(true);
        String out = render(attr);
        assertTrue(out.contains("Var WS_SYNC = declare.level(05).picX(4).sync().var() ;"), out);
        assertEquals(renderDirect(attr, exporter), out.strip());
    }

    @Test
    void justifiedRightClause()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-JUST", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeString(4);
        attr.SetJustifiedRight(true);
        String out = render(attr);
        assertTrue(out.contains("Var WS_JUST = declare.level(05).picX(4).justifyRight().var() ;"), out);
        assertEquals(renderDirect(attr, exporter), out.strip());
    }

    @Test
    void valueLiteralClause()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-VAL", catalog, exporter);
        attr.SetLevel("05");
        attr.SetTypeString(4);
        attr.SetInitialValue(new MockDataEntity(1, catalog, exporter, "123"));
        String out = render(attr);
        assertTrue(out.contains("Var WS_VAL = declare.level(05).picX(4).value(123).var() ;"), out);
        assertEquals(renderDirect(attr, exporter), out.strip());
    }

    @Test
    void compClause()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-BIN", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeNum(4, 0);
        attr.SetComp("Comp");
        String out = render(attr);
        assertTrue(out.contains("Var WS_BIN = declare.level(05).pic9(4).comp().var() ;"), out);
        assertEquals(renderDirect(attr, exporter), out.strip());
    }

    @Test
    void comp2Clause()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaAttribute attr = new CJavaAttribute(1, "WS-FLT", catalog(), exporter);
        attr.SetLevel("05");
        attr.SetTypeNum(4, 0);
        attr.SetComp("Comp2");
        String out = render(attr);
        assertTrue(out.contains("Var WS_FLT = declare.level(05).pic9(4).comp2().var() ;"), out);
        assertEquals(renderDirect(attr, exporter), out.strip());
    }
}
