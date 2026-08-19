/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityLoopWhile extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     * @param out
     */
    public CEntityLoopWhile(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }
    public void SetWhileCondition(CBaseEntityCondition exp)
    {
        whileCondition = exp ;
        isdoBefore = false ;
    }
    public void SetDoWhileCondition(CBaseEntityCondition exp)
    {
        whileCondition = exp ;
        isdoBefore = true ;
    }
    public void SetDoUntilCondition(CBaseEntityCondition exp)
    {
        whileCondition = exp.GetOppositeCondition() ;
        isdoBefore = true;
    }
    public void SetUntilCondition(CBaseEntityCondition exp)
    {
        whileCondition = exp.GetOppositeCondition() ;
        isdoBefore = false ;
    }
    protected CBaseEntityCondition whileCondition = null ;
    protected boolean isdoBefore = false ; // false = WHILE DO / true = DO WHILE
    public void Clear()
    {
        super.Clear() ;
        whileCondition.Clear() ;
        whileCondition = null ;
    }
    public boolean ignore()
    {
        boolean ignore = whileCondition.ignore() ;
//      ignore |= (isChildrenIgnored() && m_;
        return ignore ;
    }
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

    // ==================== ST4 Template Accessors ====================

    public CBaseEntityCondition getWhileCondition()
    {
        return whileCondition;
    }

    public boolean isDoBefore()
    {
        return isdoBefore;
    }

}
