/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.Vector;
import utils.CObjectCatalog;


/**
 * @author sly
 *
 */
public abstract class CBaseActionEntity extends CBaseLanguageEntity
{
    /**
     * @param name
     * @param cat
     */
    /** Constructor for target-neutral semantic actions. */
    public CBaseActionEntity(int line, CObjectCatalog cat)
    {
        super(line, "", cat);
    }

    /* (non-Javadoc)
     * @see semantic.CBaseSemanticEntity#RegisterMySelfToCatalog()
     */
    protected void RegisterMySelfToCatalog()
    {
        // nothing
    }

    /** Executes the ignore variable operation. */
    public boolean IgnoreVariable(CDataEntity data)
    {
        // nothing
        return false ;
    }

    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        // nothing
        return false ;
    }

    /**
     * @param val
     * @param factory
     * @return
     */
    public CBaseActionEntity GetSpecialAssignement(String val, CBaseEntityFactory factory)
    {
        // have to be overwritten
        return null;
    }

    /**
     * @param newCond
     */
    public boolean Replace(CBaseActionEntity newCond)
    {
        // have to be overwritten
        return parent.UpdateAction(this, newCond) ;
    }

    /**
     * @return
     */
    public CDataEntity getValueAssigned()
    {
        // have to be overwritten
        return null;
    }
    public Vector getVarsAssigned()
    {
        // have to be overwritten
        return null;
    }

    /**
     * @return
     */
    public boolean hasExplicitGetOut()
    {
        return false ;
    }
}
