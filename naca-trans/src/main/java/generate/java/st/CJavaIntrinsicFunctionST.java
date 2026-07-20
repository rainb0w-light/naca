/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.java.expressions.CJavaIntrinsicFunction;
import generate.templates.TemplateLoader;
import java.util.List;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

/**
 * ST4 expression renderer for a generic COBOL intrinsic function invocation.
 * Renders through the recursive assembler
 * (CEntityIntrinsicFunction=recursiveIntrinsicFunctionEntity); the arguments
 * are unfolded by the recursive model adaptor.
 */
public class CJavaIntrinsicFunctionST extends CJavaIntrinsicFunction
{
    public CJavaIntrinsicFunctionST(
        CObjectCatalog catalog,
        CBaseLanguageExporter output,
        String functionName,
        List<CBaseEntityExpression> arguments)
    {
        super(catalog, output, functionName, arguments);
    }

    @Override
    public String ExportReference(int line)
    {
        return TemplateLoader.getRecursiveAssembler().renderRoot(this).trim();
    }
}
