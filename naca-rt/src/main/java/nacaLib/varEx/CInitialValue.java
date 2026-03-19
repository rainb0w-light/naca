/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
// InitialValue.java

package nacaLib.varEx;


public class CInitialValue
{
	public CInitialValue(int n, boolean bFill)
	{
		genericValue = new GenericValueInt(n);
		this.bFill = bFill;
	}

	public CInitialValue(double d, boolean bFill)
	{
		genericValue = new GenericValueDouble(d);
		this.bFill = bFill;
	}

	public CInitialValue(String csValue, boolean bFill)
	{
		genericValue = new GenericValueString(csValue);
		this.bFill = bFill;
	}


	public CInitialValue(char c, boolean bFill)
	{
		genericValue = new GenericValueChar(c);
		this.bFill = bFill;
	}
	
//	public void apply()
//	{
//		if(var != null)
//		{
//			if(!bFill)
//			{
//				if(sValue != null)
//					var.set(sValue);
//				else 
//					var.set(c);
//			}
//			else
//			{
//				if(sValue != null)
//					var.varManager.setAndFillWithType(sValue);
//				else 
//					var.varManager.setAndFillWithType(c);
//			}
//		}
//	}
	
	public String toString()
	{
		return "GenericValue="+genericValue.getAsString() + " bFill="+bFill;
	}
	
	GenericValue genericValue;
	boolean bFill = false;
}

