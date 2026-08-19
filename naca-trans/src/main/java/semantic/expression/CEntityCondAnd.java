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
public class CEntityCondAnd extends CBaseEntityCondition
{
    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 1;
    }

    /** Executes the get opposite condition operation. */
    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityCondOr eOr = new CEntityCondOr();
        eOr.SetCondition(op1.GetOppositeCondition(), op2.GetOppositeCondition());
        return eOr;
    }

    /** Sets the condition. */
    public void SetCondition(CBaseEntityCondition op1, CBaseEntityCondition op2)    {
        this.op1 = op1 ;
        op1.SetParent(this);
        this.op2 = op2 ;
        op2.SetParent(this) ;
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
        if (child == null) {
            return false;
        }
        int parent = GetPriorityLevel();
        int childPriority = child.GetPriorityLevel();
        return (parent == 2 && childPriority == 1)
            || (parent == 1 && childPriority == 2)
            || parent > childPriority;
    }
    protected CBaseEntityCondition op1 = null ;
    protected CBaseEntityCondition op2 = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        op1.Clear() ;
        op2.Clear() ;
        op1 =null ;
        op2 = null ;
    }
    protected int GetLevelPriority()
    {
        return  1;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return op1.ignore() && op2.ignore() ;
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        boolean b1 = op1.ReplaceVariable(field, var) ;
        boolean b2 = op2.ReplaceVariable(field, var) ;
        return b1 || b2 ;
    }
    /** Executes the get special condition replacing operation. */
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        return null;
    }
    /** Updates the condition. */
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
    /** Sets the conditon reference. */
    public void SetConditonReference(CDataEntity e)
    {
        ASSERT(null) ;
    }

}
