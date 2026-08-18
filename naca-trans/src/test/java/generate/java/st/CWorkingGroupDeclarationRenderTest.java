package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CBaseLanguageEntity;
import semantic.CEntityClass;

class CWorkingGroupDeclarationRenderTest
{
    private static final String PROGRAM = """
                 IDENTIFICATION DIVISION.
                 PROGRAM-ID. GROUPTEST.
                 DATA DIVISION.
                 WORKING-STORAGE SECTION.
                 01 CARDFILE-STATUS.
                    05 CARDFILE-STAT1 PIC X.
                    05 CARDFILE-STAT2 PIC X.
                 01 UNUSED-GROUP.
                    05 UNUSED-CHILD PIC X.
                 PROCEDURE DIVISION.
                     MOVE "00" TO CARDFILE-STATUS.
                     STOP RUN.
           """;

    @Test
    void referencedGroupKeepsCompleteStorageLayoutAndCompiles()
    {
        assertDoesNotThrow(this::assertReferencedGroup, "referenced group layout should compile");
    }

    private void assertReferencedGroup() throws Exception
    {
        String rendered = CobolRenderTestSupport.render(PROGRAM);
        String group = "Var CARDFILE_STATUS = declare.level(1).var() ;";
        String first = "Var CARDFILE_STAT1 = declare.level(05).picX(1).var() ;";
        String second = "Var CARDFILE_STAT2 = declare.level(05).picX(1).var() ;";
        assertEquals(1, CobolRenderTestSupport.count(rendered, group),
            "group declaration count");
        assertEquals(1, CobolRenderTestSupport.count(rendered, first),
            "first child declaration count");
        assertEquals(1, CobolRenderTestSupport.count(rendered, second),
            "second child declaration count");
        assertTrue(rendered.indexOf(group) < rendered.indexOf(first), "group precedes first child");
        assertTrue(rendered.indexOf(first) < rendered.indexOf(second), "children preserve order");
        CobolRenderTestSupport.compile(rendered);
    }

    @Test
    void ignoredIndependentGroupIsExcludedByParentSelection()
    {
        assertDoesNotThrow(this::assertIgnoredGroup, "ignored group should remain excluded");
    }

    private void assertIgnoredGroup() throws Exception
    {
        CEntityClass root = CobolRenderTestSupport.parse(PROGRAM);
        CBaseLanguageEntity unused = find(root, "UNUSED-GROUP");
        assertNotNull(unused, "unused group should be present in semantic tree");
        unused.SetIgnoreStructure();
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(
            root, JavaTemplateRole.ROOT);
        assertEquals(0, CobolRenderTestSupport.count(rendered, "UNUSED_GROUP"),
            "unused group should be omitted");
        assertEquals(0, CobolRenderTestSupport.count(rendered, "UNUSED_CHILD"),
            "unused child should be omitted");
    }

    private static CBaseLanguageEntity find(CBaseLanguageEntity entity, String name)
    {
        if (name.equals(entity.GetName()))
        {
            return entity;
        }
        for (CBaseLanguageEntity child : entity.getChildren())
        {
            CBaseLanguageEntity found = find(child, name);
            if (found != null)
            {
                return found;
            }
        }
        return null;
    }

}
