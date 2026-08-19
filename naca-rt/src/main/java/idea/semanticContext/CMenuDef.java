/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.semanticContext;

import java.util.ArrayList;

/**
 * @author U930DI
 *
 */
public class CMenuDef
{
    CMenuDef()
    {
    }

    void setTitle(String csTitle)
    {
        this.csTitle = csTitle;
    }

    CMenuOptionDef createAndRegisterNewOption()
    {
        CMenuOptionDef MenuOptionDef = new CMenuOptionDef();
        options.add(MenuOptionDef);
        return MenuOptionDef;
    }

    public String buildHTMLMenu()
    {
        String cs = "<H1><CENTER>"+csTitle+"</CENTER></H1><BR><table>";
        for(int n = 0; n< options.size(); n++)
        {
            CMenuOptionDef MenuOptionDef = options.get(n);
            cs += "<tr><td><BUTTON TYPE=SUBMIT>" + MenuOptionDef.csLabel + "</BUTTON></td></tr>";
            // use MenuOptionDef.csAction to identify option
        }
        cs += "</table>";
        return cs;
    }

    ArrayList<CMenuOptionDef> options = new ArrayList<CMenuOptionDef>();    // Array of  MenuOptionDef
    public String csTitle = null;
}
