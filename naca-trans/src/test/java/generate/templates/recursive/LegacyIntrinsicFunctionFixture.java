package generate.templates.recursive;

import generate.CBaseLanguageExporter;
import java.util.List;
import java.util.stream.Collectors;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityIntrinsicFunction;
import utils.CObjectCatalog;

/**
 * Test-only snapshot of the retired direct intrinsic-function backend.
 */
final class LegacyIntrinsicFunctionFixture extends CEntityIntrinsicFunction
{
    LegacyIntrinsicFunctionFixture(
        CObjectCatalog catalog,
        CBaseLanguageExporter output,
        String functionName,
        List<CBaseEntityExpression> arguments)
    {
        super(catalog, functionName, arguments);
        setLanguageExporter(output);
    }

    @Override
    public String ExportReference(int line)
    {
        String renderedArguments = getArguments().stream()
            .map(CBaseEntityExpression::Export)
            .collect(Collectors.joining(", "));
        return "CobolIntrinsicFunctions." + getNormalizedFunctionName()
            + "(" + renderedArguments + ")";
    }
}
