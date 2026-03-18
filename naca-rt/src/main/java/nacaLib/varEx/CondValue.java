/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 26 nov. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author PJD
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.varEx;

//import nacaLib.program.Var;

public class CondValue
{
	CondValue(String sMin, String sMax)
	{
		sMin = sMin;
		sMax = sMax;
		bInterval = true;
		constant = null;
	}

	CondValue(String sValue)
	{
		sMin = sValue;
		bInterval = false;
		constant = null;
	}
	
	CondValue(CobolConstantBase constant)
	{
		constant = constant;
		bInterval = false;
	}
	
	public boolean is(Var v)
	{
		if(constant != null)
			return v.is(constant);
		else
		{
			if(bInterval)
			{
				if(v.compareTo(ComparisonMode.Unicode, sMin) >= 0 && v.compareTo(ComparisonMode.Unicode, sMax) <= 0)
					return true;
			}
			if(v.equals(sMin))
				return true;
		}
		return false;		  
	}
	
	public String getMin()
	{
		if(constant == null)			
			return sMin;
		return null;
	}
	
	public String toString()
	{
		if(constant != null)		
			return constant.getSTCheckValue();		
		if(bInterval)
			return "[" + sMin + "," + sMax + "]";
		return sMin;
	}
	
	private CobolConstantBase constant = null;
	private String sMin = null; 
	private String sMax = null;
	private boolean bInterval = false;
}