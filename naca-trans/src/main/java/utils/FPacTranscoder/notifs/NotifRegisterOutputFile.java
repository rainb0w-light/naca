/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils.FPacTranscoder.notifs;

import semantic.CEntityFileBuffer;
import jlib.engine.BaseNotification;

/** Provides notif register output file behavior. */
public class NotifRegisterOutputFile extends BaseNotification
{
    public String id;
    public CEntityFileBuffer fileBuffer;
    public Object alias = null ;

}
