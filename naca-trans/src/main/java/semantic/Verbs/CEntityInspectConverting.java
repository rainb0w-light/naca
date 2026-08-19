/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/** Provides centity inspect converting behavior. */
public class CEntityInspectConverting extends CBaseActionEntity
{
    /** Creates a new centity inspect converting instance. */
    public CEntityInspectConverting(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        variable = null;
    }

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return variable.ignore();
    }

    /** Sets the convert. */
    public void SetConvert(CDataEntity var)
    {
        variable = var;
    }

    /** Sets the from. */
    public void SetFrom(CDataEntity var)
    {
        from = var;
    }

    /** Sets the to. */
    public void SetTo(CDataEntity var)
    {
        to = var;
    }

    protected CDataEntity variable = null ;
    protected CDataEntity from = null ;
    protected CDataEntity to = null ;
    public CDataEntity getVariable() {
        return variable;
    }
    public CDataEntity getFrom() {
        return from;
    }
    public CDataEntity getTo() {
        return to;
    }
}
