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
public abstract class CBaseEntityCondition extends CBaseEntityCondExpr
{
//  public CBaseEntityCondition(int nLine)
//  {
//      super(nLine);
//  }

    public enum EConditionType
    {
        IS_DIFFERENT,
        IS_EQUAL,
        IS_GREATER_THAN,
        IS_GREATER_THAN_OR_EQUAL,
        IS_LESS_THAN,
        IS_LESS_THAN_OR_EQUAL,
        IS_FIELD_COLOR,
        IS_FIELD_ATTRIBUTE,
        IS_FIELD_HIGHLITING,
        IS_FIELD_MODIFIED,
        IS_FIELD_PROTECTED ;
    }
    /**
     * @param line
     * @param name
     * @param cat
     * @param out
     */

    public abstract int GetPriorityLevel() ;
    public abstract CBaseEntityCondition GetOppositeCondition() ;

    public void Replace(CBaseEntityCondition newCond)
    {
        parent.UpdateCondition(this, newCond) ;
    }

//  public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
//  {
//      return null;
//  }
    /**
     * @return
     */
    abstract public boolean isBinaryCondition() ;
    public CBaseEntityCondition getAsCondition()
    {
        return this;
    }

    public abstract CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace) ;
    public abstract CDataEntity GetConditionReference() ;
    public CDataEntity getConditionReference()
    {
        return GetConditionReference();
    }
    public abstract void SetConditonReference(CDataEntity e) ;

}
