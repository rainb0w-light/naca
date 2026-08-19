/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityDigits extends CBaseEntityFunction
{

    /**
     * @param cat
     * @param data
     */
    public CEntityDigits(CObjectCatalog cat, CDataEntity data1)
    {
        super(cat, data1);
    }
    public boolean isValNeeded()
    {
        return false;
    }
}
