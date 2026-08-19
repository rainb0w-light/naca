/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntitySetFlag extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntitySetFlag(int line, CObjectCatalog cat, CDataEntity field)
    {
        super(line, cat);
        refField = field ;
    }

    /*
     * Pure read-only getters consumed by the recursive ST4 assembly contract
     * (semantic.forms.CEntitySetFlag -> recursiveSetFlagEntity), preserved from
     * the retired direct backend generate.java.forms.CJavaSetFlag whose DoExport
     * selected the emitted runtime call from exactly these two slots: a non-null
     * flag value selects moveFlag("<value>", <field>) (the protected
     * OnlineProgram.moveFlag(String, Edit) call, value quoted verbatim), otherwise
     * resetFlag(<field>) is emitted (the protected OnlineProgram.resetFlag(Edit)
     * call). No output protocol lives here; the getters only expose the
     * already-resolved semantic state and the assembler renders the child field
     * data reference recursively.
     */
    public CDataEntity getField()
    {
        return refField ;
    }
    public String getFlagValue()
    {
        return flagValue ;
    }
    public boolean isMoveFlag()
    {
        return flagValue != null ;
    }

    public void SetFlag(String cs)
    {
        flagValue = cs ;
    }
    protected String flagValue = null ;
    protected CDataEntity refField = null ;
    public void Clear()
    {
        super.Clear();
        refField = null ;
    }
    public boolean ignore()
    {
        return refField == null ;
    }
    public boolean IgnoreVariable(CDataEntity data)
    {
        if (data == refField)
        {
            refField = null ;
            data.UnRegisterWritingAction(this) ;

            return true ;
        }
        return false ;
    }
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (refField == field)
        {
            refField = var ;
            field.UnRegisterWritingAction(this) ;
            var.RegisterWritingAction(this) ;
            return true ;
        }
        return false ;
    }

    /**
     *
     */
    public void ResetFlag()
    {
        flagValue = null ;
    }
}
