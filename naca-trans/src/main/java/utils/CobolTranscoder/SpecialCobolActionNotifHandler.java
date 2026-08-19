/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils.CobolTranscoder;

import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;
import utils.CobolTranscoder.Notifs.NotifIsUsedCICSPreprocessor;
import jlib.engine.BaseNotificationHandler;

/** Provides special cobol action notif handler behavior. */
public class SpecialCobolActionNotifHandler extends BaseNotificationHandler
{
    private boolean isuseCICSPreprocessor = false ;


    /** Executes the on use cicspreprocessor operation. */
    public boolean OnUseCICSPreprocessor(NotifDeclareUseCICSPreprocessor notif)
    {
        isuseCICSPreprocessor = true ;
        return true ;
    }

    /** Executes the on is used cicspre operation. */
    public boolean OnIsUsedCICSPRe(NotifIsUsedCICSPreprocessor notif)
    {
        notif.isused = isuseCICSPreprocessor;
        return true ;
    }
}
