/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import semantic.Verbs.CEntityAssign;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityAssign.
 * Migration from: generate.java.CJavaAssign
 *
 * Renders through the recursive assembler: the entity binding
 * (CEntityAssign=recursiveMoveEntity) drives a clean template and child
 * references are unfolded by the recursive model adaptor.
 */
public class CJavaAssignST extends CEntityAssign
{
    public CJavaAssignST(int l, CObjectCatalog cat, CBaseLanguageExporter out)
    {
        super(l, cat);
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