package generate.templates.recursive;

import semantic.CEntityArrayReference;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

final class LegacyArrayReferenceFixture extends CEntityArrayReference
{
    LegacyArrayReferenceFixture(int line, CObjectCatalog catalog)
    {
        super(line, catalog);
    }

    @Override
    public String ExportReference(int line)
    {
        if (reference == null || arrIndexes.isEmpty())
        {
            return "";
        }
        StringBuilder result = new StringBuilder(reference.ExportReference(line));
        result.append(".getAt(");
        for (int i = 0; i < arrIndexes.size(); i++)
        {
            if (i > 0)
            {
                result.append(", ");
            }
            CBaseEntityExpression index = arrIndexes.get(i);
            result.append(generate.LegacyExpressionRenderer.render(index));
        }
        return result.append(")").toString();
    }
}
