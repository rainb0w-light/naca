package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import utils.CObjectCatalog;

class BmsMapsetRootRenderTest
{
    @Test
    void completeMapsetRendersRecursivelyWithoutLegacyTraversal()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, null);
        CEntityResourceFormContainer mapset =
            factory.NewEntityFormContainer(1, "TESTMAP", false);
        CEntityResourceForm form = factory.NewEntityForm(2, "SCREEN-ONE", false);
        form.setResourceName("SCREEN-ONE");
        form.SetSize(80, 24);
        CEntityResourceField field = factory.NewEntityEntryField(3, "CUSTOMER-ID");
        field.SetDisplayName("CUSTOMER-ID");
        field.SetTypeString(12);
        form.AddField(field);
        mapset.AddForm(form);

        String source = TemplateLoader.getRecursiveAssembler()
            .renderRoot(mapset, JavaTemplateRole.ROOT);

        assertTrue(source.contains("class TESTMAP extends Map"), source);
        assertTrue(source.contains(
            "Form SCREEN_ONE = declare.form(\"SCREEN_ONE\", 24, 80) ;"), source);
        assertTrue(source.contains(
            "Edit CUSTOMER_ID = declare.edit(\"CUSTOMER_ID\", 12).edit() ;"), source);
        assertFalse(source.contains("[UNDEFINED]"), source);
    }
}
