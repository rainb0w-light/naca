/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 ao�t 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements.SQL;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSQLTableColDescriptor
{
	public CSQLTableColDescriptor()
	{
	}
	
	public void SetName(String csName)
	{
		csName = csName;
	}
	
	public String GetName()
	{
		return csName;
	}
	
	void SetLength(int n)
	{
		nLength = n;
		islengthSet = true;
	}	

	void SetDecimal(int n)
	{
		nDecimal = n;
		isdecimalSet = true;
	}
	
	public boolean HasSize()
	{
		return islengthSet;
	}
	
	public String GetSizes()
	{
		if(islengthSet)
		{
			if(isdecimalSet)
				return String.valueOf(nLength) + ", " + String.valueOf(nDecimal);
			return String.valueOf(nLength);
		}
		return "";
	}
	
	void SetType(String csType)
	{
		csType = csType;
	}
	
	public String GetType()
	{
		return csType;
	} 
	
	void SetNull(boolean b)
	{
		isnull = b;
	}		
	
	public boolean IsNull()
	{
		return isnull;
	}
	
	private int nLength = 0;
	private int nDecimal = 0;
	private boolean isdecimalSet = false;
	private boolean islengthSet = false;
	private String csType = "";
	private String csName = "";
	private boolean isnull = false;

}
