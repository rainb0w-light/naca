/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.semanticContext;

/**
 * @author U930DI
 *
 */
public class CMenuOptionDef
{
    CMenuOptionDef()
    {
    }

    void setActionId(String csActionId)
    {
        this.csActionId = csActionId;
    }

    void setLabel(String csLabel)
    {
        this.csLabel = csLabel;
    }

    String csLabel = null;
    String csActionId = null;
}
