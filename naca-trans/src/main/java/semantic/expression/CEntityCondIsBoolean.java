/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;

/** Provides centity cond is boolean behavior. */
public class CEntityCondIsBoolean extends CUnitaryEntityCondition
{
    protected boolean isisTrue = false ;

    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 7;
    }

    @Override
    public CBaseEntityCondition GetOppositeCondition()
    {
        // Target-neutral semantic rebuild: the opposite of "reference is true" is
        // "reference is not true" (and conversely) — copy the reference, flip the
        // flag, with no output protocol involved; the recursive ST4 assembler
        // renders both forms through the recursiveCondIsBooleanEntity binding.
        CEntityCondIsBoolean cond = new CEntityCondIsBoolean() ;
        cond.reference = reference ;
        cond.isisTrue = !isisTrue;
        return cond;
    }

    public boolean isTrue()
    {
        return isisTrue;
    }

    /**
     * ST4 property form of {@link #isTrue()}.  The semantic model exposes the
     * predicate as a boolean value; the template should only read that value,
     * never invoke semantic or rendering code.
     */
    public boolean isTrueValue()
    {
        return isTrue();
    }

    /** Sets the is true. */
    public void setIsTrue(CDataEntity e)
    {
        reference = e ;
        isisTrue = true ;
    }

    /** Sets the is false. */
    public void setIsFalse(CDataEntity e)
    {
        reference = e ;
        isisTrue = false ;
    }


    @Override
    public boolean isBinaryCondition()
    {
        return false;
    }

    @Override
    public CBaseEntityCondition GetSpecialConditionReplacing(String val,
                    CBaseEntityFactory fact, CDataEntity replace)
    {
        return null;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return reference.ignore() ;
    }

}
