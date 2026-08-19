/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityExec extends CBaseActionEntity
{

    /**
     * @param cat
     */
    public CEntityExec(int l, CObjectCatalog cat, String statement)
    {
        super(l, cat);
        csStatement = statement ;
    }

    protected String csStatement = "" ;
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }

    public String getStatement() {
        return csStatement;
    }
}
