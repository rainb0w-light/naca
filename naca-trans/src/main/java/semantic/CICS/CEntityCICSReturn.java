/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.CICS;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author U930CV
 *
 */
public class CEntityCICSReturn extends CBaseActionEntity
{
    /**
     * @param line
     * @param cat
     */
    public CEntityCICSReturn(int line, CObjectCatalog cat)
    {
        super(line, cat);
        // The catalog notification is a production-only side effect; the ST4 render
        // tests instantiate this entity directly with a null catalog (like the READ
        // exemplar), so guard it instead of dereferencing unconditionally.
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }
    /** Sets the trans id. */
    public void SetTransID(CDataEntity tid, CDataEntity comma, CDataEntity comlen, boolean bChecked)
    {
        transID = tid;
        commArea = comma ;
        commLenght = comlen ;
        ischecked = bChecked ;
    }

    protected boolean ischecked = false ;
    protected CDataEntity transID = null ;
    protected CDataEntity commArea = null ;
    protected CDataEntity commLenght = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        transID = null ;
        commArea = null ;
        commLenght = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (transID == field)
        {
            transID = var ;
            field.UnRegisterReadingAction(this) ;
            var.RegisterReadingAction(this) ;
            return true ;
        }
        else if (commArea == field)
        {
            commArea = var ;
            field.UnRegisterReadingAction(this) ;
            var.RegisterReadingAction(this) ;
            return true ;
        }
        return false ;
    }
    /** Returns whether s explicit get out. */
    public boolean hasExplicitGetOut()
    {
        return true ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveCICSReturnEntity). They expose the already-resolved semantic
    // sub-entities; rendering is done by the template, never here.

    public CDataEntity getTransID()
    {
        return transID;
    }

    public CDataEntity getCommArea()
    {
        return commArea;
    }

    public CDataEntity getCommLength()
    {
        return commLenght;
    }

    public boolean isChecked()
    {
        return ischecked;
    }

    public String getTransIDConstantValue()
    {
        return transID == null ? null : transID.GetConstantValue();
    }
}
