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
public class CEntityLengthOf extends CBaseEntityFunction
{
    /** Creates a new centity length of instance. */
    public CEntityLengthOf(CObjectCatalog cat, CDataEntity data)
    {
        super(cat, data);
    }

    public boolean isValNeeded()
    {
        return true;
    }
}
