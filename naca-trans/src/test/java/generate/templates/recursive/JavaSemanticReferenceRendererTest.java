package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.CJavaArrayReference;
import generate.java.CJavaAddressReference;
import generate.java.CJavaAttribute;
import generate.java.CJavaEnvironmentVariable;
import generate.java.CJavaExporter;
import generate.java.CJavaExternalDataStructure;
import generate.java.CJavaIndex;
import generate.java.CJavaStructure;
import generate.java.CJavaSubStringReference;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityConstant;
import utils.CObjectCatalog;

class JavaSemanticReferenceRendererTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final CJavaExporter output = new CJavaExporter(null, "/tmp/unused.java", null, false);
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersOrdinaryReservedAndQualifiedReferencesLikeTheDirectGenerator()
    {
        CJavaAttribute ordinary = attribute("WS-VALUE");
        CJavaAttribute reserved = attribute("NEW");
        CJavaExternalDataStructure qualifier = new CJavaExternalDataStructure(
            1, "COPY-BOOK", catalog, output);
        CJavaAttribute qualified = attribute("COPY-FIELD");
        qualified.of = qualifier;

        assertMatchesDirect(ordinary);
        assertMatchesDirect(reserved);
        assertMatchesDirect(qualifier);
        assertMatchesDirect(qualified);
    }

    @Test
    void usesTheSemanticDisplayNameForStructureReferences()
    {
        CJavaStructure structure = new CJavaStructure(
            1, "ORIGINAL-NAME", catalog, output, "01");
        structure.SetDisplayName("DISPLAY-NAME");

        assertMatchesDirect(structure);
    }

    @Test
    void recursivelyRendersArrayIndexesAndSubstringBounds()
    {
        CJavaArrayReference array = new CJavaArrayReference(1, catalog, output);
        array.SetReference(attribute("TABLE-VALUE"));
        array.AddIndex(expression(number("1")));
        array.AddIndex(expression(number("00002")));

        CJavaSubStringReference substring = new CJavaSubStringReference(1, catalog, output);
        substring.SetReference(array, expression(number("3")), expression(number("4")));

        assertMatchesDirect(array);
        assertMatchesDirect(substring);
    }

    @Test
    void supportsSubstringWithoutAnExplicitLength()
    {
        CJavaSubStringReference substring = new CJavaSubStringReference(1, catalog, output);
        substring.SetReference(attribute("TEXT-VALUE"), expression(number("2")), null);

        assertMatchesDirect(substring);
    }

    @Test
    void rendersFigurativeConstantsLikeTheDirectGenerator()
    {
        assertEquals("CobolConstant.HighValue", assembler.renderRoot(
            new CEntityConstant(CEntityConstant.Value.HIGH_VALUE),
            JavaTemplateRole.REFERENCE));
        assertEquals("CobolConstant.Spaces", assembler.renderRoot(
            new CEntityConstant(CEntityConstant.Value.SPACES),
            JavaTemplateRole.REFERENCE));
    }

    @Test
    void rendersIndexAndEnvironmentReferencesLikeTheDirectGenerator()
    {
        CJavaExternalDataStructure qualifier = new CJavaExternalDataStructure(
            1, "INDEX-OWNER", catalog, output);
        CJavaIndex index = new CJavaIndex("ITEM-INDEX", catalog, output);
        index.of = qualifier;
        CJavaEnvironmentVariable environment = new CJavaEnvironmentVariable(
            1, "RETURN-CODE", catalog, output, "getReturnCode()", "setReturnCode(", true);

        assertMatchesDirect(index);
        assertMatchesDirect(environment);
    }

    @Test
    void recursivelyRendersAddressReferences()
    {
        CJavaAddressReference address = new CJavaAddressReference(
            catalog, output, attribute("BUFFER-ADDRESS"));

        assertMatchesDirect(address);
    }

    private CJavaAttribute attribute(String name)
    {
        return new CJavaAttribute(1, name, catalog, output);
    }

    private LegacyNumberFixture number(String value)
    {
        return new LegacyNumberFixture(catalog, value);
    }

    private CBaseEntityExpression expression(CDataEntity value)
    {
        return new LegacyTerminalFixture(value);
    }

    private void assertMatchesDirect(CDataEntity value)
    {
        assertEquals(value.ExportReference(1), assembler.renderRoot(value, JavaTemplateRole.REFERENCE), value.getClass().getName());
    }
}
