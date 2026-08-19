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
public class CEntityContinue extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     * @param out
     */
    public CEntityContinue(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;// maybe true
    }
}
