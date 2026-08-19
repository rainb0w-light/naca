/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

//import org.w3c.dom.Element;

import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public abstract class CBaseResourceEntity extends CBaseExternalEntity
{

    /**
     * @param name
     * @param cat
     */
    public CBaseResourceEntity(int l, String name, CObjectCatalog cat)
    {
        super(l, name, cat);
    }

//  public abstract Element DoXMLExport() ;
    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#HasAccessors()
     */
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return false;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }

}
