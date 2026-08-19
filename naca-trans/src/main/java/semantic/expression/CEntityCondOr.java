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
 */
public class CEntityCondOr extends CBaseEntityCondition
{
    public int GetPriorityLevel()
    {
        return 2;
    }

    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityCondAnd eAnd = new CEntityCondAnd();
        eAnd.SetCondition(op1.GetOppositeCondition(), op2.GetOppositeCondition());
        return eAnd;
    }

    public void SetCondition(CBaseEntityCondition op1, CBaseEntityCondition op2)    {
        this.op1 = op1 ;
        op1.SetParent(this) ;
        this.op2 = op2 ;
        op2.SetParent(this);
    }
    public CBaseEntityCondition getLeft()
    {
        return op1;
    }
    public CBaseEntityCondition getRight()
    {
        return op2;
    }
    public CBaseEntityCondition getEffectiveLeft()
    {
        return op1 != null && op1.ignore() ? op2 : op1;
    }
    public CBaseEntityCondition getEffectiveRight()
    {
        return op2 != null && op2.ignore() ? op1 : op2;
    }
    public boolean isLeftIgnored()
    {
        return op1 != null && op1.ignore();
    }
    public boolean isRightIgnored()
    {
        return op2 != null && op2.ignore();
    }
    public boolean isLeftGrouped()
    {
        return needsGrouping(op1);
    }
    public boolean isRightGrouped()
    {
        return needsGrouping(op2);
    }
    private boolean needsGrouping(CBaseEntityCondition child)
    {
        if (child == null) return false;
        int parent = GetPriorityLevel();
        int childPriority = child.GetPriorityLevel();
        return (parent == 2 && childPriority == 1)
            || (parent == 1 && childPriority == 2)
            || parent > childPriority;
    }
    protected CBaseEntityCondition op1 = null ;
    protected CBaseEntityCondition op2 = null ;
    public void Clear()
    {
        super.Clear() ;
        op1.Clear() ;
        op1 = null ;
        op2.Clear() ;
        op2 = null ;
    }
    public boolean ignore()
    {
        return op1.ignore() && op2.ignore() ;
    }
//  public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
//  {
//      CBaseEntityCondition eCond = op1.getSimilarCondition(factory, term);
//      if (eCond == null)
//      {
//          eCond = op2.getSimilarCondition(factory, term);
//      }
//      return eCond ;
//  }
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        return null;
    }
    public void UpdateCondition(CBaseEntityCondition condition, CBaseEntityCondition newCond)
    {
        if (op1 == condition)
        {
            op1 = newCond ;
        }
        if (op2 == condition)
        {
            op2 = newCond ;
        }
    }
    public boolean isBinaryCondition()
    {
        return false;
    }
    /**
     * @see semantic.expression.CBaseEntityCondition#GetConditionReference()
     */
    @Override
    public CDataEntity GetConditionReference()
    {
        return null;
    }
    public void SetConditonReference(CDataEntity e)
    {
        ASSERT(null) ;
    }

}
