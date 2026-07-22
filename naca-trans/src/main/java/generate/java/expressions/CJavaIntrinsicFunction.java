/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.expressions;

import generate.CBaseLanguageExporter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityIntrinsicFunction;
import utils.CObjectCatalog;

/** Direct Java generator retained as the fallback path for intrinsic functions. */
public class CJavaIntrinsicFunction extends CEntityIntrinsicFunction
{
    public CJavaIntrinsicFunction(
        CObjectCatalog catalog,
        CBaseLanguageExporter output,
        String functionName,
        List<CBaseEntityExpression> arguments)
    {
        super(catalog, functionName, arguments);
        setLanguageExporter(output);
    }

    public String getRuntimeName()
    {
        return getFunctionName().toLowerCase(Locale.ROOT).replace('-', '_');
    }

    @Override
    public String ExportReference(int line)
    {
        String arguments = getArguments().stream()
            .map(CBaseEntityExpression::Export)
            .collect(Collectors.joining(", "));
        return "CobolIntrinsicFunctions." + getRuntimeName() + "(" + arguments + ")";
    }
}
