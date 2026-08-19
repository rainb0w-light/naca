/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CDataEntity;

/**
 * @author sly
 *
 */
public abstract class CUnitaryEntityCondition extends CBaseEntityCondition
{
//  public CUnitaryEntityCondition(int nLine)
//  {
//      super(nLine);
//  }
    /* (non-Javadoc)
     * @see semantic.expression.CBaseEntityCondition#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
     */
    @Override
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (reference == field)
        {
            reference = var ;
            field.UnRegisterVarTesting(this) ;
            field.RegisterVarTesting(this) ;
            return true ;
        }
        return false ;
    }
    public void SetConditonReference(CDataEntity e)
    {
        ASSERT(e) ;
        reference = e ;
    }
    public CDataEntity GetConditionReference()
    {
        return reference ;
    }
    protected CDataEntity reference = null;
    public void Clear()
    {
        super.Clear() ;
        reference = null ;
    }
}
