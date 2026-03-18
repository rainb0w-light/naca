/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 30 nov. 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.mapSupport;

public class MapFieldFlag
{
	public MapFieldFlag()
	{
	}
	
	public MapFieldFlag duplicate()
	{
		MapFieldFlag copy = new MapFieldFlag();
		copy.csValue = csValue;
		return copy;
	}
	
	public void set(String cs)
	{
		csValue = cs;
	}

	public void set(char c)
	{
		if (c == 0)
		{
			csValue = null ;
		}
		else
		{
			csValue = new String(Character.toString(c));
		}
	}
	
	public String get()
	{
		if (csValue != null)
		{
			return csValue;
		}
		else
		{
			return "" ;
		}
	}
	
	public boolean isFlag(String cs)
	{
		if (csValue == null)
		{
			return false ;
		}
		return csValue.equals(cs);
	}
	
	public char getEncodedValue()
	{
		if(csValue!=null && csValue.length() >= 1)
			return csValue.charAt(0);
		return 0; 
	}

	public void setEncodedValue(char cEncodedValue)
	{
		set(cEncodedValue);
	}
	
	private String csValue = null;

	/**
	 * @return
	 */
	public boolean isSet()
	{
		return csValue != null ;
	}

	/**
	 * 
	 */
	public void reset()
	{
		csValue = null ;		
	}
}
