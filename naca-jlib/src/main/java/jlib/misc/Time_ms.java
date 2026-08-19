/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Time_ms
{
    public static long getCurrentTime_ms()
    {
        //long l = new GregorianCalendar().getTimeInMillis();
        long l = System.currentTimeMillis();
        return l;
    }

    public static String formatHHMMSS_ms(long l)
    {
        int nNbMs = (int)(l % 1000);
        l = l / 1000;
        int nNbS = (int)(l % 60);
        l = l / 60;
        int nNbMin = (int)(l % 60);
        int nNbH = (int)(l / 60);
        String cs = nNbH + ":" + nNbMin + ":" + nNbS + "." + nNbMs;
        return cs;
    }

    public static String formatYYYYMMDDHHMMSS_ms(long l)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(l);

        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String cs = formatter.format(date) ;
        return cs ;
    }

    public static String formatDMY_HHMMSS_ms(long l)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(l);

        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
        String cs = formatter.format(date) ;
        return cs ;
    }

    public static void wait_ms(long lNbMs)
    {
        try
        {
            Thread.sleep(lNbMs);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
