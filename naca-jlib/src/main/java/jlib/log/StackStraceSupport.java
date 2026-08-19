/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

/** Provides stack strace support behavior. */
public class StackStraceSupport
{
    /** Returns the call stack as string. */
    public static String getCallStackAsString()
    {
        Throwable th = new Throwable();
        StackTraceElement tStack[]  = th.getStackTrace();

        StringBuilder sb = new StringBuilder();
        for (int i=0; i<tStack.length; i++)
        {
            StackTraceElement te = tStack[i] ;
            sb.append(te.toString() + "\r\n");
        }
        return sb.toString();
    }

    /** Returns the as string. */
    public static String getAsString(StackTraceElement stack[])
    {
        String cs = new String();
        for(int n=0; n<stack.length; n++)
        {
            StackTraceElement item = stack[n];
            cs += item.toString() + ";";
        }

        return cs;
    }

    /** Returns the file line at stack depth. */
    public static String getFileLineAtStackDepth(int nDepth)
    {
        Throwable th = new Throwable();
        StackTraceElement tStack[]  = th.getStackTrace();
        if(nDepth < tStack.length)
        {
            StackTraceElement item = tStack[nDepth];
            String cs = item.toString();
            return cs;
        }
        return "";
    }

    /** Returns the file line at first app call. */
    public static String getFileLineAtFirstAppCall()
    {
        Throwable th = new Throwable();
        StackTraceElement tStack[]  = th.getStackTrace();
        for(int nDepth=1; nDepth<tStack.length; nDepth++)
        {
            StackTraceElement item = tStack[nDepth];
            String cs = item.getClassName().toLowerCase();
            if (cs.indexOf("naca") == -1 && cs.indexOf("jlib") == -1) {
                return item.toString();
            }
        }
        return "";
    }
}
