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

import java.util.Calendar;

import jlib.misc.CurrentDateInfo;
import jlib.misc.NumberParser;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: OpenCalendarRange.java,v 1.2 2006/05/08 10:38:06 cvsadmin Exp $
 */
public class OpenCalendarRange
{
	OpenCalendarRange()
	{
		nHour = new int[2];
		nMinute = new int[2];
		nSecond = new int[2];
	}
	
	void set(CalendarOpenState state, String csMin, String csMax)
	{
		openState = state;
		fill(0, csMin);
		fill(1, csMax);
	}
	
	void setBegin(CalendarInstant i)
	{
		openState = i.openState;
		nHour[0] = i.nHour;
		nMinute[0] = i.nMinute;
		nSecond[0] = i.nSecond;
	}

	void setEnd(CalendarInstant i)
	{
		nHour[1] = i.nHour;
		nMinute[1] = i.nMinute;
		nSecond[1] = i.nSecond;
	}
	
	private void fill(int nMinMax, String csTime)
	{
		int n = csTime.indexOf(":");
		String cs = csTime.substring(0, n);
		nHour[nMinMax] = NumberParser.getAsInt(cs);
		
		csTime = csTime.substring(n+1);
		n = csTime.indexOf(":");
		cs = csTime.substring(0, n);
		nMinute[nMinMax] = NumberParser.getAsInt(cs);
		
		csTime = csTime.substring(n+1);
		nSecond[nMinMax] = NumberParser.getAsInt(csTime);		
	}
	
	void setCloseAllDay()
	{
		openState = CalendarOpenState.AppClosed; 
		nHour[0] = 0;
		nMinute[0] = 0;
		nSecond[0] = 0;
		nHour[1] = 24;
		nMinute[1] = 0;
		nSecond[1] = 0;
	}
	
	boolean isSameType(OpenCalendarRange r)
	{
		if(openState == r.openState)
			return true;
		return false;
	}
	
	CalendarInstant getInstant(int n)
	{
		CalendarInstant i = new CalendarInstant();
		i.nHour = nHour[n];
		i.nMinute = nMinute[n];
		i.nSecond = nSecond[n];
		i.openState = openState;
		return i;	
	}

	boolean concernDate(CurrentDateInfo currentDate)
	{
		int nHourVal = currentDate.getHour();
		int nMinuteVal = currentDate.getMinute();
		int nSecondVal = currentDate.getSecond();
		if(nHourVal > nHour[0] || (nHourVal == nHour[0] && nMinuteVal > nMinute[0]) || (nHourVal == nHour[0] && nMinuteVal == nMinute[0] && nSecondVal >= nSecond[0]))
		{
			if(nHourVal < nHour[1] || (nHourVal == nHour[1] && nMinuteVal < nMinute[1]) || (nHourVal == nHour[1] && nMinuteVal == nMinute[1] && nSecondVal < nSecond[1]))
				return true;
		}
		return false;
	}
	
	CalendarOpenState getOpenState()
	{
		return openState;
	}
	
	String getAsString()
	{
		String cs = nHour[0]+":"+nMinute[0]+":"+nSecond[0] + " -> " + nHour[1]+":"+nMinute[1]+":"+nSecond[1];
		return cs;
	}
	
	void setCalendarAtEnd(Calendar cal)
	{
		cal.set(Calendar.HOUR, nHour[1]);
		cal.set(Calendar.MINUTE, nMinute[1]);
		cal.set(Calendar.SECOND, nSecond[1]);
	}
	
	private CalendarOpenState openState = CalendarOpenState.Unknown;
	int nHour[] = null;
	int nMinute[] = null;
	int nSecond[] = null;
}
