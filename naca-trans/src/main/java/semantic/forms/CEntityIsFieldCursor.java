/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 14 f�vr. 2005
 *
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
public class CEntityIsFieldCursor extends CUnitaryEntityCondition
{

    /* (non-Javadoc)
     * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String, semantic.CBaseEntityFactory)
     */
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        if (ishasCursor)
        {
            return reference.GetSpecialCondition(getLine(), val, EConditionType.IS_EQUAL, fact);
        }
        else
        {
            return reference.GetSpecialCondition(getLine(), val, EConditionType.IS_DIFFERENT, fact);
        }
    }

    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#ignore()
     */
    public boolean ignore()
    {
        return reference.ignore();
    }

    protected boolean ishasCursor = true ;
    /**
     * @param refField
     */
    public void SetHasCursor(CDataEntity refField)
    {
        reference = refField ;
        ishasCursor = true ;
    }

    /**
     * @param refField
     */
    public void SetHasNotCursor(CDataEntity refField)
    {
        reference = refField ;
        ishasCursor = false ;
    }

    /*
     * Pure read-only getters consumed by the recursive ST4 assembly contract
     * (semantic.forms.CEntityIsFieldCursor -> recursiveIsFieldCursorEntity), preserved
     * from the retired direct backend generate.java.forms.CJavaIsFieldCursor whose
     * Export emitted isFieldHasCursor(<reference>) when ishasCursor was set and
     * isNotFieldHasCursor(<reference>) otherwise — the two protected
     * OnlineProgram.isFieldHasCursor(Edit) / isNotFieldHasCursor(Edit) condition calls
     * (contract operations bms.field.cursor.has / bms.field.cursor.hasNot). Unlike the
     * field-modified condition, negation is carried by this entity's own ishasCursor
     * flag (SetHasCursor/SetHasNotCursor and GetOppositeCondition flip it) rather than a
     * wrapping CEntityCondNot, so isOpposite() exposes the flag and the template branches
     * on it to select the is/isNot runtime call, preserving the two distinct legacy
     * calls byte-for-byte. getReference() only exposes the already-resolved inherited
     * condition reference; the assembler renders it recursively. No output protocol lives
     * here. GetPriorityLevel/GetOppositeCondition move up from the retired backend; the
     * opposite is a flag-flipped CEntityIsFieldCursor (rendered isNotFieldHasCursor /
     * isFieldHasCursor through the same binding) with var testing re-registered, exactly
     * as the legacy backend did — no generate.* coupling in the semantic tree.
     */
    public CDataEntity getReference()
    {
        return reference ;
    }

    public boolean isOpposite()
    {
        return !ishasCursor ;
    }

    public int GetPriorityLevel()
    {
        return 7;
    }

    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityIsFieldCursor cur = new CEntityIsFieldCursor() ;
        cur.ishasCursor = !ishasCursor;
        cur.reference = reference ;
        reference.RegisterVarTesting(cur) ;
        return cur;
    }

    public boolean isBinaryCondition()
    {
        return true;
    }

}
