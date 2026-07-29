package generate.fpacjava;

import generate.CBaseLanguageExporter;
import java.util.List;
import java.util.stream.Collectors;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityIntrinsicFunction;
import utils.CObjectCatalog;

/**
 * FPac compatibility renderer. FPac remains a separate generator pipeline and
 * is intentionally outside the COBOL recursive-assembler migration.
 */
public final class CFPacJavaIntrinsicFunction extends CEntityIntrinsicFunction
{
    public CFPacJavaIntrinsicFunction(
        CObjectCatalog catalog,
        CBaseLanguageExporter output,
        String functionName,
        List<CBaseEntityExpression> arguments)
    {
        super(catalog, functionName, arguments);
        setLanguageExporter(output);
    }

    public String ExportReference(int line)
    {
        String renderedArguments = getArguments().stream()
            .map(generate.LegacyExpressionRenderer::render)
            .collect(Collectors.joining(", "));
        return "CobolIntrinsicFunctions." + getNormalizedFunctionName()
            + "(" + renderedArguments + ")";
    }
}
