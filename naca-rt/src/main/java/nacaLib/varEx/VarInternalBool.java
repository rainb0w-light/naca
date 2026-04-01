/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 28 juin 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import jlib.misc.AsciiEbcdicConverter;

/**
 * @author U930CV
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VarInternalBool extends Var
{
	private boolean isvalue = false ;
	/**
	 * @param declareTypeBase
	 */
	public VarInternalBool()
	{
		super(null);
		varDef = new VarDefInternalBool(this);
		varTypeId = varDef.getTypeId();
		isvalue = false;
	}

	/* (non-Javadoc)
	 * @see nacaLib.varEx.Var#allocCopy()
	 */
	protected VarBase allocCopy()
	{
		VarInternalBool v = new VarInternalBool();
		return v;
	}

	/* (non-Javadoc)
	 * @see nacaLib.varEx.VarBase#getAsLoggableString()
	 */
	protected String getAsLoggableString()
	{
		if (isvalue)
		{
			return "true" ;
		}
		else
		{
			return "false" ;
		}
	}

	/* (non-Javadoc)
	 * @see nacaLib.varEx.VarBase#hasType(nacaLib.varEx.VarTypeEnum)
	 */
	public boolean hasType(VarTypeEnum e)
	{
		return false;
	}

	public void set(int n)
	{
		if(n == 0)
			isvalue = false;
		else
			isvalue = true;
	}

	public void set(long l)
	{
		if(l == 0)
			isvalue = false;
		else
			isvalue = true;
	}

	public void set(String cs)
	{
		if(cs.equalsIgnoreCase("false") || cs.equalsIgnoreCase("0")) 
			isvalue = false;
		else
			isvalue = true;
	}

	public void set(boolean b)
	{
		isvalue = b ;
	}

	public boolean compareTo(boolean b)
	{
		return (isvalue && b) || (!isvalue && !b);
	}
	
	public boolean getBool()
	{
		return isvalue;
	}
	
	public int getInt()
	{
		if(isvalue)
			return 1;
		return 0;
	}
		
	public long getLong()
	{
		if(isvalue)
			return 1L;
		return 0L;
	}
	
	public double getDouble()
	{
		if(isvalue)
			return 1.0;
		return 0.0;
	}
	
	public String getString()
	{
		if(isvalue)
			return "1";
		return "0";
	}
	
	public String toString()
	{
		if(isvalue)
			return "true";
		return "false";
	}
	
	public int compareTo(int nValue)
	{
		int nVarValue = getInt();
		return nVarValue - nValue;
	}
	
	
	public int compareTo(double dValue)
	{
		double varValue = getDouble();
		double d = varValue - dValue;
		if(d < -0.00001)	//Consider epsilon precision at 10 e-5 
			return -1;
		else if(d > 0.00001)	//Consider epsilon precision at 10 e-5
			return 1;
		return 0;			
	} 
	

	protected byte[] convertUnicodeToEbcdic(char [] tChars)
	{
		return AsciiEbcdicConverter.noConvertUnicodeToEbcdic(tChars);
	}
	
	protected char[] convertEbcdicToUnicode(byte[] tBytes)
	{
		return AsciiEbcdicConverter.noConvertEbcdicToUnicode(tBytes);
	}
	

	public VarType getVarType()
	{
		return VarType.VarInternalBool;
	}
}
