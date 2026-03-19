/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.appOpening;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: CalendarOpenState.java,v 1.2 2006/05/08 10:38:06 cvsadmin Exp $
 */
public class CalendarOpenState
{
	public static final CalendarOpenState Unknown = new CalendarOpenState(0, false, false, false);
	public static final CalendarOpenState AppClosed = new CalendarOpenState(1, true, false, false);
	public static final CalendarOpenState AppManuallyClosed = new CalendarOpenState(2, true, false, true);
	public static final CalendarOpenState AppOpened = new CalendarOpenState(3, true, true, false);
	
	private CalendarOpenState(int nId, boolean bKnown, boolean bOpen, boolean bManual)
	{
		this.nId = nId;
		this.bKnown = bKnown;
		this.bOpen = bOpen;
		this.bManual = bManual;
	}
	
	public boolean isKnown()
	{
		return bKnown;
	}
	
	public boolean isOpen()
	{
		return bOpen;
	}
	
	public String getString()
	{
		if(bKnown)
		{
			if(bOpen)
				return "Ouvert / ge�ffnet / aperto";
			else
			{
				if(bManual)
					return "Ferm� manuellement / Manuell geschlossen / Chiuso manualmente ";
				return "Ferm� / geschlossen / chiuso";
			}
		}
		return "Unknown";
	}
	
	public int getId()
	{
		return nId;
	}

	private int nId = 0;
	private boolean bKnown = false;
	private boolean bOpen = false;
	private boolean bManual = false;
}
