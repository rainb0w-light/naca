/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.base;

import jlib.log.Asserter;

public class CJMapObject //extends BaseObject
{
	protected CJMapObject()
	{
	}
	
	public static void setAssertActive(boolean b)
	{
		Asserter.setAssertActive(b);
	}
	
	public static void Assert(String csMessage)
	{
		Asserter.assertAlways(csMessage);
	}
	
	protected void assertIfNull(Object o)
	{
		Asserter.assertIfNull(o);
	}
	
	protected void assertIfNotNull(Object o)
	{
		Asserter.assertIfNotNull(o);
	}
	
	protected void assertIfEmpty(String cs)
	{
		Asserter.assertIfEmpty(cs);
	}

    protected void assertIfFalse(boolean b)
	{
        if (nacaLib.testSupport.TestAssertionCollector.isCollecting()) {
            // Capture stack trace to find the calling line
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            String callingLocation = "unknown";
            String expectedValue = "true";
            String actualValue = String.valueOf(b);

            // Look for the calling method in the stack
            for (int i = 2; i < stack.length; i++) {
                StackTraceElement elem = stack[i];
                String className = elem.getClassName();
                // Find first non-library caller
                if (!className.startsWith("nacaLib.base.CJMapObject") &&
                    !className.startsWith("nacaLib.testSupport")) {
                    callingLocation = className + "." + elem.getMethodName()
                        + "(" + elem.getFileName() + ":" + elem.getLineNumber() + ")";
                    break;
                }
            }

            nacaLib.testSupport.TestAssertionCollector.addResult(
                b,
                "Assertion failed at " + callingLocation,
                expectedValue,
                actualValue
            );
        } else {
            Asserter.assertIfFalse(b);
        }
	}
	
	protected void assertIfFalse(boolean b, String csReason)
	{
		Asserter.assertIfFalse(b, csReason);
	}
	
	protected void assertIfDifferent(String a, String b)
	{
		if (nacaLib.testSupport.TestAssertionCollector.isCollecting()) {
			nacaLib.testSupport.TestAssertionCollector.assertEquals(a, b, "Assertion failed");
		} else {
			Asserter.assertIfDifferent(a, b);
		}
	}

	protected void assertIfEquals(String a, String b)
	{
		if (nacaLib.testSupport.TestAssertionCollector.isCollecting()) {
			nacaLib.testSupport.TestAssertionCollector.assertEquals(a, b, "Assertion failed");
		} else {
			Asserter.assertIfEquals(a, b);
		}
	}

	protected void assertIfDifferent(int a, int b)
	{
		if (nacaLib.testSupport.TestAssertionCollector.isCollecting()) {
			nacaLib.testSupport.TestAssertionCollector.assertEquals(a, b, "Assertion failed");
		} else {
			Asserter.assertIfDifferent(a, b);
		}
	}

	protected void assertIfDifferent(double a, double b)
	{
		if (nacaLib.testSupport.TestAssertionCollector.isCollecting()) {
			nacaLib.testSupport.TestAssertionCollector.assertEquals(a, b, "Assertion failed");
		} else {
			Asserter.assertIfDifferent(a, b);
		}
	}
	
	public static final boolean isLogCESM = false;
	public static final boolean isLogFlow = false;
	public static final boolean isLogSql = false;
	public static final boolean IsSTCheck = false;
}
