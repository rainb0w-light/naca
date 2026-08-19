/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.misc;

import java.util.HashMap;

/**
 * @author u930di
 *
 */
public class SemanticContextDef
{
    /** Creates a new semantic context def instance. */
    public SemanticContextDef()
    {
    }

    /** Returns the semantic context value definition. */
    public String getSemanticContextValueDefinition(String csTable, String csCol)
    {
        String csTableColName = SemanticContextDef.getTableColName(csTable, csCol);
        return getSemanticContextValueDefinition(csTableColName);
    }

    /** Returns the semantic context value definition. */
    public String getSemanticContextValueDefinition(String csTableColName)
    {
        String csSemanticContext = hashDBSemanticContext.get(csTableColName);
        return csSemanticContext;
    }

    /** Sets the semantic context value definition. */
    public void setSemanticContextValueDefinition(String csTable, String csCol, String csSemanticContext)
    {
        String csTableColName = SemanticContextDef.getTableColName(csTable, csCol);
        hashDBSemanticContext.put(csTableColName, csSemanticContext);
    }


    /** Returns the table col name. */
    static public String getTableColName(String csTable, String csCol)
    {
        return csTable + "/" + csCol;
    }

    private HashMap<String, String> hashDBSemanticContext = new HashMap<String, String>();
}
