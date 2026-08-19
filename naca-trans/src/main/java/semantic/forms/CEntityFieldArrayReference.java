/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.CEntityArrayReference;
import semantic.CSubStringAttributReference;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityFieldArrayReference extends CEntityArrayReference
{
    /**
     * @param l
     * @param cat
     */
    public CEntityFieldArrayReference(int l, CObjectCatalog cat)
    {
        super(l, cat);
    }
    /*
     * Semantic predicate preserved from the retired direct backend
     * (generate.java.forms.CJavaFieldArrayReference): a pure read-only getter
     * consumed by the BMS traversal and the recursive ST4 assembly contract; no
     * output protocol lives here. The reference read renders through the
     * semantic.forms.CEntityFieldArrayReference -> arrayReferenceEntity binding
     * ("<field reference>.getAt(<indexes>)"), exactly the legacy ExportReference
     * shape (the same frozen template the COBOL CEntityArrayReference uses). The
     * retired backend reported FIELD here whereas the CEntityArrayReference base
     * reports VAR, so the override stays on the pure entity to preserve behavior.
     */
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.FIELD ;
    }
    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CBaseEntityCondition.EConditionType type,
        CBaseEntityFactory factory)
    {
        CBaseEntityCondition eCond = reference.GetSpecialCondition(getLine(), value, type, factory);
        if (eCond == null)
        {
            return null ;
        }
        else
        {
            CDataEntity eData = eCond.GetConditionReference() ;
            CEntityFieldArrayReference eArray = factory.NewEntityFieldArrayReference(getLine()) ;
            eArray.arrIndexes = arrIndexes ;
            eArray.reference = eData ;
            eArray.RegisterVarTesting(eCond) ;
            eCond.SetConditonReference(eArray);
            return eCond;
        }
    }
    /** Executes the get sub string reference operation. */
    public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory)
    {
        CSubStringAttributReference ref = factory.NewEntitySubString(getLine()) ;
        ref.SetReference(this, start, length) ;
        return ref ;
    }
}
