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

import jlib.misc.SortableItem;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: CalendarInstant.java,v 1.1 2006/05/05 12:15:54 cvsadmin Exp $
 */
public class CalendarInstant extends SortableItem
{
	CalendarOpenState openState = CalendarOpenState.Unknown;
	int nHour = 0;
	int nMinute = 0;
	int nSecond = 0;
	
	public int compare(SortableItem item)
	{
		CalendarInstant i = (CalendarInstant)item;
		if(nHour == i.nHour && nMinute == i.nMinute && nSecond == i.nSecond)
			return 0;
		else if(nHour < i.nHour || (nHour == i.nHour && nMinute < i.nMinute) || (nHour == i.nHour && nMinute == i.nMinute && nSecond == i.nSecond))
			return -1;
		return 1;
	}
}
