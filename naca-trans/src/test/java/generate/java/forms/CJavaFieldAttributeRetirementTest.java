package generate.java.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.forms.CEntityFieldAttribute;
import semantic.forms.CEntityResourceField;
import utils.CObjectCatalog;

/** Retirement proof for the BMS field-attribute reference backend. */
class CJavaFieldAttributeRetirementTest
{
    @Test
    void factoryBuildsPureSemanticReferenceAndRuntimeBackedTemplate()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityResourceField owner = BmsJavaEntities.entryField(1, "CUSTOMER-FIELD", catalog, null);
        CEntityFieldAttribute attribute = new CJavaEntityFactory(catalog, null)
            .NewEntityFieldAttribute(1, "CUSTOMER-FIELDA", owner);

        assertEquals(CEntityFieldAttribute.class, attribute.getClass());
        assertEquals(CDataEntity.CDataEntityType.FIELD_ATTRIBUTE, attribute.GetDataType());
        assertFalse(attribute.HasAccessors());
        assertTrue(attribute.isValNeeded());
        assertEquals("CUSTOMER_FIELD.getAttribute()", TemplateLoader.getRecursiveAssembler()
            .renderRoot(attribute, JavaTemplateRole.REFERENCE));
    }
}
