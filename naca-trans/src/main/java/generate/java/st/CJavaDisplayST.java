/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import semantic.Verbs.CEntityDisplay;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityDisplay (DISPLAY).
 * Renders through the recursive assembler (CEntityDisplay=display).
 */
public class CJavaDisplayST extends CEntityDisplay
{
    public CJavaDisplayST(int l, CObjectCatalog cat, CBaseLanguageExporter out, Upon t)
    {
        super(l, cat, t);
        setLanguageExporter(out);
    }

    @Override
    protected void DoExport()
    {
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(this);
        for (String line : rendered.split("\n", -1)) {
            if (!line.isEmpty()) {
                WriteLine(line);
            }
        }
    }
}
