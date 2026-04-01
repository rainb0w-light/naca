/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.misc;

import java.util.Calendar;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: CurrentDateInfo.java,v 1.4 2006/07/20 07:53:53 u930di Exp $
 */
public class CurrentDateInfo
{
	public CurrentDateInfo()
	{
		setNow();
	}
	
	public long setNow()
	{
		calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}
	
	public void setYear(int nYear, int nMonth, int nDay, int nHourOfDay, int nMinute, int nSecond)
	{
		calendar.set(nYear, nMonth, nDay, nHourOfDay, nMinute, nSecond);
	}

	public void setDateDDDotMMDotYYYY(String csDD_MM_YYYY)
	{
		int nDay = NumberParser.getAsInt(csDD_MM_YYYY.substring(0, 2));
		int nMonth = NumberParser.getAsInt(csDD_MM_YYYY.substring(3, 5));
		nMonth--;	// Month is 0 based (January = 0)
		int nYear = NumberParser.getAsInt(csDD_MM_YYYY.substring(6, 10));
		calendar.set(nYear, nMonth, nDay, 0, 0, 0);
	}
		
	public void setHourHHDotMMDotSS(String csHH_MM_SS)
	{
		int nHour = NumberParser.getAsInt(csHH_MM_SS.substring(0, 2));
		int nMinute = NumberParser.getAsInt(csHH_MM_SS.substring(3, 5));
		int nSecond = NumberParser.getAsInt(csHH_MM_SS.substring(6, 8));
		calendar.set(1970, 0, 1, nHour, nMinute, nSecond);
	}	
	
	public long getTimeInMillis()
	{
		return calendar.getTimeInMillis();
	}
	
	public long getTimeOffsetFromNow_ms()
	{
		long l1 = getTimeInMillis();
		long l2 = Calendar.getInstance().getTimeInMillis();
		return l2 - l1;
	}
	
	public long getTimeOffset_ms(CurrentDateInfo date2)
	{
		long l1 = getTimeInMillis();
		long l2 = date2.getTimeInMillis();
		if(l2 > l1)
			return l2 - l1;
		return l1 - l2;
	}
	
	public Integer getDateAsIntegerYYYYMMDD()
	{
		int n = (calendar.get(Calendar.YEAR) * 10000) + ((calendar.get(Calendar.MONTH)+1) *100) + calendar.get(Calendar.DAY_OF_MONTH);
		Integer date = Integer.valueOf(n);
		return date;
	}
	
	public String getDisplayableDateTime()
	{
		String cs = "" + StringUtil.FormatWithFill2LeftZero(getDay()) + "/" + StringUtil.FormatWithFill2LeftZero(getMonthBase1()) + "/" + StringUtil.FormatWithFill4LeftZero(getYear()) + "  " + StringUtil.FormatWithFill2LeftZero(getHour()) + ":" + StringUtil.FormatWithFill2LeftZero(getMinute()) + ":" + StringUtil.FormatWithFill2LeftZero(getSecond());
		return cs;
	}
	
	public int getYear()
	{
		return calendar.get(Calendar.YEAR);
	}
	
	public int getMonth()
	{
		return calendar.get(Calendar.MONTH);
	}
	
	public int getMonthBase1()
	{
		return calendar.get(Calendar.MONTH)+1;
	}
	
	public int getDay()
	{
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public int getDayOfWeek()
	{
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	public int getHour()
	{
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinute()
	{
		return calendar.get(Calendar.MINUTE);
	}
	
	public int getSecond()
	{
		return calendar.get(Calendar.SECOND);
	}
	
	public String toString()
	{
		return calendar.toString();
	}
	
	private Calendar calendar = null;
}
