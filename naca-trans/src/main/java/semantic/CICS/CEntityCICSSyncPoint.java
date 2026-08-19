/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.CICS;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author U930CV
 *
 */
public class CEntityCICSSyncPoint extends CBaseActionEntity
{
    /**
     * @param line
     * @param cat
     */
    public CEntityCICSSyncPoint(int line, CObjectCatalog cat, boolean bRollback)
    {
        super(line, cat);
        isrollback = bRollback ;
        // The catalog notification is a production-only side effect; the ST4 render
        // tests instantiate this entity directly with a null catalog (like the READ
        // and CICS RETURN exemplars), so guard it instead of dereferencing unconditionally.
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }

    protected boolean isrollback = false ;
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getter for the recursive ST4 assembler (template
    // recursiveCICSSyncPointEntity). It exposes the already-resolved semantic
    // state; rendering is done by the template, never here.

    public boolean isRollback()
    {
        return isrollback;
    }
}
