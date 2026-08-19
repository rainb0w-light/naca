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
public class CEntityCondCompare extends CBinaryEntityCondition
{
    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 7;
    }

    /** Executes the get opposite condition operation. */
    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityCondCompare newCond = new CEntityCondCompare();
        newCond.isisGreater = !isisGreater;
        newCond.isisOrEquals = !isisOrEquals;
        newCond.op1 = op1;
        newCond.op2 = op2;
        return newCond;
    }


    /** Sets the less than. */
    public void SetLessThan(CBaseEntityExpression op1, CBaseEntityExpression op2)
    {
        this.op1 = op1 ;
        this.op2 = op2 ;
        isisOrEquals = false ;
        isisGreater = false ;
    }
    /** Sets the less or equal than. */
    public void SetLessOrEqualThan(CBaseEntityExpression op1, CBaseEntityExpression op2)
    {
        this.op1 = op1 ;
        this.op2 = op2 ;
        isisOrEquals = true ;
        isisGreater = false ;
    }
    /** Sets the greater than. */
    public void SetGreaterThan(CBaseEntityExpression op1, CBaseEntityExpression op2)
    {
        this.op1 = op1 ;
        this.op2 = op2 ;
        isisOrEquals = false ;
        isisGreater = true ;
    }
    /** Sets the greater or equals than. */
    public void SetGreaterOrEqualsThan(CBaseEntityExpression op1, CBaseEntityExpression op2)
    {
        this.op1 = op1 ;
        this.op2 = op2 ;
        isisOrEquals = true ;
        isisGreater = true ;
    }

    protected CBaseEntityExpression op1 ;
    protected CBaseEntityExpression op2 ;
    protected boolean isisGreater = false ; // true : >/>=, false : </<=
    protected boolean isisOrEquals = false ;// true : <=/>=, false : </>

    public CBaseEntityExpression getLeft()
    {
        return op1;
    }

    public CBaseEntityExpression getRight()
    {
        return op2;
    }

    public boolean isGreater()
    {
        return isisGreater;
    }

    public boolean isOrEqual()
    {
        return isisOrEquals;
    }

    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        op1.Clear() ;
        op1 = null ;
        op2.Clear() ;
        op2 = null ;
    }

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return (op1 != null && op1.ignore()) || (op2 != null && op2.ignore());
    }
    /** Executes the get special condition replacing operation. */
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        //CBaseEntityCondition cond = op1.GetSpecialCondition(val, type, fact) ;
        CDataEntity op = op1.GetSingleOperator() ;
        if (op != null)
        {
            EConditionType type = GetType() ;
            CBaseEntityCondition cond = op.GetSpecialCondition(getLine(), val, type, fact) ;
            if (cond != null)
            {
                return cond ;
            }
        }
        return null;
    }
    /** Executes the get type operation. */
    public CBaseEntityCondition.EConditionType GetType()
    {
        EConditionType type = null ;
        if (isisGreater && isisOrEquals)
        {
            type = EConditionType.IS_GREATER_THAN_OR_EQUAL ;
        }
        else if (isisGreater && !isisOrEquals)
        {
            type = EConditionType.IS_GREATER_THAN ;
        }
        else if (!isisGreater && isisOrEquals)
        {
            type = EConditionType.IS_LESS_THAN_OR_EQUAL ;
        }
        else if (!isisGreater && !isisOrEquals)
        {
            type = EConditionType.IS_LESS_THAN ;
        }
        return type ;
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        boolean b1 = op1.ReplaceVariable(field, var) ;
        boolean b2 = op2.ReplaceVariable(field, var) ;
        return b1 || b2 ;
    }

    /** Returns the operator. */
    public String getOperator()
    {
        if (isisGreater && isisOrEquals)
        {
            return ">=";
        }
        else if (isisGreater && !isisOrEquals)
        {
            return ">";
        }
        else if (!isisGreater && isisOrEquals)
        {
            return "<=";
        }
        else
        {
            return "<";
        }
    }

}
