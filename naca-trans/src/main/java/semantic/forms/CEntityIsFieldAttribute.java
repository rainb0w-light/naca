/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CUnitaryEntityCondition;

/**
 * @author sly
 *
 */
public class CEntityIsFieldAttribute extends CUnitaryEntityCondition
{
    protected CDataEntity varValue = null ;
    public void Clear()
    {
        super.Clear();
        varValue = null ;
    }

    /*
     * Pure read-only getters consumed by the recursive ST4 assembly contract
     * (semantic.forms.CEntityIsFieldAttribute -> recursiveIsFieldAttributeEntity),
     * preserved from the retired direct backend
     * generate.java.forms.CJavaIsFieldAttribute. No output protocol lives here: the
     * getters only expose already-resolved state (the field reference, the optional
     * compared value, the attribute flags and the derived grouping/bracketing the
     * legacy Export computed) and the assembler renders the references recursively.
     *
     * The retired Export built either is[Not]FieldAttribute(<reference>, <varValue>)
     * when a compared value was present, or up to three mutually-exclusive attribute
     * terms — group1 autoSkip>protected>numeric>unprotected, group2 bright>dark,
     * group3 modified>unmodified>cleared — each emitted as is[Not]Field<Kind>(<reference>),
     * joined by " && " (or " || " when opposite) and wrapped in parentheses when two or
     * three groups are present. The template mirrors that exactly: isBracketed() is the
     * legacy isaddBracket, getJoinOperator() the legacy " && "/" || " joiner, and the
     * group*Present()/group*Join() getters drive the flat term/join layout.
     *
     * NOTE (pre-existing latent debt, NOT introduced by this slice): the legacy Export
     * also emitted isFieldProtected/isNotFieldProtected (group1 "protected"),
     * isNotFieldAttribute (opposite value test) and isNotFieldUnmodified (opposite
     * unmodified) — none of which has an implementation on idea.onlinePrgEnv.OnlineProgram.
     * This condition entity is unreachable dead code (no parser/factory path in either
     * the BMS or the FPac pipeline constructs it; the FPac factory throws), so those
     * branches never compiled in the legacy output either. They are reproduced here
     * byte-for-byte for parity but are deliberately NOT declared in the runtime contract
     * (RuntimeContractTest would reject a signature the runtime does not implement).
     */
    public CDataEntity getReference()
    {
        return reference ;
    }
    public CDataEntity getVarValue()
    {
        return varValue ;
    }
    public boolean isOpposite()
    {
        return isopposite ;
    }
    public boolean isAutoSkip()
    {
        return isisAutoSkip ;
    }
    public boolean isProtected()
    {
        return isisProtected ;
    }
    public boolean isNumeric()
    {
        return isisNumeric ;
    }
    public boolean isUnprotected()
    {
        return isisUnprotected ;
    }
    public boolean isBright()
    {
        return isisBright ;
    }
    public boolean isDark()
    {
        return isisDark ;
    }
    public boolean isModified()
    {
        return isisModified ;
    }
    public boolean isUnmodified()
    {
        return isisUnmodified ;
    }
    public boolean isCleared()
    {
        return isisCleared ;
    }
    public boolean isGroup1Present()
    {
        return isisAutoSkip || isisProtected || isisNumeric || isisUnprotected ;
    }
    public boolean isGroup2Present()
    {
        return isisBright || isisDark ;
    }
    public boolean isGroup3Present()
    {
        return isisModified || isisUnmodified || isisCleared ;
    }
    // Legacy isaddBracket: parentheses are added once a second (or third) group term is
    // appended, i.e. when two or more of the three groups are present.
    public boolean isBracketed()
    {
        int present = (isGroup1Present() ? 1 : 0)
            + (isGroup2Present() ? 1 : 0)
            + (isGroup3Present() ? 1 : 0) ;
        return present >= 2 ;
    }
    // Legacy BuildString joiner: " || " when opposite, otherwise " && ".
    public String getJoinOperator()
    {
        return isopposite ? "||" : "&&" ;
    }
    // group2 needs a leading join only when group1 is also present.
    public boolean isGroup2Join()
    {
        return isGroup1Present() && isGroup2Present() ;
    }
    // group3 needs a leading join only when group1 or group2 is also present.
    public boolean isGroup3Join()
    {
        return (isGroup1Present() || isGroup2Present()) && isGroup3Present() ;
    }

    /* (non-Javadoc)
     * @see semantic.expression.CBaseEntityCondition#GetPriorityLevel()
     */
    public int GetPriorityLevel()
    {
        if (nbConditions <= 1)
        {
            return 7;
        }
        else
        {
            return 1 ; // --> there are " || " in case of several conditions
        }
    }

    /* (non-Javadoc)
     * @see semantic.expression.CBaseEntityCondition#GetOppositeCondition()
     */
    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityIsFieldAttribute cond = new CEntityIsFieldAttribute() ;
        cond.isisAutoSkip = isisAutoSkip;
        cond.isisBright = isisBright;
        cond.isisCleared = isisCleared;
        cond.isisDark = isisDark;
        cond.isisModified = isisModified;
        cond.isisProtected = isisProtected;
        cond.isisNumeric = isisNumeric;
        cond.isisUnmodified = isisUnmodified;
        cond.isisUnprotected = isisUnprotected;
        cond.isopposite = !isopposite;
        cond.nbConditions = nbConditions ;
        cond.reference = reference ;
        cond.varValue = varValue ;
        reference.RegisterVarTesting(cond) ;
        return cond ;
    }

    public void IsAttribute(CDataEntity data, CDataEntity var)
    {
        varValue = data ;
        reference = var ;
    }
    public void IsAutoSkip()
    {
        isisAutoSkip = true ;
        nbConditions ++ ;
    }
    protected boolean isisAutoSkip = false ;

    public void IsBright()
    {
        isisBright = true ;
        nbConditions ++ ;
    }
    protected boolean isisBright = false ;

    public void IsNumeric()
    {
        isisNumeric = true ;
        nbConditions ++ ;
    }
    protected boolean isisNumeric = false ;

    public void IsProtected()
    {
        isisProtected = true;
        nbConditions ++ ;
    }
    protected boolean isisProtected = false ;

    public void IsUnprotected()
    {
        isisUnprotected = true ;
        nbConditions ++ ;
    }
    protected boolean isisUnprotected = false ;

    public void IsModified()
    {
        isisModified = true ;
        nbConditions ++ ;
    }
    public void IsUnmodified()
    {
        isisUnmodified = true ;
        nbConditions ++ ;
    }
    public void IsCleared()
    {
        isisCleared = true ;
        nbConditions ++ ;
    }
    protected boolean isisModified = false ;
    protected boolean isisUnmodified = false ;
    protected boolean isisCleared = false ;

    public void IsDark()
    {
        isisDark = true ;
        nbConditions ++ ;
    }
    protected boolean isisDark = false ;
    protected int nbConditions = 0;

    public void SetVariable(CDataEntity field)
    {
        reference = field ;
    }
    public boolean ignore()
    {
        return reference.ignore() ;
    }

    /* (non-Javadoc)
     * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String, semantic.CBaseEntityFactory)
     */
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        CBaseEntityCondition cond = reference.GetSpecialCondition(getLine(), val, EConditionType.IS_FIELD_ATTRIBUTE, fact);
        if (isopposite && cond!=null)
        {
            return cond.GetOppositeCondition() ;
        }
        return cond ;
    }
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (reference == field)
        {
            field.UnRegisterVarTesting(this) ;
            var.RegisterVarTesting(this) ;
            reference = var ;
            return true ;
        }
        if (varValue == field)
        {
            varValue = var ;
            field.UnRegisterValueAccess(this) ;
            var.RegisterValueAccess(this) ;
            return true ;
        }
        return false  ;
    }

    protected void SetOpposite()
    {
        isopposite = !isopposite;
    }
    protected boolean isopposite = false ;
    public boolean isBinaryCondition()
    {
        return true;
    }

}
