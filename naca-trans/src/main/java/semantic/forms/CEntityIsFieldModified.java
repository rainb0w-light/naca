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
import semantic.expression.CEntityCondNot;
import semantic.expression.CUnitaryEntityCondition;

/**
 * @author U930CV
 *
 */
public class CEntityIsFieldModified extends CUnitaryEntityCondition
{
    /** Sets the is modified. */
    public void SetIsModified(CDataEntity eData)
    {
        reference = eData ;
    }

    /*
     * Pure read-only getters consumed by the recursive ST4 assembly contract
     * (semantic.forms.CEntityIsFieldModified -> recursiveIsFieldModifiedEntity),
     * preserved from the retired direct backend
     * generate.java.forms.CJavaIsFieldModified whose Export emitted exactly
     * isFieldModified(<reference>) — the protected OnlineProgram.isFieldModified(Edit)
     * condition call (contract operation bms.field.modified). This class SHADOWS the
     * inherited CUnitaryEntityCondition.reference field (SetIsModified writes this
     * subclass slot), so the template reads getReference() — NOT the inherited
     * GetConditionReference(), which would return the unset superclass slot. No output
     * protocol lives here: the getter only exposes the already-resolved data reference
     * and the assembler renders it recursively. GetPriorityLevel/GetOppositeCondition
     * move up from the retired backend; the opposite is a pure
     * semantic.expression.CEntityCondNot (rendered !(isFieldModified(<reference>)) by
     * recursiveCondNotEntity) so the semantic tree carries no generate.* coupling.
     */
    public CDataEntity getReference()
    {
        return reference ;
    }

    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 7;
    }

    /** Executes the get opposite condition operation. */
    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityCondNot not = new CEntityCondNot();
        not.SetCondition(this);
        return not ;
    }

    protected CDataEntity reference = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        reference = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return reference.ignore();
    }
    /** Executes the get special condition replacing operation. */
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        return reference.GetSpecialCondition(getLine(), val, EConditionType.IS_EQUAL, fact);
    }
    public boolean isBinaryCondition()
    {
        return true;
    }
}
