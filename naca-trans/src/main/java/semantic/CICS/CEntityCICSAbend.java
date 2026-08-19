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
public class CEntityCICSAbend extends CBaseActionEntity
{
    /**
     * @param line
     * @param cat
     */
    public CEntityCICSAbend(int line, CObjectCatalog cat)
    {
        super(line, cat);
        // The catalog notification is a production-only side effect; the ST4 render
        // tests instantiate this entity directly with a null catalog (like the READ
        // and CICS XCTL/LINK exemplars), so guard it instead of dereferencing
        // unconditionally.
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }

    /** Sets the abcode. */
    public void SetABCode(CDataEntity ab)
    {
        aBCode = ab ;
    }

    protected CDataEntity aBCode = null ;
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        if (aBCode != null)
        {
            aBCode.Clear() ;
        }
        aBCode = null ;
    }
    /** Returns whether s explicit get out. */
    public boolean hasExplicitGetOut()
    {
        return true ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveCICSAbendEntity). They expose the already-resolved semantic
    // sub-entities; rendering is done by the template, never here.

    public CDataEntity getABCode()
    {
        return aBCode;
    }
}
