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
    /** Sets the condition. */
    public void SetCondition(CEntityNamedCondition cond)
    {
        reference = cond ;
    }
    protected boolean isopposite = false ;

    public boolean isOpposite()
    {
        return isopposite;
    }

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return reference.ignore();
    }
    public boolean isBinaryCondition()
    {
        return false;
    }

    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 7;
    }

    /** Executes the get opposite condition operation. */
    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityIsNamedCondition opposite = new CEntityIsNamedCondition();
        opposite.isopposite = !isopposite;
        opposite.reference = reference;
        return opposite;
    }

    /** Executes the get special condition replacing operation. */
    public CBaseEntityCondition GetSpecialConditionReplacing(
        String value,
        CBaseEntityFactory factory,
        CDataEntity replacement)
    {
        return null;
    }

}
