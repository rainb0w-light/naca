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
import semantic.Verbs.CEntityAssign;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityAssign.
 * Migration from: generate.java.CJavaAssign
 */
public class CJavaAssignST extends CEntityAssign
{
    public CJavaAssignST(int l, CObjectCatalog cat, CBaseLanguageExporter out)
    {
        super(l, cat, out);
    }

    @Override
    protected void DoExport()
    {
        ST template = TemplateLoader.getVerbsTemplate("assign");
        template.add("entity", this);
        
        String output = template.render();
        WriteLine(output);
    }
}