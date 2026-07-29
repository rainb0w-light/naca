package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityBloc;
import semantic.CEntityProcedureDivision;
import semantic.Verbs.CEntityContinue;
import utils.CObjectCatalog;

class CEntityProcedureDivisionRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityProcedureDivision division)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(division, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityProcedureDivision.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityProcedureDivision(1).getClass());
        assertEquals(CEntityProcedureDivision.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityProcedureDivision(1).getClass());
    }

    @Test
    void recursivelyRendersParametersAndProcedureBody()
    {
        CEntityProcedureDivision division = new CEntityProcedureDivision(1, catalog);
        division.AddCallParameter(new MockDataEntity(1, "ARG"));
        CEntityBloc body = new CEntityBloc(1, catalog);
        body.AddChild(new CEntityContinue(1, catalog));
        division.SetProcedureBloc(body);

        assertEquals(
            "ParamDeclaration callParameters = declare.using(ARG);\n"
                + "public void procedureDivision() {\n"
                + "// CONTINUE \n"
                + "}",
            render(division));
    }
}
