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
 * @author sly
 *
 */
public class CEntityCICSGetMain extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntityCICSGetMain(int line, CObjectCatalog cat)
    {
        super(line, cat);
        // The catalog notification is a production-only side effect; the ST4 render
        // tests instantiate this entity directly with a null catalog (like the READ
        // and CICS exemplars above), so guard it instead of dereferencing
        // unconditionally.
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
}
