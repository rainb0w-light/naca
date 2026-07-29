package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.fixtures.LegacyAttributeFixture;
import generate.java.CJavaExporter;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityAddTo;
import utils.CObjectCatalog;

class JavaSemanticAddRendererTest {
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final CJavaExporter refs = new CJavaExporter(null, "/tmp/unused.java", null, false);
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersIncrementDecrementAndNormalAddFromSemanticModel() {
        assertEquals("inc(total) ;", render("1"));
        assertEquals("dec(total) ;", render("-1"));
        assertEquals("inc(7, total) ;", render("7"));
    }

    private String render(String value) {
        CEntityAddTo add = new CEntityAddTo(0, catalog);
        add.SetAddValue(new LegacyNumberFixture(catalog, value));
        add.SetAddDest(new LegacyAttributeFixture(0, "TOTAL", catalog, refs));
        return assembler.renderRoot(add, JavaTemplateRole.REFERENCE).replaceAll("\\$\\d+", "").stripTrailing();
    }
}
