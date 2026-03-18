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

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: CalendarCacheManager.java,v 1.1 2006/05/05 12:15:53 cvsadmin Exp $
 */
public class CalendarCacheManager
{
	CalendarCacheManager()
	{
		
	}
	
	void flush()
	{
		timeNextCheck_ms = 0;
	}
	
	boolean mustCheckServiceOpenState()
	{
		long lCurrentTime_ms = currentDate.setNow();
		if(lCurrentTime_ms >= timeNextCheck_ms)	// No need to check: not enough tine has elaped
			return true;
		return false;
	}
	
	void setNoDefinition()
	{
		currentState = CalendarOpenState.AppOpened;
		currentOpenCalendarRange = null;
	}
	
	CalendarOpenState getCurrentState()
	{
		return currentState;
	}
	
	void setCurrentOpenStateUnknown()
	{
		currentState = CalendarOpenState.Unknown;
		currentOpenCalendarRange = null;
	}
	
	void setCurrentOpenState(CalendarOpenState state, OpenCalendarRange range)
	{
		long lCurrentTime_ms = currentDate.getTimeInMillis();
		String cs0 = currentDate.toString();
		
		currentState = state;
		currentOpenCalendarRange = range;

		Calendar calEnd = Calendar.getInstance();
		calEnd.set(currentDate.getYear(), currentDate.getMonth(), currentDate.getDay(), range.nHour[1], range.nMinute[1], range.nSecond[1]); 

		timeNextCheck_ms = calEnd.getTimeInMillis();
		String cs = calEnd.toString();
		
		long l = (timeNextCheck_ms - lCurrentTime_ms);
		l /= 1000 ;
		int n = 0;		
	}
	
	Integer getCurrentDateAsIntegerYYYYMMDD()
	{
		return currentDate.getDateAsIntegerYYYYMMDD();
	}
	
	int getCurrentDayOfWeek()
	{
		return currentDate.getDayOfWeek();
	}
	
	CurrentDateInfo getCurrentDate()
	{
		return currentDate;
	}
	
	String getCurrentOpenCalendarRangeString()
	{
		if(currentOpenCalendarRange != null)
			return currentOpenCalendarRange.getAsString();
		return "";			
	}
	
	private CalendarOpenState currentState = null; 
	private long timeNextCheck_ms = 0;
	private CurrentDateInfo currentDate = new CurrentDateInfo();
	private OpenCalendarRange currentOpenCalendarRange = null;
}
