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
public class CEntitySetCursor extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntitySetCursor(int line, CObjectCatalog cat, CDataEntity field)
    {
        super(line, cat);
        refField = field ;
    }

    /*
     * Pure read-only getters consumed by the recursive ST4 assembly contract
     * (semantic.forms.CEntitySetCursor -> recursiveSetCursorEntity), preserved
     * from the retired direct backend generate.java.forms.CJavaSetCursor whose
     * DoExport selected the emitted runtime call from exactly these three slots:
     * a set reference value selects moveCursor(<value>, <field>), the remove
     * flag selects removeCursor(<field>), otherwise setCursor(<field>) is
     * emitted. No output protocol lives here; the getters only expose the
     * already-resolved semantic state and the assembler renders the child data
     * references recursively.
     */
    public CDataEntity getField()
    {
        return refField ;
    }
    public CDataEntity getReferenceValue()
    {
        return referenceValue ;
    }
    public boolean isMoveCursor()
    {
        return referenceValue != null ;
    }
    public boolean isRemoveCursor()
    {
        return isremoveCursor ;
    }

    protected CDataEntity refField = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        refField = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return refField == null || (referenceValue!=null && referenceValue.ignore());
    }
    /** Executes the ignore variable operation. */
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
    /** Executes the replace variable operation. */
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
    public void removeCursor()
    {
        isremoveCursor = true ;
    }
    protected boolean isremoveCursor = false ;
    /**
     * @param term
     */
    public void SetReference(CDataEntity term)
    {
        referenceValue = term ;
    }
    protected CDataEntity referenceValue = null ;

}
