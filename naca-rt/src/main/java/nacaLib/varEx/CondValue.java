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
	CondValue(String min, String max)
	{
		this.min = min;
		this.max = max;
		isinterval = true;
		constant = null;
	}

	CondValue(String sValue)
	{
		this.min = sValue;
		isinterval = false;
		constant = null;
	}

	CondValue(CobolConstantBase constant)
	{
		this.constant = constant;
		isinterval = false;
	}
	
	public boolean is(Var v)
	{
		if(constant != null)
			return v.is(constant);
		else
		{
			if(isinterval)
			{
				if(v.compareTo(ComparisonMode.Unicode, min) >= 0 && v.compareTo(ComparisonMode.Unicode, max) <= 0)
					return true;
			}
			if(v.equals(min))
				return true;
		}
		return false;		  
	}
	
	public String getMin()
	{
		if(constant == null)			
			return min;
		return null;
	}
	
	public String toString()
	{
		if(constant != null)		
			return constant.getSTCheckValue();		
		if(isinterval)
			return "[" + min + "," + max + "]";
		return min;
	}
	
	private CobolConstantBase constant = null;
	private String min = null;
	private String max = null;
	private boolean isinterval = false;
}