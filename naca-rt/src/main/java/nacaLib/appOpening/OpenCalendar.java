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

import jlib.xml.Tag;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: OpenCalendar.java,v 1.3 2006/05/23 11:34:25 u930cv Exp $
 */
public class OpenCalendar
{
	OpenCalendar()
	{
	}
		
	void loadDefinition(String csFile)
	{
		this.csFile = csFile;
		Tag tag = Tag.createFromFile(csFile);
		if(tag != null)
		{
			Tag tagDay = tag.getEnumChild("Day");
			while(tagDay != null)
			{
				String csDayValue = tagDay.getVal("Value");
				int nDayOfWeekId = getDayOfWeeekId(csDayValue);
				loadDayRange(nDayOfWeekId, tagDay);
				tagDay = tag.getEnumChild();
			}
			
			Tag tagDate = tag.getEnumChild("Date");
			while(tagDate != null)
			{
				String csDateValue = tagDate.getVal("Value");
				Integer date = getDate(csDateValue);
				loadDateRange(date, tagDate);
				tagDate = tag.getEnumChild();
			}
		}
		
		if(week != null)
			week.generateSortedIntervals();

		if(rangesForDates != null)
			rangesForDates.generateSortedIntervals();
	}
	
	void reloadDefinition()
	{
		week = null;
		rangesForDates = null;
		loadDefinition(csFile);
	}
	
	private void loadDayRange(int nDayOfWeeekId, Tag tagDay)
	{
		Tag tagOpenRange = tagDay.getEnumChild("Open");
		while(tagOpenRange != null)
		{
			String csMin = tagOpenRange.getVal("Min");
			String csMax = tagOpenRange.getVal("Max");
			addRange(nDayOfWeeekId, "Open", csMin, csMax);
			tagOpenRange = tagDay.getEnumChild();
		}
	}
		
	private void addRange(int nDayId, String csType, String csMin, String csMax)
	{
		if(week == null)
			week = new OpenCalendarWeek();
		week.addRange(nDayId, csType, csMin, csMax);
	}
	
	private int getDayOfWeeekId(String csDayValue)
	{
		csDayValue = csDayValue.toUpperCase();
		if(csDayValue.startsWith("MON"))
			return Calendar.MONDAY;
		else if(csDayValue.startsWith("TUE"))
			return Calendar.TUESDAY;
		else if(csDayValue.startsWith("WED"))
			return Calendar.WEDNESDAY;
		else if(csDayValue.startsWith("THU"))
			return Calendar.THURSDAY;
		else if(csDayValue.startsWith("FRI"))
			return Calendar.FRIDAY;
		else if(csDayValue.startsWith("SAT"))
			return Calendar.SATURDAY;
		return Calendar.SUNDAY;
	}
	
	private Integer getDate(String csDateValue)
	{
		int n = csDateValue.indexOf("/");
		String csDay = csDateValue.substring(0, n);
		
		csDateValue = csDateValue.substring(n+1);
		n = csDateValue.indexOf("/");
		String csMonth = csDateValue.substring(0, n);
		
		csDateValue = csDateValue.substring(n+1);
		
		Integer date = Integer.valueOf(csDateValue + csMonth + csDay);
		return date;
	}
	

	private void loadDateRange(Integer iDate, Tag tagDate)
	{
		Tag tagRange = tagDate.getEnumChild();
		while(tagRange != null)
		{
			String csName = tagRange.getName();
			String csMin = tagRange.getVal("Min");
			String csMax = tagRange.getVal("Max");
			addDateRange(iDate, csName, csMin, csMax);
			tagRange = tagDate.getEnumChild();
		}
	}
	
	private void addDateRange(Integer iDate, String csType, String csMin, String csMax)
	{
		if(csType.equalsIgnoreCase("Close") || csType.equalsIgnoreCase("Open"))
		{
			if(rangesForDates == null)
				rangesForDates = new OpenCalendarRangesForDates();
			
			CalendarOpenState state = CalendarOpenState.AppOpened; 
			if(csType.equalsIgnoreCase("Close"))
				state = CalendarOpenState.AppClosed;
			rangesForDates.addDateRange(iDate, state, csMin, csMax);
		}
	}

	CalendarOpenState getOpenState(CalendarCacheManager cacheManager, boolean bCacheState)
	{
		if(rangesForDates != null)	// Check individual dates
		{
			CalendarOpenState state = rangesForDates.getOpenState(cacheManager, bCacheState);
			if(state.isKnown())
				return state;
		}
		
		if(week != null)
			return week.getOpenState(cacheManager, bCacheState);
		return CalendarOpenState.Unknown;
	}
	
	private String csFile = null;
	private OpenCalendarWeek week = null;
	private OpenCalendarRangesForDates rangesForDates = null;
}
