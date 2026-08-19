/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityMoveReference extends CBaseActionEntity
{
    /**
     * @param line
     * @param cat
     */
    public CEntityMoveReference(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    /** Sets the move reference. */
    public void SetMoveReference(CEntityAddressReference from, CEntityAddressReference to)
    {
        this.from = from ;
        this.to = to;
    }
    protected CEntityAddressReference from = null ;
    protected CEntityAddressReference to = null ;

    public CEntityAddressReference getFrom()
    {
        return from ;
    }

    public CEntityAddressReference getTo()
    {
        return to ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return from.ignore() || to.ignore();
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        from.Clear() ;
        to.Clear() ;
        from = null ;
        to = null ;
    }
}
