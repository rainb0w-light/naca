/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils.CobolTranscoder;

import utils.CobolTranscoder.Notifs.*;
import jlib.engine.BaseNotificationHandler;

public class SpecialCobolActionNotifHandler extends BaseNotificationHandler
{
	private boolean bUseCICSPreprocessor = false ;
	
	
	public boolean OnUseCICSPreprocessor(NotifDeclareUseCICSPreprocessor notif)
	{
		bUseCICSPreprocessor = true ;
		return true ;
	}
	
	public boolean OnIsUsedCICSPRe(NotifIsUsedCICSPreprocessor notif)
	{
		notif.bUsed = bUseCICSPreprocessor ;
		return true ;
	}
}
