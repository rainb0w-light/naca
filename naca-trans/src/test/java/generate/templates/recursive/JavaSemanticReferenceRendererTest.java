package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.fixtures.LegacyAttributeFixture;
import generate.fixtures.LegacyIndexFixture;
import generate.fixtures.LegacyStructureFixture;
import generate.java.CJavaExporter;
import generate.fixtures.LegacyExternalDataStructureFixture;
import generate.java.CJavaSubStringReference;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CEntityAddressReference;
import semantic.CEntityArrayReference;
import semantic.CEntityEnvironmentVariable;
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
        LegacyAttributeFixture ordinary = attribute("WS-VALUE");
        LegacyAttributeFixture reserved = attribute("NEW");
        LegacyExternalDataStructureFixture qualifier = new LegacyExternalDataStructureFixture(
            1, "COPY-BOOK", catalog, output);
        LegacyAttributeFixture qualified = attribute("COPY-FIELD");
        qualified.of = qualifier;

        assertMatchesDirect(ordinary);
        assertMatchesDirect(reserved);
        assertMatchesDirect(qualifier);
        assertMatchesDirect(qualified);
    }

    @Test
    void usesTheSemanticDisplayNameForStructureReferences()
    {
        LegacyStructureFixture structure = new LegacyStructureFixture(
            1, "ORIGINAL-NAME", catalog, output, "01");
        structure.SetDisplayName("DISPLAY-NAME");

        assertMatchesDirect(structure);
    }

    @Test
    void recursivelyRendersArrayIndexesAndSubstringBounds()
    {
        CEntityArrayReference array = new CEntityArrayReference(1, catalog);
        array.SetReference(attribute("TABLE-VALUE"));
        array.AddIndex(expression(number("1")));
        array.AddIndex(expression(number("00002")));

        assertEquals("table_Value.getAt(1, 2)",
            assembler.renderRoot(array, JavaTemplateRole.REFERENCE));

        LegacyArrayReferenceFixture legacyArray = new LegacyArrayReferenceFixture(1, catalog);
        legacyArray.SetReference(attribute("TABLE-VALUE"));
        legacyArray.AddIndex(expression(number("1")));
        legacyArray.AddIndex(expression(number("00002")));
        CJavaSubStringReference substring = new CJavaSubStringReference(1, catalog, output);
        substring.SetReference(legacyArray, expression(number("3")), expression(number("4")));

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
        LegacyExternalDataStructureFixture qualifier = new LegacyExternalDataStructureFixture(
            1, "INDEX-OWNER", catalog, output);
        LegacyIndexFixture index =
            new LegacyIndexFixture("ITEM-INDEX", catalog, output);
        index.of = qualifier;
        CEntityEnvironmentVariable environment = new CEntityEnvironmentVariable(
            1, "RETURN-CODE", catalog, "getReturnCode()", "setReturnCode(", true);

        assertMatchesDirect(index);
        assertEquals("getReturnCode()",
            assembler.renderRoot(environment, JavaTemplateRole.REFERENCE));
    }

    @Test
    void recursivelyRendersAddressReferences()
    {
        CEntityAddressReference address = new CEntityAddressReference(
            catalog, attribute("BUFFER-ADDRESS"));

        assertEquals("addressOf(buffer_Address)",
            assembler.renderRoot(address, JavaTemplateRole.REFERENCE));
    }

    private LegacyAttributeFixture attribute(String name)
    {
        return new LegacyAttributeFixture(1, name, catalog, output);
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
