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
public class CEntityCondIsAll extends CBaseEntityCondition
{
    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 7;
    }

    /** Executes the get opposite condition operation. */
    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityCondIsAll opposite = new CEntityCondIsAll();
        opposite.SetCondition(exprData, exprToken);
        opposite.bIsOpposite = !bIsOpposite;
        return opposite;
    }

    /** Sets the condition. */
    public void SetCondition(CBaseEntityExpression data, CBaseEntityExpression tok)
    {
        exprData = data ;
        exprToken = tok ;
    }

    protected CBaseEntityExpression exprData = null ;
    protected CBaseEntityExpression exprToken = null ;
    protected boolean bIsOpposite = false ;
    /** Sets the opposite. */
    public void setOpposite()
    {
        bIsOpposite = ! bIsOpposite ;
    }
    public CBaseEntityExpression getData()
    {
        return exprData;
    }
    public CBaseEntityExpression getToken()
    {
        return exprToken;
    }
    public boolean isOpposite()
    {
        return bIsOpposite;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        exprData.Clear() ;
        exprToken.Clear() ;
        exprData = null ;
        exprToken = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return exprData.ignore() ;
    }
    /** Executes the get special condition replacing operation. */
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        return null;
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
