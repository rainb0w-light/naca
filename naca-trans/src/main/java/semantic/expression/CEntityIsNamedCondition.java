/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.CEntityNamedCondition;

/**
 * @author U930CV
 *
 */
public class CEntityIsNamedCondition extends CUnitaryEntityCondition
{
    public void SetCondition(CEntityNamedCondition cond)
    {
        reference = cond ;
    }
    protected boolean isopposite = false ;

    public boolean isOpposite()
    {
        return isopposite;
    }

    public boolean ignore()
    {
        return reference.ignore();
    }
    public boolean isBinaryCondition()
    {
        return false;
    }

    public int GetPriorityLevel()
    {
        return 7;
    }

    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityIsNamedCondition opposite = new CEntityIsNamedCondition();
        opposite.isopposite = !isopposite;
        opposite.reference = reference;
        return opposite;
    }

    public CBaseEntityCondition GetSpecialConditionReplacing(
        String value,
        CBaseEntityFactory factory,
        CDataEntity replacement)
    {
        return null;
    }

}
