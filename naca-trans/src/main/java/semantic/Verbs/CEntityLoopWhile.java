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
    /** Sets the while condition. */
    public void SetWhileCondition(CBaseEntityCondition exp)
    {
        whileCondition = exp ;
        isdoBefore = false ;
    }
    /** Sets the do while condition. */
    public void SetDoWhileCondition(CBaseEntityCondition exp)
    {
        whileCondition = exp ;
        isdoBefore = true ;
    }
    /** Sets the do until condition. */
    public void SetDoUntilCondition(CBaseEntityCondition exp)
    {
        whileCondition = exp.GetOppositeCondition() ;
        isdoBefore = true;
    }
    /** Sets the until condition. */
    public void SetUntilCondition(CBaseEntityCondition exp)
    {
        whileCondition = exp.GetOppositeCondition() ;
        isdoBefore = false ;
    }
    protected CBaseEntityCondition whileCondition = null ;
    protected boolean isdoBefore = false ; // false = WHILE DO / true = DO WHILE
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        whileCondition.Clear() ;
        whileCondition = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        boolean ignore = whileCondition.ignore() ;
//      ignore |= (isChildrenIgnored() && m_;
        return ignore ;
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
