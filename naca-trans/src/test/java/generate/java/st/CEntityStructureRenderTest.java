package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityAttribute;
import semantic.CEntityDataSection;
import semantic.CEntityStructure;
import utils.CObjectCatalog;

class CEntityStructureRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityStructure.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityStructure(1, "WS-GROUP", "01").getClass());
        assertEquals(CEntityStructure.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityStructure(1, "WS-GROUP", "01").getClass());
    }

    @Test
    void assemblerSeparatesReferenceAndRecursiveDeclaration()
    {
        CEntityStructure structure =
            new CEntityStructure(1, "WS-GROUP", catalog, "01");
        CEntityAttribute child =
            new CEntityAttribute(2, "WS-VALUE", catalog);
        child.SetLevel("05");
        child.SetTypeString(8);
        CEntityDataSection fileSection =
            new CEntityDataSection(0, "FileSection", catalog);
        fileSection.AddChild(structure);
        structure.AddChild(child);

        assertEquals(
            "Var WS_VALUE = declare.level(05).picX(8).var() ;",
            normalize(TemplateLoader.getRecursiveAssembler()
                .renderRoot(child, JavaTemplateRole.DECLARATION)));
        assertEquals("WS_GROUP", TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, JavaTemplateRole.REFERENCE));
        assertEquals(
            "Var WS_GROUP = declare.level(1).var() ; "
                + "Var WS_VALUE = declare.level(05).picX(8).var() ;",
            normalize(TemplateLoader.getRecursiveAssembler()
                .renderRoot(structure, JavaTemplateRole.DECLARATION)));
    }

    @Test
    void occursDimensionsComeFromTheSemanticTree()
    {
        CEntityStructure outer =
            new CEntityStructure(1, "OUTER-TABLE", catalog, "01");
        outer.SetTableSize(new MockDataEntity(1, catalog, null, "10"));
        CEntityStructure inner =
            new CEntityStructure(2, "INNER-TABLE", catalog, "05");
        inner.SetTableSize(new MockDataEntity(2, catalog, null, "20"));
        outer.AddChild(inner);

        assertEquals(1, outer.getNbDimOccurs());
        assertEquals(2, inner.getNbDimOccurs());
    }

    private static String normalize(String source)
    {
        return source.replaceAll("\\s+", " ").strip();
    }
}
