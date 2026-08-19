/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

/**
 * @author PJD
 *
 */
public class Asserter
{
    static private boolean ms_ActivateAssert = false ;

    /** Executes the assert always operation. */
    static public void assertAlways(String csMessage)
    {
        throw new AssertException(csMessage);
    }

    static public void setAssertActive(boolean b)
    {
        ms_ActivateAssert = b ;
    }

    /** Executes the assert if null operation. */
    static public void assertIfNull(Object o)
    {
        if (ms_ActivateAssert)
        {
            if (o == null)
            {
                AssertException exp = new AssertException("AssertIsNull");
                throw exp ;
            }
        }
    }

    /** Executes the assert if not null operation. */
    static public void assertIfNotNull(Object o)
    {
        if (ms_ActivateAssert)
        {
            if (o != null)
            {
                AssertException exp = new AssertException("AssertIsNotNull");
                throw exp ;
            }
        }
    }

    /** Executes the assert if empty operation. */
    static public void assertIfEmpty(String cs)
    {
        if (ms_ActivateAssert)
        {
            if (cs.equals(""))
            {
                AssertException exp = new AssertException("AssertStringIsEmpty");
                throw exp ;
            }
        }
    }

    /** Executes the assert if false operation. */
    static public void assertIfFalse(boolean b)
    {
        if (ms_ActivateAssert)
        {
            if (!b)
            {
                AssertException exp = new AssertException("AssertTestIfFalse");
                throw exp ;
            }
        }
    }

    /** Executes the assert if false operation. */
    static public void assertIfFalse(boolean b, String csReason)
    {
        if (ms_ActivateAssert)
        {
            if (!b)
            {
                AssertException exp = new AssertException(csReason);
                throw exp ;
            }
        }
    }

    /** Executes the assert if different operation. */
    static public void assertIfDifferent(String a, String b)
    {
        if (ms_ActivateAssert)
        {
            if (!a.equals(b))
            {
                AssertException exp = new AssertException("assertIfDifferent");
                throw exp ;
            }
        }
    }

    /** Executes the assert if equals operation. */
    static public void assertIfEquals(String a, String b)
    {
        if (ms_ActivateAssert)
        {
            if (a.equals(b))
            {
                AssertException exp = new AssertException("assertIfDifferent");
                throw exp ;
            }
        }
    }

    /** Executes the assert if different operation. */
    static public void assertIfDifferent(int a, int b)
    {
        if (ms_ActivateAssert)
        {
            if(a != b)
            {
                AssertException exp = new AssertException("assertIfDifferent");
                throw exp ;
            }
        }
    }

    /** Executes the assert if different operation. */
    static public void assertIfDifferent(double a, double b)
    {
        if (ms_ActivateAssert)
        {
            if(a < b - 0.0001 || a > b + 0.0001)
            {
                AssertException exp = new AssertException("assertIfDifferent");
                throw exp ;
            }
        }
    }

}
