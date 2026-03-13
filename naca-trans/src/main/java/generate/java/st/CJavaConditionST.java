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
 * 
 * Design Principle (PUSH Model):
 * - Controller pushes entity to template
 * - Template handles ALL logic, formatting, and nested rendering
 * - No conditionals or string concatenation in Controller
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
        // Pure PUSH model - no logic, just push entity to template
        ST template = TemplateLoader.getControlTemplate("condition");
        template.add("entity", this);
        
        String output = template.render();
        if (!output.trim().isEmpty()) {
            WriteLine(output);
        }
    }
}