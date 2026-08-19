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
public class CEntityCondIsKindOf extends CUnitaryEntityCondition
{
    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 7;
    }

    /** Executes the get opposite condition operation. */
    public CBaseEntityCondition GetOppositeCondition()
    {
        CEntityCondIsKindOf opposite = new CEntityCondIsKindOf();
        opposite.isisAlphabetic = isisAlphabetic;
        opposite.isisLower = isisLower;
        opposite.isisNumeric = isisNumeric;
        opposite.isisUpper = isisUpper;
        opposite.isopposite = !isopposite;
        opposite.reference = reference;
        return opposite;
    }
    /** Sets the is numeric. */
    public void SetIsNumeric(CDataEntity data)
    {
        SetConditonReference(data) ;
        isisNumeric = true ;
        isisAlphabetic = false ;
        isisLower = false ;
        isisUpper = false ;
    }
    /** Sets the is alphabetic. */
    public void SetIsAlphabetic(CDataEntity data)
    {
        SetConditonReference(data) ;
        isisNumeric = false ;
        isisAlphabetic = true ;
        isisLower = false ;
        isisUpper = false ;
    }
    /** Sets the is lower. */
    public void SetIsLower(CDataEntity data)
    {
        SetConditonReference(data) ;
        isisNumeric = false ;
        isisAlphabetic = false ;
        isisLower = true ;
        isisUpper = false ;
    }
    /** Sets the is upper. */
    public void SetIsUpper(CDataEntity data)
    {
        SetConditonReference(data) ;
        isisNumeric = false ;
        isisAlphabetic = false ;
        isisLower = false ;
        isisUpper = true ;
    }

    protected boolean isisNumeric = false ;
    protected boolean isisLower = false ;
    protected boolean isisUpper = false ;
    protected boolean isisAlphabetic = false ;
    protected boolean isopposite = false ;
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return reference.ignore();
    }
    /** Executes the get special condition replacing operation. */
    public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
    {
        return null;
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (reference == field)
        {
            field.UnRegisterVarTesting(this) ;
            var.RegisterVarTesting(this) ;
            reference = var ;
            return true ;
        }
        return false ;
    }
    /** Sets the opposite. */
    public void setOpposite()
    {
        isopposite = !isopposite;
    }
    public boolean isNumeric()
    {
        return isisNumeric;
    }
    public boolean isAlphabetic()
    {
        return isisAlphabetic;
    }
    public boolean isLower()
    {
        return isisLower;
    }
    public boolean isUpper()
    {
        return isisUpper;
    }
    public boolean isOpposite()
    {
        return isopposite;
    }
    public boolean isBinaryCondition()
    {
        return false;
    }
}
