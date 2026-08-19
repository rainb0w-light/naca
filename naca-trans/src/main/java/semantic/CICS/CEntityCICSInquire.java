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
public class CEntityCICSInquire extends CBaseActionEntity
{
    /**
     * @param line
     * @param cat
     */
    public CEntityCICSInquire(int line, CObjectCatalog cat)
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

    public CDataEntity program = null ;
    public CDataEntity transaction = null ;
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }

    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        program = null ;
        transaction = null ;
    }

    // ==================== ST4 Template Accessors ====================

    public CDataEntity getProgram()
    {
        return program;
    }

    public CDataEntity getTransaction()
    {
        return transaction;
    }

}
