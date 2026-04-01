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
 * @version $Id: OpenCalendarManager.java,v 1.1 2006/05/05 12:15:53 cvsadmin Exp $
 */
public class OpenCalendarManager
{
	public final static int Standard = 0;
	public final static int Custom = 1;
	
	public OpenCalendarManager()
	{
		cacheManager = new CalendarCacheManager();
	}
	
	synchronized public void setReloadCalendarFiles()
	{
		cacheManager.flush();
		if(tCalendar == null)
			return ;
		if(tCalendar[Standard] != null)
			tCalendar[Standard].reloadDefinition();
		if(tCalendar[Custom] != null)
			tCalendar[Custom].reloadDefinition();
	}

	synchronized public void addCalendarDefinition(int nCalendardId, String csCalendarFilePath)
	{
		if(tCalendar == null)
			tCalendar = new OpenCalendar[2]; 

		OpenCalendar calendar = new OpenCalendar();
		calendar.loadDefinition(csCalendarFilePath);
		tCalendar[nCalendardId] = calendar;
	}
	
	public boolean isServiceOpen()
	{
		CalendarOpenState state = getServiceOpenState();
		return state.isOpen();
	}
	
	synchronized public CalendarOpenState getAppCustomOpenState()
	{
		if(tCalendar == null)	// No def: Always open
		{
			cacheManager.setNoDefinition();
			return CalendarOpenState.AppOpened;
		}
		
		// Check custom calendar
		OpenCalendar calendar = tCalendar[Custom];
		if(calendar != null)
		{
			CalendarOpenState openState = calendar.getOpenState(cacheManager, false);
			if(openState.isKnown())
				return openState;
		}
		return CalendarOpenState.Unknown;
	}
	
	synchronized public CalendarOpenState getAppStandardOpenState()
	{
		if(tCalendar == null)	// No def: Always open
		{
			cacheManager.setNoDefinition();
			return CalendarOpenState.AppOpened;
		}
		
		// No custom definition: See std def
		OpenCalendar calendar = tCalendar[Standard];
		if(calendar != null)
		{
			CalendarOpenState openState = calendar.getOpenState(cacheManager, false);
			if(openState.isKnown())
				return openState;
			return CalendarOpenState.AppClosed;	// Missign standard def are same as closed 
		}
		// No standard def: open
		return CalendarOpenState.AppOpened;
	}
	
	synchronized public CalendarOpenState getServiceOpenState()
	{
		if(tCalendar == null)	// No def: Always open
		{
			cacheManager.setNoDefinition();
			return CalendarOpenState.AppOpened;
		}
		
		if(!cacheManager.mustCheckServiceOpenState())
			return cacheManager.getCurrentState();
		
		// Check custom calendar
		OpenCalendar calendar = tCalendar[Custom];
		if(calendar != null)
		{
			CalendarOpenState openState = calendar.getOpenState(cacheManager, true);
			if(openState.isKnown())
			{
				return openState;
			}
		}
		
		// No custom definition: See std def
		calendar = tCalendar[Standard];
		if(calendar != null)
		{
			CalendarOpenState openState = calendar.getOpenState(cacheManager, true);
			if(openState.isKnown())
				return openState;
			return CalendarOpenState.AppClosed;	// Missign standard def are same as closed 
		}
		
		// No standard def: open
		return CalendarOpenState.AppOpened;
	}
	
	synchronized public String getCurrentOpenCalendarRangeString()
	{
		return cacheManager.getCurrentOpenCalendarRangeString();
	}
	
	public void flushCalendarCache()
	{
		cacheManager.flush();
	}

	private OpenCalendar tCalendar[] = null;
	private CalendarCacheManager cacheManager = null; 
}
