package generate.templates.recursive;

import generate.CBaseLanguageExporter;
import java.util.stream.Collectors;
import semantic.CDataEntity;
import semantic.expression.CEntityList;
import utils.CObjectCatalog;

/**
 * Test-only snapshot of the retired direct list backend.
 */
final class LegacyListFixture extends CEntityList
{
    LegacyListFixture(
        String name,
        CObjectCatalog catalog,
        CBaseLanguageExporter output)
    {
        super(name, catalog);
        setLanguageExporter(output);
    }

        public String ExportReference(int line)
    {
        if (isEmpty())
        {
            return "null";
        }
        String type = isStringElements()
            ? "String"
            : isFieldElements() ? "Edit" : isNumberElements() ? "int" : "Var";
        return "new " + type + "[] {"
            + getData().stream()
                .map(value -> generate.LegacyDataRenderer.renderReference(value, getLine()))
                .collect(Collectors.joining(", "))
            + "}";
    }
}
