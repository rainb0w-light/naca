package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityNamedCondition;
import utils.CObjectCatalog;

class CEntityNamedConditionRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityNamedCondition.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityNamedCondition(1, "VALID-STATUS").getClass());
        assertEquals(CEntityNamedCondition.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityNamedCondition(1, "VALID-STATUS").getClass());
    }

    @Test
    void assemblerSeparatesReferenceAndDeclarationAndKeepsUndefinedValues()
    {
        CEntityNamedCondition condition =
            new CEntityNamedCondition(1, "VALID-STATUS", catalog);
        condition.AddValue(null);
        condition.AddValue(new MockDataEntity(1, "1"));
        condition.AddInterval(
            new MockDataEntity(1, "2"), new MockDataEntity(1, "9"));

        assertEquals("VALID_STATUS", TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE));
        String declaration = TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.DECLARATION);
        assertTrue(declaration.contains(".value([undefined])"), declaration);
        assertTrue(declaration.contains(".value(1)"), declaration);
        assertTrue(declaration.contains(".value(2, 9)"), declaration);
    }
}
