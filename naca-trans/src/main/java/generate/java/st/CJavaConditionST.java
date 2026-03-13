/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import org.stringtemplate.v4.ST;
import semantic.CEntityCondition;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityCondition.
 * Migration from: generate.java.CJavaCondition
 */
public class CJavaConditionST extends CEntityCondition
{
    public CJavaConditionST(int l, CObjectCatalog cat, CBaseLanguageExporter out)
    {
        super(l, cat, out);
    }

    @Override
    protected void DoExport()
    {
        if (getCondition() == null)
        {
            return;
        }
        
        if (getCondition().ignore())
        {
            handleIgnoredCondition();
            return;
        }

        renderConditionWithTemplate();
    }
    
    private void handleIgnoredCondition()
    {
        if (getElseBloc() != null && !getElseBloc().ignore())
        {
            StartOutputBloc();
            WriteLine("{", getElseBloc().getLine());
            DoExport(getElseBloc());
            WriteLine("}");
            EndOutputBloc();
        }
    }
    
    private void renderConditionWithTemplate()
    {
        getCondition().SetLine(getLine());
        
        WriteWord("if (");
        String conditionStr = getCondition().Export();
        WriteWord(conditionStr + ") {");
        WriteEOL();
        
        StartOutputBloc();
        DoExport(getThenBloc());
        EndOutputBloc();
        
        int endLine = getThenBloc().GetEndLine();
        if (endLine == 0 && getElseBloc() != null)
        {
            endLine = getElseBloc().getLine() - 1;
        }
        WriteLine("}", endLine);
        
        if (getElseBloc() != null)
        {
            WriteLine("else {", getElseBloc().getLine());
            StartOutputBloc();
            DoExport(getElseBloc());
            EndOutputBloc();
            WriteLine("}", getElseBloc().GetEndLine());
        }
    }
}