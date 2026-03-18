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
		cal = Calendar.getInstance();
		return cal.getTimeInMillis();
	}
	
	public void setYear(int nYear, int nMonth, int nDay, int nHourOfDay, int nMinute, int nSecond)
	{
		cal.set(nYear, nMonth, nDay, nHourOfDay, nMinute, nSecond);
	}

	public void setDateDDDotMMDotYYYY(String csDD_MM_YYYY)
	{
		int nDay = NumberParser.getAsInt(csDD_MM_YYYY.substring(0, 2));
		int nMonth = NumberParser.getAsInt(csDD_MM_YYYY.substring(3, 5));
		nMonth--;	// Month is 0 based (January = 0)
		int nYear = NumberParser.getAsInt(csDD_MM_YYYY.substring(6, 10));
		cal.set(nYear, nMonth, nDay, 0, 0, 0);
	}
		
	public void setHourHHDotMMDotSS(String csHH_MM_SS)
	{
		int nHour = NumberParser.getAsInt(csHH_MM_SS.substring(0, 2));
		int nMinute = NumberParser.getAsInt(csHH_MM_SS.substring(3, 5));
		int nSecond = NumberParser.getAsInt(csHH_MM_SS.substring(6, 8));
		cal.set(1970, 0, 1, nHour, nMinute, nSecond);
	}	
	
	public long getTimeInMillis()
	{
		return cal.getTimeInMillis();
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
		int n = (cal.get(Calendar.YEAR) * 10000) + ((cal.get(Calendar.MONTH)+1) *100) + cal.get(Calendar.DAY_OF_MONTH);
		Integer iDate = Integer.valueOf(n);
		return iDate;
	}
	
	public String getDisplayableDateTime()
	{
		String cs = "" + StringUtil.FormatWithFill2LeftZero(getDay()) + "/" + StringUtil.FormatWithFill2LeftZero(getMonthBase1()) + "/" + StringUtil.FormatWithFill4LeftZero(getYear()) + "  " + StringUtil.FormatWithFill2LeftZero(getHour()) + ":" + StringUtil.FormatWithFill2LeftZero(getMinute()) + ":" + StringUtil.FormatWithFill2LeftZero(getSecond());
		return cs;
	}
	
	public int getYear()
	{
		return cal.get(Calendar.YEAR);
	}
	
	public int getMonth()
	{
		return cal.get(Calendar.MONTH);
	}
	
	public int getMonthBase1()
	{
		return cal.get(Calendar.MONTH)+1;
	}
	
	public int getDay()
	{
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	public int getDayOfWeek()
	{
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	public int getHour()
	{
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinute()
	{
		return cal.get(Calendar.MINUTE);
	}
	
	public int getSecond()
	{
		return cal.get(Calendar.SECOND);
	}
	
	public String toString()
	{
		return cal.toString();
	}
	
	private Calendar cal = null;
}
