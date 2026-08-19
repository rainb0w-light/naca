/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils;

import semantic.CBaseEntityFactory;

/** Provides cbase recusrive special action behavior. */
public abstract class CBaseRecusriveSpecialAction
{
    protected CObjectCatalog programCatalog = null ;
    protected CBaseEntityFactory factory = null ;

    /** Creates a new cbase recusrive special action instance. */
    public CBaseRecusriveSpecialAction(CObjectCatalog programCatalog, CBaseEntityFactory factory)
    {
        this.programCatalog = programCatalog ;
        this.factory = factory ;
    }

}
