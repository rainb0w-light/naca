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
public class CEntityIsFieldColor extends CUnitaryEntityCondition
{

    public void IsColor(CEntityFieldColor.CFieldColor col, CDataEntity data)
    {
        isColor = col ;
        reference = data ;
    }
    protected CEntityFieldColor.CFieldColor isColor ;
    public void Clear()
    {
        super.Clear();
        reference = null ;
    }
    public boolean ignore()
    {
        return reference.ignore();
    }
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        return reference.GetSpecialCondition(getLine(), val, EConditionType.IS_EQUAL, fact);
    }
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (reference == field)
        {
            reference = var ;
            field.UnRegisterVarTesting(this) ;
            var.RegisterVarTesting(this) ;
            return true ;
        }
        return false ;
    }
    /**
     *
     */
    public void SetOpposite()
    {
        isopposite = !isopposite;
    }
    protected boolean isopposite = false ;
    public boolean isBinaryCondition()
    {
        return true;
    }

    /*
     * Pure read-only getters consumed by the recursive ST4 assembly contract
     * (semantic.forms.CEntityIsFieldColor -> recursiveIsFieldColorEntity), preserved
     * from the retired direct backend generate.java.forms.CJavaIsFieldColor whose
     * Export built "is" + ("Not" when isopposite) +
     * "FieldColored(<reference>, MapFieldAttrColor.<color>)" — the protected
     * OnlineProgram.isFieldColored(Edit, MapFieldAttrColor) condition call
     * (contract operation bms.field.color.is), negated to isNotFieldColored(Edit,
     * MapFieldAttrColor) (bms.field.color.isNot) when the isopposite flag is set
     * (IF <FIELD>C <> <color-code> lowers through SetOpposite). Negation is carried by
     * this entity's own isopposite flag (SetOpposite and GetOppositeCondition flip it)
     * rather than a wrapping CEntityCondNot, so isOpposite() exposes the flag and the
     * template branches on it to select the is/isNot runtime call, preserving the two
     * distinct legacy calls byte-for-byte. getColorConstant() folds the compared color
     * slot into the static nacaLib.mapSupport.MapFieldAttrColor field reference
     * (MapFieldAttrColor.<color>) — a pure getter over already-resolved state, never
     * lowering; getReference() only exposes the already-resolved inherited condition
     * reference and the assembler renders it recursively. No output protocol lives here.
     * GetPriorityLevel/GetOppositeCondition move up from the retired backend; the
     * opposite is a flag-flipped CEntityIsFieldColor (rendered isNotFieldColored /
     * isFieldColored through the same binding) with var testing re-registered, exactly
     * as the legacy backend did — no generate.* coupling in the semantic tree.
     */
    public CDataEntity getReference()
    {
        return reference ;
    }

    public boolean isOpposite()
    {
        return isopposite ;
    }

    public String getColorConstant()
    {
        return "MapFieldAttrColor." + isColor.text ;
    }

    public int GetPriorityLevel()
    {
        return 7;
    }

    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityIsFieldColor cond = new CEntityIsFieldColor() ;
        cond.isopposite = !isopposite;
        cond.isColor = this.isColor ;
        cond.reference = this.reference ;
        reference.RegisterVarTesting(cond) ;
        return cond ;
    }
}
