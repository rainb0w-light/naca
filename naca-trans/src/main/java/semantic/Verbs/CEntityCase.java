/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityCase extends CBaseActionEntity
{

    /**
     * @param cat
     * @param out
     */
    public CEntityCase(int l, CObjectCatalog cat, int nEndLine)
    {
        super(l, cat);
        nEndBlocLine = nEndLine ;
    }
    /** Sets the condition. */
    public void SetCondition(CBaseEntityCondition exp)
    {
        //ASSERT(exp);
        condition = exp ;
    }

    protected CBaseEntityCondition condition = null  ;
    protected int nEndBlocLine = 0 ;

    public CBaseEntityCondition getCondition()
    {
        return condition;
    }

    public boolean isOtherwise()
    {
        return condition == null;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        if (condition!=null)
        {
            condition.Clear() ;
        }
        condition = null ;
    }

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        if (condition != null)
        {
            boolean ignore = condition.ignore() ;
            //ignore |= isChildrenIgnored() ;
            return ignore ;
        }
        else
        {
            return isChildrenIgnored() ;
        }
    }
    /** Executes the ignore variable operation. */
    public boolean IgnoreVariable(CDataEntity data)
    {
        return false ;
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

}
