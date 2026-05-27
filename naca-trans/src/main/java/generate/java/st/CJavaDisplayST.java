/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.st;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityDisplay;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityDisplay.
 * Migration from: generate.java.CJavaDisplay
 * 
 * Handles CONSOLE/ENVINONMENT/DEFAULT upon cases manually,
 * since the template would need conditional logic (against ST4 philosophy).
 * 
 * Could be fully templated if we add uponType accessor and conditional templates.
 */
public class CJavaDisplayST extends CEntityDisplay
{
    public CJavaDisplayST(int l, CObjectCatalog cat, CBaseLanguageExporter out, Upon t)
    {
        super(l, cat, out, t);
    }

    @Override
    protected void DoExport()
    {
        StringBuilder sb = new StringBuilder();
        
        if (upon == Upon.CONSOLE) {
            sb.append("console().display(");
        } else if (upon == Upon.ENVINONMENT) {
            sb.append("displayEnv(");
        } else {
            sb.append("display(");
        }
        
        for (int i = 0; i < itemsToDisplay.size(); i++) {
            CDataEntity e = itemsToDisplay.get(i);
            if (i != 0) {
                sb.append(" + ");
            }
            sb.append(e.ExportReference(getLine()));
        }
        
        sb.append(");");
        WriteLine(sb.toString());
    }
}
