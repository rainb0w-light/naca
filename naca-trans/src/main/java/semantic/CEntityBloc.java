/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityBloc extends CBaseLanguageEntity
{
    /**
     * @param name
     * @param cat
     * @param out
     */
    public CEntityBloc(int l, CObjectCatalog cat)
    {
        super(l, "", cat);
    }

    /* (non-Javadoc)
     * @see semantic.CBaseSemanticEntity#RegisterMySelfToCatalog()
     */
    protected void RegisterMySelfToCatalog()
    {
        // NOTHING
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return isChildrenIgnored();
    }

    /** Sets the end line. */
    public void SetEndLine(int n)
    {
        nEndLine = n ;
    }
    protected int nEndLine = 0 ;

    /** Returns the actions. */
    public List<CBaseActionEntity> getActions()
    {
        List<CBaseActionEntity> actions = new ArrayList<CBaseActionEntity>();
        for (CBaseLanguageEntity child : lstChildren)
        {
            if (child instanceof CBaseActionEntity)
            {
                actions.add((CBaseActionEntity)child);
            }
        }
        return Collections.unmodifiableList(actions);
    }

    /** Returns the active actions. */
    public List<CBaseActionEntity> getActiveActions()
    {
        List<CBaseActionEntity> actions = new ArrayList<CBaseActionEntity>();
        for (CBaseLanguageEntity child : lstChildren)
        {
            if (child instanceof CBaseActionEntity && !child.ignore())
            {
                actions.add((CBaseActionEntity)child);
            }
        }
        return Collections.unmodifiableList(actions);
    }

    /** Executes the get end line operation. */
    public int GetEndLine()
    {
        return nEndLine ;
    }
    /** Updates the action. */
    public boolean UpdateAction(CBaseActionEntity entity, CBaseActionEntity newCond)
    {
        for (int i=0; i<lstChildren.size(); i++)
        {
            CBaseActionEntity act = (CBaseActionEntity)lstChildren.get(i) ;
            if (act == entity)
            {
                lstChildren.set(i, newCond) ;
                return true ;
            }
        }
        return false ;
    }

    /**
     * @return
     */
    public boolean hasExplicitGetOut()
    {
        if (lstChildren.isEmpty())
        {
            return false ;
        }
        CBaseActionEntity le = (CBaseActionEntity)lstChildren.getLast() ;
        return le.hasExplicitGetOut() ;
    }
}
