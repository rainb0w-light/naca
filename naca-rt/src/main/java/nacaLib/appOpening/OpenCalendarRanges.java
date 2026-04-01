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

import java.util.ArrayList;

import jlib.misc.CurrentDateInfo;
import jlib.misc.QuickSort;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: OpenCalendarRanges.java,v 1.1 2006/05/05 12:15:54 cvsadmin Exp $
 */
public class OpenCalendarRanges
{
	OpenCalendarRanges()
	{		
	}
	
	void addRange(CalendarOpenState state, String csMin, String csMax)
	{
		OpenCalendarRange range = new OpenCalendarRange();
		range.set(state, csMin, csMax);
		addRange(range);
	}
	
	void addRange(OpenCalendarRange range)
	{
		if(ranges == null)
			ranges = new ArrayList<OpenCalendarRange>();
		ranges.add(range);
	}
	
	void sortIntervals()
	{
		if(ranges == null)
		{
			setCloseAllDay();
		}
		
		ArrayList<CalendarInstant> instant = new ArrayList<CalendarInstant>();
		for(int n = 0; n< ranges.size(); n++)
		{
			OpenCalendarRange range = ranges.get(n);
			
			CalendarInstant begin = range.getInstant(0);
			instant.add(begin);
			
			CalendarInstant end = range.getInstant(1);
			instant.add(end);
		}
		ranges.clear();
		
		QuickSort<CalendarInstant> qs = new QuickSort<CalendarInstant>(instant);
		qs.sort();
		
		int n=0;
		while(n< instant.size())
		{
			OpenCalendarRange range = new OpenCalendarRange();
			CalendarInstant begin = instant.get(n);
			range.setBegin(begin);

			n++;
			CalendarInstant end = instant.get(n);
			range.setEnd(end);
			
			ranges.add(range);
			
			n++;
		}
	}
		
	void setCloseAllDay()
	{
		OpenCalendarRange rangeClosedAllDay = new OpenCalendarRange();
		rangeClosedAllDay.setCloseAllDay();

		addRange(rangeClosedAllDay);
	}

	CalendarOpenState getOpenState(CalendarCacheManager cacheManager, boolean bCacheState)
	{
		CurrentDateInfo currentDate = cacheManager.getCurrentDate();
		for(int n = 0; n< ranges.size(); n++)
		{
			OpenCalendarRange range = ranges.get(n);
			if(range.concernDate(currentDate))
			{
				CalendarOpenState state = range.getOpenState();
				if(bCacheState)
					cacheManager.setCurrentOpenState(state, range);
				return state;
			}			
		}
		if(bCacheState)
			cacheManager.setCurrentOpenStateUnknown();
		return CalendarOpenState.Unknown;
	}
	
	private ArrayList<OpenCalendarRange> ranges = null;
}
