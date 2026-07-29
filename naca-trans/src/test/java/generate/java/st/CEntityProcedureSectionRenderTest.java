package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityBloc;
import semantic.CEntityProcedureSection;
import semantic.Verbs.CEntityContinue;
import utils.CObjectCatalog;

class CEntityProcedureSectionRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityProcedureSection section)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(section, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityProcedureSection.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityProcedureSection(1, "MAIN-SECTION").getClass());
        assertEquals(CEntityProcedureSection.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityProcedureSection(1, "MAIN-SECTION").getClass());
    }

    @Test
    void recursivelyRendersAndClosesSectionBody()
    {
        CEntityProcedureSection section = new CEntityProcedureSection(
            1, "MAIN-SECTION", catalog);
        CEntityBloc body = new CEntityBloc(1, catalog);
        body.AddChild(new CEntityContinue(1, catalog));
        section.SetSectionBloc(body);

        assertEquals(
            "Section MAIN_SECTION = new Section(this);\n"
                + "public void MAIN_SECTION() {\n"
                + "// CONTINUE \n"
                + "}",
            render(section));
    }

    @Test
    void reducedSectionRendersAsParagraph()
    {
        CEntityProcedureSection section = new CEntityProcedureSection(
            1, "MAIN-SECTION", catalog);
        section.SetSectionBloc(new CEntityBloc(1, catalog));
        section.ReduceToProcedure();

        assertEquals(
            "Paragraph MAIN_SECTION = new Paragraph(this);\n"
                + "public void MAIN_SECTION() {\n"
                + "}",
            render(section));
    }
}
