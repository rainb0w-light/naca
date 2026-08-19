/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;



/**
 * @author sly
 *
 *     Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityCondNot extends CBaseEntityCondition
{
    /** Executes the get opposite condition operation. */
    public CBaseEntityCondition GetOppositeCondition()
    {
        return cond;
    }

    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 6;
    }

    protected CBaseEntityCondition cond ;

    public CBaseEntityCondition getOperand()
    {
        return cond;
    }

    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        cond.Clear() ;
        cond = null ;
    }

    /** Sets the condition. */
    public void SetCondition(CBaseEntityCondition cond)
    {
        ASSERT(cond);
        this.cond = cond ;
        cond.SetParent(this);
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return cond.ignore();
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        return cond.ReplaceVariable(field, var) ;
    }
    /** Executes the get special condition replacing operation. */
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        CBaseEntityCondition condNew = cond.GetSpecialConditionReplacing(val, fact, replace);
        CBaseEntityCondition notcond = condNew.GetOppositeCondition() ;
        if (notcond == null)
        {
            CEntityCondNot notCond = fact.NewEntityCondNot() ;
            notCond.SetCondition(condNew);
        }
        return notcond ;
    }
//  public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
//  {
//      CEntityCondNot not = factory.NewEntityCondNot() ;
//      CBaseEntityCondition cond = cond.getSimilarCondition(factory, term) ;
//      not.SetCondition(cond);
//      return not ;
//  }
    /** Updates the condition. */
    public void UpdateCondition(CBaseEntityCondition condition, CBaseEntityCondition newCond)
    {
        if (cond == condition)
        {
            cond = newCond ;
        }
    }
    public boolean isBinaryCondition()
    {
        return cond.isBinaryCondition() ;
    }
    /**
     * @see semantic.expression.CBaseEntityCondition#GetConditionReference()
     */
    @Override
    public CDataEntity GetConditionReference()
    {
        return null;
    }
    /** Sets the conditon reference. */
    public void SetConditonReference(CDataEntity e)
    {
        ASSERT(null) ;
    }
}
