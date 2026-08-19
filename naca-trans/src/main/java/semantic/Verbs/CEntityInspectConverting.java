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

public class CEntityInspectConverting extends CBaseActionEntity
{
    public CEntityInspectConverting(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    public void Clear()
    {
        super.Clear() ;
        variable = null;
    }

    public boolean ignore()
    {
        return variable.ignore();
    }

    public void SetConvert(CDataEntity var)
    {
        variable = var;
    }

    public void SetFrom(CDataEntity var)
    {
        from = var;
    }

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
